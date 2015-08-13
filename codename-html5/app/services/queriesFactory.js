(function () {
    var $queries = function ($http, $cookieStore, $transformRequestToForm, appConstants) {
        var factory = {};

        //GET ALL
        factory.getByLocation = function (lon,lat, interests,lookingFors, categories) {
            return $http({
                method: 'GET',
                url: appConstants.server + appConstants.context + 'rest/query/allbylocation?lon='+lon+'&lat='+lat+'&interests='+JSON.stringify(interests)+'&lookingFors='+JSON.stringify(lookingFors)+'&categories='+JSON.stringify(categories),
                headers: {'Content-Type': 'application/x-www-form-urlencoded', service_key: 'webkey:' + $cookieStore.get('email'), auth_token: $cookieStore.get('auth_token')},
                
            });
        };

        //GET ALL
        factory.getAll = function (interests, lookingFors, categories) {
            return $http({
                method: 'GET',
                url: appConstants.server + appConstants.context + 'rest/query/all?interests='+JSON.stringify(interests) +'&lookingFors=' +JSON.stringify(lookingFors) +'&categories=' + JSON.stringify(categories),
                headers: {'Content-Type': 'application/x-www-form-urlencoded', service_key: 'webkey:' + $cookieStore.get('email'), auth_token: $cookieStore.get('auth_token')},
                
            });
        };

        return factory;
    };

    $queries.$inject = ['$http', '$cookieStore', '$transformRequestToForm', 'appConstants'];
    angular.module("codename").factory("$queries", $queries);

}());