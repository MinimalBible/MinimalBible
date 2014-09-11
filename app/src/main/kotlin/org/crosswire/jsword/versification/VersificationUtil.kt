package org.crosswire.jsword.versification

import org.crosswire.jsword.book.Book
import java.util.ArrayList
import org.crosswire.jsword.versification.system.Versifications
import org.crosswire.jsword.book.BookMetaData
import rx.Observable

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
        return Observable.from(b.getVersification().getBookNames(b)) as Observable
    }

    fun getBooks(b: Book): Observable<BibleBook> {
        return Observable.from(b.getVersification().getBooks(b)) as Observable
    }

    fun getChapterCount(b: Book, bibleBook: BibleBook): Int {
        return b.getVersification().getChapterCount(bibleBook)
    }

    fun getBookName(b: Book, bibleBook: BibleBook): String {
        return b.getVersification().getLongName(bibleBook) as String
    }

    fun getVersification(b: Book): Versification {
        return b.getVersification()
    }
}

// There's probably a better way to do this
fun <T> Iterator<T>.iterable(): Iterable<T> {
    val list: MutableList<T> = ArrayList()
    while (this.hasNext()) {
        list.add(this.next())
    }

    return list
}

fun Versification.getBooks(b: Book): List<BibleBook> {
    return this.getBookIterator()!!.iterable()
            .filter { VersificationUtil.INTROS.contains(it) }
}

fun Versification.getBookNames(b: Book): List<String> {
    return this.getBooks(b).map { this.getLongName(it) as String }
}

fun Versification.getChapterCount(b: BibleBook): Int {
    return this.getLastChapter(b)
}

fun Book.getVersification(): Versification {
    return Versifications.instance()!!.getVersification(
            this.getBookMetaData()!!.getProperty(BookMetaData.KEY_VERSIFICATION) as String
    ) as Versification
}