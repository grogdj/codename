(function () {
    var inviteController = function ($scope, $rootScope, $users, $auth, appConstants, $invites ) {
       $scope.pageClass = "invite full";
       $scope.inviteStatus = false;
        
        
       $scope.requestInvite = function (email) {
            
            $invites.request(email).success(function (data) {
                $scope.inviteStatus = true;
                $rootScope.$broadcast("quickNotification", "Invitation Sent");


            }).error(function (data) {
                
                $rootScope.$broadcast("quickNotification", "Something failed: " + data, 'error');
                console.log("Error : " + data + "!");

            });

        }
       
       $scope.carouselPosition = 1;
       $scope.carouselItems = $(".carousel-item");
       $scope.carouselLenght = $scope.carouselItems.length;
       $scope.initCarousel = function(){
           setTimeout(function(){ $scope.moveCarousel() }, 6000);
           
       }
       $scope.moveCarousel = function(){
           
           if($scope.carouselPosition < $scope.carouselLenght){
                $scope.carouselPosition++
           }else {
                $scope.carouselPosition = 1;
           }
           
           $scope.carouselItems.each(function(index){
               var itemPosition = 100 - ($scope.carouselPosition - index) * 100;
               $(this).css( "left", itemPosition+"%" );
           });
            $scope.initCarousel();
       }
       
       $scope.initCarousel();

        var nounAudio = new Audio('/app/static/noun.mp3');
        $scope.playWord = function(){
            nounAudio.play();
        }
        
    };

    inviteController.$inject = ['$scope', '$rootScope', '$users', '$auth', 'appConstants', '$invites'];
    angular.module("codename").controller("inviteController", inviteController);

}());


