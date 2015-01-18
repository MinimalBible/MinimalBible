package org.bspeice.minimalbible.activity.search

import org.crosswire.jsword.passage.Verse
import org.crosswire.jsword.index.search.SearchType
import org.crosswire.jsword.book.Book
import android.util.Log
import org.crosswire.jsword.index.IndexManager

/**
 * This is the entry point for handling the actual bible search. Likely will support
 * an "advanced" search in the future, but for now, basicTextSearch is what you get.
 */
class SearchProvider(val b: Book, val indexManager: IndexManager) {

    val defaultSearchType = SearchType.ANY_WORDS

    [suppress("UNUSED_PARAMETER")]
    public fun basicTextSearch(text: String): List<Verse> {
        if (!isSearchAvailable()) {
            Log.w("SearchProvider", "Search unavailable, index status of ${b.getInitials()}: ${b.getIndexStatus()}")
            return listOf()
        }

        val searchText = defaultSearchType.decorate(text)
        val results = b.find(searchText)
        return results.map { it as Verse }
    }

    /**
     * Handler to check if the index is available - because it doesn't
     * seem to register itself properly in the book metadata
     */
    public fun isSearchAvailable(): Boolean =
            indexManager.isIndexed(b)

}
