require 'angular'

app = angular.module('bookApp', ['ui.scroll', 'ui.scroll.jqlite'])

app.controller 'BookCtrl', ['$scope', '$filter', ($scope, $filter) ->
	$scope.verseSource = 
		get: (index, count, success) ->
			console.log "Calling me with " + index
			success angular.fromJson Android.getVerses(index, count)

	$scope.order_verses = ->
		$scope.verses = $filter('orderBy')($scope.verses, 'id', false)

	$scope.appendVerse = (jsonVerseString) ->
		$scope.verses.push angular.fromJson jsonVerseString
		$scope.order_verses()
]

# Due to page initialization, we can only store the controller string.
# The actual element changes, so there's nothing we can do with JQuery
# etc. to grab the scope ahead of time and re-use it.
controller = "#bookController"

#window.appendVerse = (jsonVerseString) ->
#scope = angular.element($("#bookController")).scope()
#scope.appendVerse jsonVerseString
## Since we're calling outside of angular, we need to manually apply
#scope.$apply()

###
Future reference: Get the controller scope like so:
angular.element($("<controller-element>")).scope().<function>

For example:
angular.element($("#bookController")).scope().<function>
###
