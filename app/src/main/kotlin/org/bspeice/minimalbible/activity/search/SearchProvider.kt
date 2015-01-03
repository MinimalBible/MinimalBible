package org.bspeice.minimalbible.activity.search

import org.crosswire.jsword.passage.Verse

/**
 * This is the entry point for handling the actual bible search. Likely will support
 * an "advanced" search in the future, but for now, basicTextSearch is what you get.
 */
class SearchProvider() {

    public fun basicTextSearch(text: String): List<Verse> =
            listOf()
}
