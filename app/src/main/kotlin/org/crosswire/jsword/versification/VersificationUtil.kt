package org.crosswire.jsword.versification

import java.util.ArrayList

/**
 * VersificationUtil class allows Java to easily reach in to Kotlin
 */
object INTRO_BOOKS {
    val INTROS = array(
            BibleBook.INTRO_BIBLE,
            BibleBook.INTRO_OT,
            BibleBook.INTRO_NT
    )
}

// TODO: Refactor (is there a better way to accomplish this?) and move
fun <T> Iterator<T>.iterable(): Iterable<T> {
    val list: MutableList<T> = ArrayList()
    while (this.hasNext()) {
        list.add(this.next())
    }

    return list
}

fun Versification.getAllBooks(): List<BibleBook> =
        this.getBookIterator()!!.iterable().toList()

fun Versification.getBooks(): List<BibleBook> =
        this.getAllBooks().filter { !INTRO_BOOKS.INTROS.contains(it) }

fun Versification.getBookNames(): List<String> =
        this.getBooks().map { this.getLongName(it) }

fun Versification.getChapterCount(b: BibleBook): Int {
    return this.getLastChapter(b)
}