require 'angular'

app = angular.module('bookApp', [])

app.controller 'BookCtrl', ['$scope', ($scope) ->
	$scope.verses = [
		{'text': 'hello.'}
	];
]
