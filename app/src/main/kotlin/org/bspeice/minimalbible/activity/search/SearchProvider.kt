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
class SearchProvider(val indexManager: IndexManager, val book: Book?) {

    val defaultSearchType = SearchType.ANY_WORDS

    [suppress("UNUSED_PARAMETER")]
    public fun basicTextSearch(text: String): List<Verse> {
        if (!isSearchAvailable()) {
            Log.w("SearchProvider", "Search unavailable, index status of ${book?.getInitials()}: ${book?.getIndexStatus()}")
            return listOf()
        }

        val searchText = defaultSearchType decorate text
        // We already checked for null in isSearchAvailable(), but Kotlin
        // doesn't keep track of that (yet)
        return book!!.find(searchText)
                .map { it as Verse }
    }

    /**
     * Handler to check if the index is available - because it doesn't
     * seem to register itself properly in the book metadata.
     * This check MUST guarantee that the book is not null.
     */
    public fun isSearchAvailable(): Boolean =
            book != null &&
                    indexManager isIndexed book

}
