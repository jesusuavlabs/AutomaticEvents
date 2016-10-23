var app = angular.module('myApp', []);

app.controller('formCtrl', function($scope, $http) {
	$scope.showGetPost = true;
    $scope.showUserInfo = false;
    $scope.showTimeWait = false;
    $scope.showError = false;
    $scope.info = 'Primera fase del analisis';
    $scope.srcHisto = "";
    $scope.srcWord = "";
    $scope.srcGustos = "";
    $scope.showImages = false;
    $scope.showEvents = false;
    
    $scope.getPost = function() {
    	$scope.showTimeWait = true;
    	$scope.showGetPost = false;
    	$scope.showEvents = false;
    	
    	$http.get("/getInfo")
	    .then(function (response) {
	    	$scope.user = response.data;
	    	$scope.showUserInfo = true;
	    	$scope.showPersonal = true;
	    	$scope.showTimeWait = false;	       
  
	        $scope.info = 'Comprobación del estado de la información'
		    $scope.srcHisto = "histograma_"+$scope.user.userId+".jpg";
	    	$scope.srcWord = "wordcloud_"+$scope.user.userId+".jpg";
	    	$scope.srcGustos = "Gustos_"+$scope.user.userId+".jpg";
		    $scope.showImages = true;
		    $scope.data= [
	            {name: "Viajar", score: $scope.user.keywordsViajar},
	            {name: "Deporte", score: $scope.user.keywordsDeporte},
	            {name: 'Comida', score: $scope.user.keywordsComida},
	            {name: "Caridad", score: $scope.user.keywordsCaridad},
	            {name: "Viajar", score: $scope.user.keywordsViajar},
	            {name: 'Social', score: $scope.user.keywordsSocial},
	            {name: "Salud", score: $scope.user.keywordsSalud}
	        ];		    
		    
	    }, function (response) {
	    	//handles error
	        $scope.myError = response.statusText;
	        $scope.statuscode = response.status;
	        $scope.showError = true;
	        $scope.showTimeWait = false;
	        $scope.info = 'Error al obtener la información';
	    });		    			    		   
    	
    	
    };	
    
    $scope.getEntireInfo = function() {
    	$scope.showTimeWait = true;
    	$scope.showImages = false;
    	$scope.showUserInfo = false;
    	$scope.showPersonal = false;
    	$scope.srcHisto = "";
	    $scope.srcWord = "";
	    $scope.srcGustos = "";
	    $scope.info = 'Comenzamos a almacenar la información';
    	
    	$http.get("/storeInfo")
	    .then(function (response) {
	    	$scope.user = response.data;
	    	$scope.showUserInfo = false;
	    	$scope.showPersonal = false;
	    	$scope.showTimeWait = false;
  
	        $scope.info = 'Comenzamos a procesar toda la información recuperada';
	        $scope.showTimeWait = true;
	    	$http({ 
	    		url: "/dataMining", 
	    	    method: "GET",
	    	    params: {name: $scope.user.name}
	    	})	    			
		    .then(function (response) {	 
		    	$scope.info = "¡Ha finalizado el análisis con éxito! \nSu espera ha tenido recompensa";
		    	$scope.user = response.data;
		    	$scope.showTimeWait = false;
		    	$scope.showImages = true;
		    	$scope.showUserInfo = true;
		    	$scope.showPersonal = true;
			    $scope.srcHisto = "histograma_"+$scope.user.userId+".jpg";
		    	$scope.srcWord = "wordcloud_"+$scope.user.userId+".jpg";
		    	$scope.srcGustos = "Gustos_"+$scope.user.userId+".jpg";

			    $scope.data= [
		            {name: "Viajar", score: $scope.user.keywordsViajar},
		            {name: "Deporte", score: $scope.user.keywordsDeporte},
		            {name: 'Comida', score: $scope.user.keywordsComida},
		            {name: "Caridad", score: $scope.user.keywordsCaridad},
		            {name: "Viajar", score: $scope.user.keywordsViajar},
		            {name: 'Social', score: $scope.user.keywordsSocial},
		            {name: "Salud", score: $scope.user.keywordsSalud}
		        ];
		    }, function (response) {
		    	//handles error
		        $scope.myError = response.statusText;
		        $scope.statuscode = response.status;
		        $scope.showError = true;
		        $scope.showTimeWait = false;
		        $scope.info = 'Error al procesar la información';
		    });
	    }, function (response) {
	    	//handles error
	        $scope.myError = response.statusText;
	        $scope.statuscode = response.status;
	        $scope.showError = true;
	        $scope.showTimeWait = false;
	        $scope.info = 'Error al comienzo del procesamiento de la información';
	    });	
    	
    };	
    
    $scope.getEvents = function(keyword) {
    	$scope.showTimeWait = true;
    	$scope.showImages = false;
    	$scope.showUserInfo = false;
    	$scope.showPersonal = false;
    	var categorias = "0";
    	var categoria = "";
    	
    	switch (keyword) {
        case 'keywordsCaridad':
            categorias = "111";
            categoria = "Caridad";
            break;
        case 'keywordsCultura':
            categorias = "104,105";
            categoria = "Cultura";
            break;
        case 'keywordsComida':
            categorias = "110";
            categoria = "Comida";
            break;
        case 'keywordsDeporte':
            categorias = "108,109";
            categoria = "Deporte";
            break;
        case 'keywordsViajar':
            categorias = "109";
            categoria = "Viajar";
            break;
        case 'keywordsSocial':
            categorias = "119";
            categoria = "Social";
            break;
        case 'keywordsSalud':
            categorias = "107";
            categoria = "Salud";
            break;
        default:

    	}
    	
    	$http({ 
    		url: "https://www.eventbriteapi.com/v3/events/search/?token=SRISJJUYVVMKSXL5YKT5&categories="+categorias+"&location.address="+$scope.user.location, 
    		method: "GET"
    	})
	    .then(function (response) {	 
		    	$scope.info = "Se han obtenido eventos de categoría "+categoria;
		    	$scope.events = response.data.events;
		    	
		    	$scope.showTimeWait = false;
		    	$scope.showEvents = true;
		    	
		    }, function (response) {
		    	//handles error
		        $scope.myError = response.statusText;
		        $scope.statuscode = response.status;
		        $scope.showError = true;
		        $scope.showTimeWait = false;
		        $scope.info = 'Error al obtener los eventos';
		    });
    };
});

app.directive('imageonload', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('load', function() {
                alert('image is loaded');
            });
            element.bind('error', function(){
                alert('image could not be loaded');
            });
        }
    };
});

app.directive('bars', function ($parse) {
    return {
        restrict: 'E',
        replace: true,
        template: '<div id="chart"></div>',
        link: function (scope, element, attrs) {
          scope.data = [1,2,5,2,3,1];

          chart = d3.select('#chart')
          	.append("div").attr("class", "chart")
            .selectAll('div')
            .data(scope.data).enter()
		    .append('div')
		    .style("width", function(d) { return d + "%"; })
		    .style('opacity', 0)
		    .style("background-color", "#333")
		    .on("mouseover", function(){
		      d3.select(this).transition().duration(300)
		        .style("background-color", "#FFD700");
		    })
		    .on("mouseout", function(){
		      d3.select(this).transition().duration(300)
		        .style("background-color", "#333");})
		    .transition()
		      .delay(function(d, i) { return i * 1000 })
		      .duration(1000)    
		      .style('width', function(d) { return (d) + '%'; })
		      .style('opacity', 1);		  
	        } 
	     };
  });

