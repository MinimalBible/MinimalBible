package org.crosswire.jsword.versification

import org.crosswire.jsword.book.Book
import java.util.ArrayList
import org.crosswire.jsword.versification.system.Versifications
import org.crosswire.jsword.book.BookMetaData
import rx.Observable
import android.util.Log
import org.bspeice.minimalbible.service.manager.InvalidBookException

/**
 * Created by bspeice on 9/10/14.
 */

class VersificationUtil() {
    class object {
        val INTROS = array(
                BibleBook.INTRO_BIBLE,
                BibleBook.INTRO_OT,
                BibleBook.INTRO_NT
        )
    }

    fun getBookNames(b: Book): Observable<String> {
        return Observable.from(b.getVersification().getBookNames())
    }

    fun getBooks(b: Book): Observable<BibleBook> {
        return Observable.from(b.getVersification().getBooks())
    }

    fun getChapterCount(b: Book, bibleBook: BibleBook): Int {
        return b.getVersification().getChapterCount(bibleBook)
    }

    fun getBookName(b: Book, bibleBook: BibleBook): String {
        return b.getVersification().getLongName(bibleBook)
    }

    fun getVersification(b: Book): Versification {
        return b.getVersification()
    }
}

// TODO: Refactor (is there a better way to accomplish this?) and move
fun <T> Iterator<T>.iterable(): Iterable<T> {
    val list: MutableList<T> = ArrayList()
    while (this.hasNext()) {
        list.add(this.next())
    }

    return list
}

fun Versification.getBooks(): List<BibleBook> {
    return this.getBookIterator()!!.iterable()
            .filter { !VersificationUtil.INTROS.contains(it) }
}

fun Versification.getBookNames(): List<String> {
    return this.getBooks().map { this.getLongName(it) }
}

fun Versification.getChapterCount(b: BibleBook): Int {
    return this.getLastChapter(b)
}

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