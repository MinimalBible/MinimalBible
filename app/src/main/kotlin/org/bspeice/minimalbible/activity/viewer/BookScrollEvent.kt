package org.bspeice.minimalbible.activity.viewer

import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.getVersification
import org.crosswire.jsword.passage.Verse
import org.crosswire.jsword.versification.BibleBook

/**
 * Created by bspeice on 11/26/14.
 */
data class BookScrollEvent(val b: BibleBook, val chapter: Int) {
    constructor(v: Verse) : this(v.getBook(), v.getChapter()) {
    }

    constructor(b: Book, ordinal: Int) : this(b.getVersification().decodeOrdinal(ordinal)) {
    }
}
