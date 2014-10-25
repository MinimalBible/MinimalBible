package org.crosswire.jsword.book

import org.crosswire.jsword.versification.Versification
import org.crosswire.jsword.versification.system.Versifications
import android.util.Log
import org.bspeice.minimalbible.service.manager.InvalidBookException
import org.crosswire.jsword.versification.getBookNames
import org.crosswire.jsword.versification.BibleBook

/**
 * Utility methods for dealing with Books
 */

fun Book.getVersification(): Versification {
    val v = Versifications.instance()!!.getVersification(
            this.getBookMetaData()!!.getProperty(BookMetaData.KEY_VERSIFICATION).toString()
    )
    if (v == null) {
        Log.e(javaClass<Book>().getSimpleName(), "Invalid book: " + this.getInitials())
        throw InvalidBookException(this.getInitials())
    } else
        return v
}

fun Book.bookNames(): List<String> = this.getVersification().getBookNames()

fun Book.bookName(bBook: BibleBook): String =
        this.getVersification().getBookName(bBook).getLongName()