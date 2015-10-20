/**
 * Controller for Maker page
 */
app.controller('product', [
		'$scope',
		'$http',
		'$q',
		'$interval',
		'$filter',
		function($scope, $http, $q, $interval, $filter) {
			
			$scope.deletedRows = [];
			$scope.selectedProduct = undefined;
			$scope.gridProduct = {
					enableRowSelection : true,
					enableSelectAll : true
					};

			/**
			 * Define the columns for the product grid
			 */
			$scope.gridProduct.columnDefs = [ {
				name : 'id',
				enableCellEdit : false
			}, {
				name : 'name',
				displayName : 'Name'
			}, {
				name : 'price',
				displayName : 'Price',
				type : 'number'
			}, {
				name : 'wasChecked',
				displayName : 'Was checked',
				type : 'boolean',
				enableCellEdit : false
			} ];

			/**
			 * UI-Grid function to be called whenever a row in the product grid gets saved
			 */
			$scope.saveProductRow = function(rowEntity) {
				
				var promise = $q.defer();
				rowEntity.wasChecked = false;
				$scope.gridProductApi.rowEdit.setSavePromise(rowEntity,
						promise.promise);
				
				promise.resolve();

			};

			/**
			 * Function to add a new row in the Product table
			 */
			$scope.addData = function() {
				var n = $scope.gridProduct.data.length + 1;
				$scope.gridProduct.data.push({
					"name" : "Product " + n,
					"price" : 0,
					"wasChecked" : false,
					"active" : true
				});
			};
			
			/**
			 * Utility function to check if an array contains an object
			 * 
			 * @param arr the array
			 * @param obj the object to search for
			 */
			$scope.contains = function(arr, obj) {
			    var i = arr.length;
			    while (i--) {
			       if (arr[i] === obj) {
			           return true;
			       }
			    }
			    return false;
			}
			
			/**
			 * Function to delete the selected rows from the Product Grid.
			 */
			$scope.deleteRows = function() {
				var selectedRows = $scope.gridProductApi.selection.getSelectedRows();
				var tableRows = $scope.gridProduct.data;
				
				$scope.deletedRows = [];
				
				for (var i = tableRows.length - 1; i >= 0;i--)
				{
					var row = tableRows[i];
					
					if ($scope.contains(selectedRows, row))
					{
						row.active = false;
						row.wasChecked = false;
						$scope.deletedRows.push(row);
						$scope.gridProduct.data.splice(i,1);
					}
					
				}
			}
			
			/**
			 * Function called when submitting the "maker" changes
			 */
			$scope.submit = function() {
				var tableData = $scope.gridProduct.data;
				tableData = tableData.concat($scope.deletedRows);
				$http.post('http://localhost:8080/rest/product/batch', tableData)
				.success(function(data) {
					alert("success");
				});
			}

			/**
			 * Set-up of Product grid.
			 * 
			 * Setting the saveRow callback, the multiSelect features 
			 * and row selection changed callback event.
			 */
			$scope.gridProduct.onRegisterApi = function(gridProductApi) {
				// set gridProductApi on scope
				$scope.gridProductApi = gridProductApi;
				gridProductApi.rowEdit.on.saveRow($scope, $scope.saveProductRow);
				$scope.gridProductApi.selection.setMultiSelect(false);
				$scope.gridProductApi.selection.on.rowSelectionChanged($scope,
						function(row){
					
						if (row.isSelected)
						{
							$scope.selectedProduct = row.entity;
							var filteredDetails = $filter('filterActiveProductDetails')(row.entity.productDetails);
							$scope.gridProductDetails.data = filteredDetails;							
						}
						else
						{
							$scope.selectedProduct = undefined;
							$scope.gridProductDetails.data = [];
						}										
					});
			};

			$http.get('http://localhost:8080/rest/product/maker').success(
					function(data) {
						$scope.gridProduct.data = data;
					});
			
			
			$scope.gridProductDetails = {

					};

			/**
			 * Define the Product Details grid columns
			 */
			$scope.gridProductDetails.columnDefs = [ {
				name : 'id',
				enableCellEdit : false
			}, {
				name : 'storeName',
				displayName : 'Store Name'
			}, {
				name : 'expirationDate',
				displayName : 'Expiration Date',
				cellFilter: 'date:\'yyyy-MM-dd\'',
				type : 'date'
			}, {
				name : 'wasChecked',
				displayName : 'Was checked',
				type : 'boolean',
				enableCellEdit : false
			} ];
			
			/**
			 * Function to add a new row in the Product Details grid. 
			 * Add a new Product detail to the currently selected Product.
			 */
			$scope.addDataDetails = function() {
				var n = $scope.gridProductDetails.data.length + 1;
				var selectedRows = $scope.gridProductApi.selection.getSelectedRows();
				if ($scope.selectedProduct !== undefined)
				{
					var now = new Date();
					var newRow = {
							"storeName" : "Store " + n,
							"expirationDate" : $filter('date')(now, 'yyyy-MM-dd'),
							"wasChecked" : false,
							"active" : true
						};
					$scope.gridProductDetails.data.push(newRow);
					
					if ($scope.selectedProduct.productDetails === undefined)
					{
						$scope.selectedProduct.productDetails = [];
					}
					$scope.selectedProduct.productDetails = $scope.gridProductDetails.data;
					$scope.selectedProduct.wasChecked = false;
				}
				
			};
			
			/**
			 * Delete a product details row from the selected Product. 
			 * Deleting means to set active flag to false. 
			 * It also need to be approved by the checker so it sets the wasChecked flag to false 
			 * for the Product Details object.
			 */
			$scope.logicalDeleteFromProductDetails = function(storeName)
			{
				var prodDetailsLength = $scope.selectedProduct.productDetails.length;
				for (var i = 0; i < prodDetailsLength; i++)
				{
					var prodDetails = $scope.selectedProduct.productDetails[i];
					if (prodDetails.storeName == storeName)
					{
						prodDetails.active = false;
						prodDetails.wasChecked = false;
					}
				}
			}
			
			/**
			 * Delete all selected Product Details rows
			 */
			$scope.deleteRowsDetails = function() {
				var selectedRows = $scope.gridProductDetailsApi.selection.getSelectedRows();
				var tableRows = $scope.gridProductDetails.data;
				
				
				for (var i = tableRows.length - 1; i >= 0;i--)
				{
					var row = tableRows[i];
					
					if ($scope.contains(selectedRows, row))
					{
						$scope.logicalDeleteFromProductDetails(row.storeName);
						
						$scope.gridProductDetails.data = $filter('filterActiveProductDetails')($scope.gridProductDetails.data);
						
						if ($scope.selectedProduct !== undefined)
						{
							$scope.selectedProduct.wasChecked = false;
						}
					}
					
				}
			}
			
			/**
			 * UI-Grid function to be called whenever a row in the product details grid gets saved
			 */
			$scope.saveProductDetailsRow = function(rowEntity) {
				var promise = $q.defer();
				rowEntity.wasChecked = false;
				$scope.gridProductDetailsApi.rowEdit.setSavePromise(rowEntity,
						promise.promise);
				
				// get selected row from product table
				var selectedRows = $scope.gridProductApi.selection.getSelectedRows();
				selectedRows[0].wasChecked = false;
				
				promise.resolve();

			};
			
			/**
			 * Set-up of Product Details grid.
			 * 
			 * Setting the saveRow callback, the multiSelect features 
			 * and row selection changed callback event.
			 */
			$scope.gridProductDetails.onRegisterApi = function(gridProductDetailsApi) {
				// set gridProductApi on scope
				$scope.gridProductDetailsApi = gridProductDetailsApi;
				gridProductDetailsApi.rowEdit.on.saveRow($scope, $scope.saveProductDetailsRow);
				$scope.gridProductDetailsApi.selection.setMultiSelect(false);
			};
			
			
		} ])
		.filter('filterActiveProductDetails', [function($filter) {
			 return function(inputArray){         
				       
				  var data=[];
				  angular.forEach(inputArray, function(item){             
				   if(item.active == true){
				     data.push(item);
				   }
				  });      
				  return data;
				 };
				}]);
