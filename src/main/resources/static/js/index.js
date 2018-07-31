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
	    	  var homeTotal = 0;
	    	  var awayTotal = 0;
	    	  for(var i = 0 ; i < $scope.betList.length;i++ ){
	    		  if($scope.betList[i].betHome){
	    			  homeTotal += $scope.betList[i].point;
	    		  }else{
	    			  awayTotal += $scope.betList[i].point;
	    		  }
	    	  }
	    	  $scope.currentMatch.homeTotal = homeTotal;
	    	  $scope.currentMatch.awayTotal = awayTotal;
	    }, function errorCallBack(response) {
	          console.log('Get Sensors Failed!');
	   });
	}
	
	$scope.betForMatch = function(isHome){
		var team = '';
		var point = 0;
		if(isHome){
			if($scope.currentMatch.homePoint == null || $scope.currentMatch.homePoint == '' || $scope.currentMatch.homePoint < 2 || $scope.currentMatch.homePoint > 20){
				alert('Bắt buộc đặt từ 2 -> 10 bóng');
				return;
			}
			point = $scope.currentMatch.homePoint;
			team = $scope.currentMatch.homeTeam;
		}else{
			if($scope.currentMatch.awayPoint  == null || $scope.currentMatch.awayPoint  == '' || $scope.currentMatch.awayPoint < 2 || $scope.currentMatch.awayPoint > 20){
				alert('Bắt buộc đặt từ 2 -> 10 bóng');
				return;
			}
			point = $scope.currentMatch.awayPoint;
			team = $scope.currentMatch.awayTeam;
		}
		if(confirm("Bạn có chắc muốn đặt cho "+team + ". Sau khi dat thi khong the hoan lai" )){
			$http({
		         method: 'GET',
		         url: '/user/bet/'+$scope.currentMatch.id+'/'+isHome+'/'+ point,
		         responseType: 'json'
		    }).then(function successCallback(response) {
		    	if(response.data.code == 'NOT_ACTIVE'){
		    		alert('Trận này ko active');
		    	} else if(response.data.code == 'TIME_OUT'){
		    		alert('Hết giờ đặt');
		    	}else if(response.data.code == 'OUT_RANGE'){
		    		alert('Vượt giới hạn');
		    	}else if(response.data.code == 'NOT_ENOUGH_MONEY'){
		    		alert('Không đủ tiền');
		    	}else  if(response.data.code == 'ALREADY_BET'){
		    		alert('Đã đặt rồi không thể đặt tiếp');
		    	}else if(response.data.code == 'SUCCESS'){
		    		  $scope.changeCurrentMatch($scope.currentMatch);
		    		  alert('Đặt thành công');
		    	}
		    }, function errorCallBack(response) {
		          console.log('Get Sensors Failed!');
		   });
		}
	}
	
	$scope.getNumber = function(number){
		return new Array(number);
	}
	
	$scope.loadAllMatch();
}]);