app.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/index");

    // Now set up the states
    $stateProvider        
        .state('index', {
            url: "",
            controller: ['$state', function($state) {
                $state.go('product');
            }]
        })
        .state('product', {
            url: "/product",
            templateUrl: "js/products/product.html"
        })
        .state('product-checker', {
            url: "/product-checker",
            templateUrl: "js/products/product-checker.html"
        })
    ;

});
