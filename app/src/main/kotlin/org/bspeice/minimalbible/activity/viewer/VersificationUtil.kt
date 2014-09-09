/**
 * Created by bspeice on 9/8/14.
 */
package org.bspeice.minimalbible.activity.viewer.bookutil

import org.crosswire.jsword.versification.BibleBook
import java.util.ArrayList
import org.crosswire.jsword.versification.Versification
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.versification.system.Versifications
import org.crosswire.jsword.book.BookMetaData
import rx.Observable

class VersificationUtil() {

    val INTROS: Array<BibleBook> = array(
            BibleBook.INTRO_BIBLE,
            BibleBook.INTRO_OT,
            BibleBook.INTRO_NT)

    public fun getVersification(b: Book): Versification {
        return Versifications.instance()!!.getVersification(
                b.getBookMetaData()!!.getProperty(BookMetaData.KEY_VERSIFICATION) as String
        ) as Versification
    }

    public fun getBookName(book: Book, bibleBook: BibleBook): String {
        return getVersification(book).getLongName(bibleBook) as String
    }

    public fun getChapterCount(book: Book, bibleBook: BibleBook): Int {
        return getVersification(book).getLastChapter(bibleBook)
    }

    public fun getBooks(b: Book): Observable<BibleBook> {
        return Observable.from(getVersification(b).getBookIterator()!!.copyIterator())
                ?.filter { INTROS.contains(it) } as Observable<BibleBook>
    }

    public fun getBookNames(b: Book): Observable<String> {
        return getBooks(b).map { getBookName(b, it as BibleBook) }
                as Observable<String>
    }

}

fun <T> Iterator<T>.copyIterator(): List<T> {
    var list: MutableList<T> = ArrayList() // Must be mutable
    while (this.hasNext()) {
        list.add(this.next())
    }
    return list
}