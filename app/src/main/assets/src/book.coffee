$ = require 'jquery' # For using selectors to access scope
require 'angular'

app = angular.module('bookApp', [])

app.controller 'BookCtrl', ['$scope', ($scope) ->
	$scope.verses = [
		{'text': 'hello.'}
	];

	$scope.appendVerse = (text) ->
		$scope.verses.push {'text': text}
]

# Due to page initialization, we can only store the controller string.
# The actual element changes, so there's nothing we can do with JQuery
# etc. to grab the scope ahead of time and re-use it.
controller = "#bookController"

window.appendVerse = (text) ->
	scope = angular.element($("#bookController")).scope()
	scope.appendVerse text
	# Since we're calling outside of angular, we need to manually apply
	scope.$apply()

console.log Android.testReturn "Good morning."

###
Future reference: Get the controller scope like so:
angular.element($("<controller-element>")).scope().<function>

For example:
angular.element($("#bookController")).scope().<function>
###
