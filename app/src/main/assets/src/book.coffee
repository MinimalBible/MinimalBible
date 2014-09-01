require 'jquery' # For using selectors to access scope
require 'angular'

app = angular.module('bookApp', [])

app.controller 'BookCtrl', ['$scope', ($scope) ->
	$scope.verses = [
		{'text': 'hello.'}
	];

	$scope.alert = ->
		alert "Hello!"
]

###
Future reference: Get the controller scope like so:
angular.element($("<controller-element>")).scope().<function>

For example:
angular.element($("#bookController")).scope().<function>
###
