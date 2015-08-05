(function () {

    var codename = angular.module('codename', ['ngCookies', 'ngRoute',
        'ngAnimate', 'angular.filter', 'ngFileUpload',
        'satellizer', 'ngTagsInput', 'locator', 'angular-growl']);

    codename.constant("appConstants", {
       
        server: "http://localhost:8080/",
        address: "localhost",
        port: "8080",
        context: ''

    })
            // configure our routes
            .config(function ($routeProvider, $authProvider) {
                $routeProvider
                        .when('/', {
                            templateUrl: 'app/views/home.html',
                            controller: 'homeController'
                        })

                        .when('/localfhellows', {
                            templateUrl: 'app/views/localfhellows.html',
                            controller: 'localFhellowsController'
                        })
                        .when('/password', {
                            templateUrl: 'app/views/password.html',
                            controller: 'passwordController'

                        })
                        .when('/profile', {
                            templateUrl: 'app/views/profile.html',
                            controller: 'profileController'
                        })
                        .when('/profile/:nickname', {
                            templateUrl: 'app/views/publicprofile.html',
                            controller: 'publicProfileController'
                        })

                        .when('/messages', {
                            templateUrl: 'app/views/messages.html',
                            controller: 'messagesController'
                        })
                        .when('/messages/:selectedConversation', {
                            templateUrl: 'app/views/messages.html',
                            controller: 'messagesController'
                        })
                
                        .when('/invite', {
                            templateUrl: 'app/views/invite.html',
                            controller: 'inviteController'
                        })

                        .otherwise({
                            redirectto: '/'
                        });
                $authProvider.google({
                    url: '/' + "codename-server/" + '/rest/auth/google',
                    redirectUri: window.location.protocol + '//' + window.location.host + '/' + "codename-server/",
                    clientId: '475121985833-g2ludjvano3pbgbt98nvt04h7ojmvpjv.apps.googleusercontent.com'
                });
            });

            
    //HISTORY 
    codename.run(function ($rootScope, $location) {

        var history = [];
        $rootScope.navStatus = "show";

        $rootScope.$on('$routeChangeSuccess', function () {
            if ($location.path() == "/" ) {
                $rootScope.navStatus = "hidden";
            }else if ($location.path() == "/invite" ) {
                $rootScope.navStatus = "hiddenFooter";
            }else {
                $rootScope.navStatus = "show";
            }
            history.push($location.$$path);
        });
        $rootScope.back = function () {
            var prevUrl = history.length > 1 ? history.splice(-2)[0] : "/";

            $location.path(prevUrl);
        };
    });

    //END HISTORY

    codename.config(['growlProvider', function(growlProvider) {
        growlProvider.globalPosition('top-center');
        growlProvider.globalTimeToLive(3000);
    }]);

}());













