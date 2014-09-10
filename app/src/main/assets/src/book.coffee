$ = require 'jquery' # For using selectors to access scope
require 'angular'

app = angular.module('bookApp', [])

app.controller 'BookCtrl', ['$scope', '$filter', ($scope, $filter) ->
	$scope.verses = []

	$scope.order_verses = ->
		$scope.verses = $filter('orderBy')($scope.verses, 'id', false)

	$scope.appendVerse = (verse) ->
		$scope.verses.push verse
		$scope.order_verses()
]

# Due to page initialization, we can only store the controller string.
# The actual element changes, so there's nothing we can do with JQuery
# etc. to grab the scope ahead of time and re-use it.
controller = "#bookController"

window.appendVerse = (jsonVerseString) ->
	scope = angular.element($("#bookController")).scope()
	scope.appendVerse angular.fromJson jsonVerseString
	# Since we're calling outside of angular, we need to manually apply
	scope.$apply()

###
Future reference: Get the controller scope like so:
angular.element($("<controller-element>")).scope().<function>

For example:
angular.element($("#bookController")).scope().<function>
###
