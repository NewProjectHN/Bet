var app = angular.module('myApp', []);
app.controller('myCtrl',['$scope','$http', function($scope,$http) {
    $scope.hometeam = "John";
    $scope.confirm_msg = "Bạn có chắc muốn xóa";
	$scope.showMsg = true;
	
	
	$scope.loadAllMatch = function(){
		$http({
	         method: 'GET',
	         url: '/user/getAllMatchAcitve',
	         responseType: 'json'
	    }).then(function successCallback(response) {
	    	  $scope.matchList = response.data;
	    	  if($scope.matchList.length > 0){
	    		  $scope.changeCurrentMatch($scope.matchList[0]);
	    	  }
	    }, function errorCallBack(response) {
	          console.log('Get Sensors Failed!');
	   });
	}
	
	$scope.resetCurrentMatch = function(){
		if(confirm("Bạn có chắc muốn reset tran nay" )){
			$http({
		         method: 'GET',
		         url: '/admin/resetForMatch/'+ $scope.currentMatch.id,
		         responseType: 'json'
		    }).then(function successCallback(response) {
		    	alert(response.data.code);
		    }, function errorCallBack(response) {
		          console.log('Get Sensors Failed!');
		   });
		}
	}
	
	$scope.payCurrentMatch = function(){
		if(confirm("Bạn có chắc muốn thuc hien thanh toan cho tran nay?" )){
			$http({
		         method: 'GET',
		         url: '/admin/paymentForMatch/'+ $scope.currentMatch.id,
		         responseType: 'json'
		    }).then(function successCallback(response) {
		    	alert(response.data.code);
		    }, function errorCallBack(response) {
		          console.log('Get Sensors Failed!');
		   });
		}
	}
	
	$scope.payArrange = function(){
		if(confirm("Bạn có chắc muốn thuc hien Cap nhat Bang Xep hang?" )){
			$http({
		         method: 'GET',
		         url: '/admin/updateRange/',
		         responseType: 'json'
		    }).then(function successCallback(response) {
		    	alert(response.data.code);
		    }, function errorCallBack(response) {
		          console.log('Get Sensors Failed!');
		   });
		}
	}
	
	$scope.betList = [];
	$scope.changeCurrentMatch = function(match){
		$scope.currentMatch = match;
		$scope.currentMatch.homePoint = 0;
		$scope.currentMatch.awayPoint = 0;
		$scope.betList = [];
		$http({
	         method: 'GET',
	         url: '/user/getAllBetByMatch/' + $scope.currentMatch.id,
	         responseType: 'json'
	    }).then(function successCallback(response) {
	    	  $scope.betList = response.data;
	  	    	  for(var i = 0 ; i < $scope.betList.length;i++ ){
	    		  if($scope.betList[i].betHome){
	    			  $scope.betList[i].homePoint = $scope.betList[i].point;
	    			  $scope.betList[i].awayPoint = 0;
	    		  }else{
	    			  $scope.betList[i].homePoint = 0;
	    			  $scope.betList[i].awayPoint = $scope.betList[i].point;
	    		  }
	    	  }
	    }, function errorCallBack(response) {
	          console.log('Get Sensors Failed!');
	   });
	}
	
	$scope.getTotalPoint = function(isHome){
		var total = 0;
		for(var i = 0 ; i < $scope.betList.length;i++ ){
			if(isHome == $scope.betList[i].betHome){
				total += $scope.betList[i].point;
   		  	}
   	  	}
		return total;
	}
	
	$scope.changePoint = function(bet){
		var team = '';
		var point = 0;
		if(bet.homePoint == null || bet.homePoint == ''){
			bet.homePoint = 0;
		}
		if(bet.awayPoint == null || bet.awayPoint == ''){
			bet.awayPoint = 0;
		}
		if(confirm("Bạn có chắc muốn đổi bóng" )){
			$http({
		         method: 'GET',
		         url: '/admin/changeBet/'+bet.id+'/'+bet.homePoint+'/'+ bet.awayPoint,
		         responseType: 'json'
		    }).then(function successCallback(response) {
		    	alert(response.data.code);
		    }, function errorCallBack(response) {
		          console.log('Get Sensors Failed!');
		   });
		}
	}
	
	/*$scope.getNumber = function(number){
		return new Array(number);
	}*/
	
	$scope.loadAllMatch();
}]);