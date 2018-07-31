var app = angular.module('myApp', []);
app.controller('myCtrl',['$scope','$http', function($scope,$http) {
    $scope.hometeam = "John";
    $scope.confirm_msg = "Bạn có chắc muốn xóa";
	$scope.showMsg = true;
	
	
	$scope.loadAllUser = function(){
		$http({
	         method: 'GET',
	         url: '/user/getAllRange',
	         responseType: 'json'
	    }).then(function successCallback(response) {
	    	  $scope.userList = response.data;
	    }, function errorCallBack(response) {
	          console.log('Get Sensors Failed!');
	   });
	}
	
	$scope.getTotal = function(userList){
		var total = 0;
		for(var i = 0 ;i < userList.length;i++){
			total+=userList[i].amount;
		}
		return total;
	}
	
	$scope.loadAllUser();
}]);