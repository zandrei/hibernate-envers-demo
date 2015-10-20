var indexOf = function(needle) {
	if (typeof Array.prototype.indexOf === 'function') {
		indexOf = Array.prototype.indexOf;
	} else {
		indexOf = function(needle) {
			var i = -1, index = -1;

			for (i = 0; i < this.length; i++) {
				if (this[i] === needle) {
					index = i;
					break;
				}
			}

			return index;
		};
	}

	return indexOf.call(this, needle);
};

app.filter('approvedBool', function () {
    return function(input) {
        switch(input) {
        case undefined:
            return false;
        default :
            return input;
        }
    };
});

app.controller(
		'productChecker',
		[
				'$scope',
				'$http',
				'$q',
				'$interval',
				function($scope, $http, $q, $interval) {
					$scope.selectedProduct = {};
					
					$scope.deleteProdDetailsPromise = null;
					$scope.gridProduct = {
						enableRowSelection : true,
						enableSelectAll : true,
					};

					/**
					 * row template for grids
					 */
					var rowtpl = '<div ng-class="{\'green\':row.entity.operation==\'ADD\', \'blue\':(row.entity.operation==\'MOD\' && row.entity.changes.active==true) , \'red\':(row.entity.operation==\'MOD\' && row.entity.changes.active==false) }"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader }" ui-grid-cell></div></div>';
					
					/*
					 * function called when accepting row changes for a product (add or remove for a product).
					 * It will cascade the accept for each product detail.
					 */
					$scope.acceptedOnClick = function(row)
					{
						if (!!row.accepted)
						{
							angular.forEach(row.productDetails, function(item){             
								   item.accepted = false;
								  });
						}
						
					}
					
					/**
					 * Product grid definition
					 */
					$scope.gridProduct.columnDefs = [
							{
								name : 'accepted',
								displayName : 'Accept row changes',
								enableCellEdit : true,
								cellEditableCondition : function($scope) {
									return ($scope.row.entity.operation != 'MOD' || $scope.row.entity.changes.active == false);
								},
								/**
								 * if the row had individual property changes show a tooltip if trying to accept changes at row level. 
								 */
								cellTooltip : function(row, col) {
									if (row.entity.operation == 'MOD' && row.entity.changes.active == true)
										return 'You must accept changes individually!';
									return null;
								},
								cellFilter: 'approvedBool',
								cellTemplate: '<input type="checkbox" ng-disabled="row.entity.operation == \'MOD\' && row.entity.changes.active == true" ng-model="row.entity.accepted" ng-click="$event.stopPropagation();grid.appScope.acceptedOnClick(row.entity)">',										
								type : 'boolean'
							},
							{
								name : 'id',
								enableCellEdit : false,
								field : 'changes.id'
							},
							{
								name : 'name',
								displayName : 'Name',
								enableCellEdit : false,
								cellTooltip : function(row, col) {
									if (row.entity.initial !== null
											&& row.entity.initial.name !== null)
										return 'Previous value: '
												+ row.entity.initial.name;
									return null;
								},
								field : 'changes.name'
							},
							{
								name : 'name_accept',
								displayName : 'Accept change',
								cellEditableCondition : function($scope) {
									return ($scope.row.entity.operation == 'MOD' && $scope.row.entity.changes.active == true);
								},
								cellFilter: 'approvedBool',
								type : 'boolean'
							},
							{
								name : 'price',
								displayName : 'Price',
								enableCellEdit : false,
								type : 'number',
								cellTooltip : function(row, col) {
									if (row.entity.initial !== null
											&& row.entity.initial.price !== null)
										return 'Previous value: '
												+ row.entity.initial.price;
									return null;
								},
								field : 'changes.price'
							},
							{
								name : 'price_accept',
								displayName : 'Accept change',
								cellEditableCondition : function($scope) {
									return ($scope.row.entity.operation == 'MOD' && $scope.row.entity.changes.active == true);
								},
								cellFilter: 'approvedBool',
								type : 'boolean'
							}, {
								name : 'wasChecked',
								displayName : 'Was checked',
								type : 'boolean',
								enableCellEdit : false,
								field : 'changes.wasChecked'
							} ];

					$scope.gridProduct.rowTemplate = rowtpl;										

					$scope.saveRow = function(rowEntity) {

						var promise = $q.defer();
						$scope.gridProductApi.rowEdit.setSavePromise(
								rowEntity, promise.promise);
						
						if (rowEntity.accepted == true)
						{
							rowEntity.changes.wasChecked = true;
						}

						//if product name change was not checked, revert to initial value
						if (rowEntity.name_accept != true && rowEntity.initial !== null) {
							rowEntity.changes.name = rowEntity.initial.name;
						}

						//if product price change was not checked, revert to initial value
						if (rowEntity.price_accept != true && rowEntity.initial !== null) {
							rowEntity.changes.price = rowEntity.initial.price;
						}

						promise.resolve();
					};

					$scope.gridProduct.onRegisterApi = function(
							gridProductApi) {
						// set gridProductApi on scope
						$scope.gridProductApi = gridProductApi;
						$scope.gridProductApi.selection.setMultiSelect(false);
						gridProductApi.rowEdit.on.saveRow($scope, $scope.saveRow);
						$scope.gridProductApi.selection.on
								.rowSelectionChanged(
										$scope,
										function(row) {

											if (row.isSelected) {
												$scope.selectedProduct = row;														
												$scope.gridProductDetails.data = row.entity.productDetails;	
												$scope.gridProductDetailsApi.core.refresh();
											} else {
												$scope.gridProductDetails.data = [];
												$scope.selectedProduct = undefined;
											}
										});
					};

					$scope.contains = function(arr, obj) {
						var i = arr.length;
						while (i--) {
							if (arr[i] === obj) {
								return true;
							}
						}
						return false;
					}
					
					$scope.cascadeSubmitToChildren = function(row)
					{
						var noDetails = row.productDetails.length;
						var detailRowsToBeDeleted = [];
						var productAccepted = row.accepted;
														
						for (var i = 0 ;i < noDetails; i++)
						{
							var productDetails = row.productDetails[i];
							//if the product operation is a logical REMOVE and the checker has accepted the changes on the product 
							//then cascade to product details
							if ((row.operation == 'MOD' && row.changes.active != true)
									&& productAccepted == true)
							{
								productDetails.changes.wasChecked = true;
								productDetails.accepted = true;
								continue;
							}
							
							if (row.operation == 'ADD')
							{
								//if the product details row was newly added but the checker did not accept the change delete the row from database
								if (productDetails.operation == 'ADD' && productDetails.accepted != true)
								{
									detailRowsToBeDeleted.push(productDetails.changes);
									continue;
								}
							}
							
							//if modify operation was performed on product row
							if (row.operation == 'MOD')
							{
								productDetails.changes.wasChecked = true;																			
								
								//if the product details row was newly added but the checker did not accept the change delete the row from database
								if (productDetails.operation == 'ADD' && productDetails.accepted != true)
								{
									detailRowsToBeDeleted.push(productDetails.changes);
									continue;
								}
								
								if (productDetails.operation == 'MOD')
								{
									// if the product details row was deleted but the checker did not accept the change
									if (productDetails.changes.active != true
											&& productDetails.accepted != true)
									{
										// undo deletion
										productDetails.changes.active = true;
										continue;
									}
									else
									{
										//a product details row had modifications
										//check if storeName change was accepted
										if (productDetails.storeName_accept != true  && productDetails.initial !== null) {
											productDetails.changes.storeName = productDetails.initial.storeName;
										}

										//check if expirationDate change was accepted
										if (productDetails.expirationDate_accept != true && productDetails.initial !== null) {
											productDetails.changes.expirationDate = productDetails.initial.expirationDate;
										}	
									}
								}
							}									
						}
						
						var rowsToBePersisted = [];
						
						for (var i = 0; i < row.productDetails.length; i++)
						{
							var change = row.productDetails[i];
							if (change.operation == 'ADD' && change.accepted != true)
							{
								console.log("row will be deleted");
							}
							else
							{
								rowsToBePersisted.push(change.changes);
							}
							
						}
						
						$http
							.post(
									'http://localhost:8080/rest/product-details/batch',
									rowsToBePersisted).success(
									function(data) {
										console.log("Persisted product details");
									});
						
						if (detailRowsToBeDeleted.length > 0 )
						{
							$scope.deleteProdDetailsPromise = $http
								.post(
										'http://localhost:8080/rest/product-details/batch-remove',
										detailRowsToBeDeleted);
						}
					}

					$scope.submit = function() {
						var tableData = $scope.gridProduct.data;
						var selectedRows = $scope.gridProductApi.selection
								.getSelectedRows();
						var persistedRows = [];
						var deleteRows = []

						for ( var i = 0; i < tableData.length; i++) {
							
							var row = tableData[i];
							row.changes.wasChecked = true;

							$scope.cascadeSubmitToChildren(row);
							// if add operation and we selected the
							// added row then add the current row to
							// persistant changes
							if (row.operation == 'ADD'
									&& row.accepted == true) {										
								persistedRows.push(row.changes);
							} else if (row.operation == 'MOD') {
								// if it is an update operation
								if (row.changes.active == false) {
									// if on the maker side the row was
									// deleted (active = false)
									if (row.accepted == false) {
										// if the checker did not select
										// the "remove" change submitted
										// by the checker
										row.changes.active = true;
									}
								}

								persistedRows.push(row.changes);

							} else {
								deleteRows.push(row.changes);
							}
						}
						$http
								.post(
										'http://localhost:8080/rest/product/batch',
										persistedRows).success(
										function(data) {
											alert("success");
										});
						
						if ($scope.deleteProdDetailsPromise !== null)
						{
							$scope.deleteProdDetailsPromise.success(function(data) {
								
								$http
								.post(
										'http://localhost:8080/rest/product/batch-remove',
										deleteRows).success(
										function(data) {
										});
								
							});
						}
						else
						{
							$http
							.post(
									'http://localhost:8080/rest/product/batch-remove',
									deleteRows).success(
									function(data) {
									});
						}
					}

					$http
							.get(
									'http://localhost:8080/rest/product/checker')
							.success(function(data) {
								$scope.gridProduct.data = data;
							});

					$scope.gridProductDetails = {
						enableRowSelection : false,
						enableSelectAll : false,								
					};

					var rowtpl = '<div ng-class="{\'green\':row.entity.operation==\'ADD\', \'blue\':(row.entity.operation==\'MOD\' && row.entity.changes.active==true) , \'red\':(row.entity.operation==\'MOD\' && row.entity.changes.active==false) }"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader }" ui-grid-cell></div></div>';
					
					

					$scope.gridProductDetails.columnDefs = [
							{
								name : 'accepted',
								displayName : 'Accept row changes',
								enableCellEdit : true,
								cellEditableCondition : function($localscope) {
									var selectedProduct = $scope.gridProductApi.selection
											.getSelectedRows()[0];
									if (selectedProduct.operation == 'ADD' && selectedProduct.accepted != true)
									{
										alert("Please approve the changes on the parent row first !");
										return false;
									}
									return !($localscope.row.entity.operation == 'MOD' && $localscope.row.entity.changes.active == true);
								},
								cellTooltip : function(row, col) {
									if (row.entity.operation == 'MOD' && row.entity.changes.active == true)
									{
										return 'You must accept changes individually!';
									}
										
									return null;
								},
								//cellTemplate: '<input type="checkbox" ng-model="row.entity.accepted" ng-click="$event.stopPropagation();">'
								cellFilter: 'approvedBool',
								//cellTemplate: '<div  ng-click="grid.appScope.onClick(cell)" ng-bind="row.getProperty(col.field)"></div>',
								type : 'boolean'
							},
							{
								name : 'id',
								enableCellEdit : false,
								field : 'changes.id'
							},
							{
								name : 'storeName',
								displayName : 'Store Name',
								enableCellEdit : false,
								cellTooltip : function(row, col) {
									if (row.entity.initial !== null
											&& row.entity.initial.storeName !== null)
										return 'Previous value: '
												+ row.entity.initial.storeName;
									return null;
								},
								field : 'changes.storeName'
							},
							{
								name : 'storeName_accept',
								displayName : 'Accept change',
								cellEditableCondition : function($scope) {
									return ($scope.row.entity.operation == 'MOD' && $scope.row.entity.changes.active == true);
								},
								cellFilter: 'approvedBool',
								type : 'boolean'
							},
							{
								name : 'expirationDate',
								displayName : 'Expiration Date',
								enableCellEdit : false,
								type : 'date',
								cellFilter: 'date:\'yyyy-MM-dd\'',
								cellTooltip : function(row, col) {
									if (row.entity.initial !== null
											&& row.entity.initial.expirationDate !== null)
										return 'Previous value: '
												+ row.entity.initial.expirationDate;
									return null;
								},
								field : 'changes.expirationDate'
							},
							{
								name : 'expirationDate_accept',
								displayName : 'Accept change',
								cellEditableCondition : function($scope) {
									return ($scope.row.entity.operation == 'MOD' && $scope.row.entity.changes.active == true);
								},
								cellFilter: 'approvedBool',
								type : 'boolean'
							}, {
								name : 'wasChecked',
								displayName : 'Was checked',
								type : 'boolean',
								enableCellEdit : false,
								field : 'changes.wasChecked'
							} ];

					$scope.gridProductDetails.rowTemplate = rowtpl;

					$scope.submitDetails = function() {
						var selectedProduct = $scope.gridProductApi.selection
								.getSelectedRows()[0];
						if (selectedProduct.operation == 'ADD') {
							alert('Add operations for product rows can only be saved along with their product details!');
							return;
						}

						$scope.submitProductDetailsChanges();
					}
					
					$scope.saveProductDetailsRow = function(rowEntity)
					{
						var promise = $q.defer();
						$scope.gridProductDetailsApi.rowEdit.setSavePromise(
								rowEntity, promise.promise);
						
						if (rowEntity.acceped == true)
						{
							rowEntity.changes.wasChecked = true;
						}

						if (rowEntity.storeName_accept != true && rowEntity.initial !== null) {
							rowEntity.changes.storeName = rowEntity.initial.storeName;
						}

						if (rowEntity.expirationDate_accept != true && rowEntity.initial !== null) {
							rowEntity.changes.expirationDate = rowEntity.initial.expirationDate;
						}
						
						rowEntity.changes.wasChecked = true;
						
						if ($scope.selectedProduct !== undefined)
						{
							$scope.selectedProduct.wasChecked = true;
						}

						promise.resolve();
					}

					$scope.gridProductDetails.onRegisterApi = function(
							gridProductDetailsApi) {
						// set gridProductApi on scope
						$scope.gridProductDetailsApi = gridProductDetailsApi;
						gridProductDetailsApi.rowEdit.on.saveRow($scope, $scope.saveProductDetailsRow);
						
					};
				} ]);
