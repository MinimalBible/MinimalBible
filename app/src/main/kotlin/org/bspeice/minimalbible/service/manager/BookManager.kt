package org.bspeice.minimalbible.service.manager

import org.bspeice.minimalbible.exception.NoBooksInstalledException
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.Books
import rx.Observable
import rx.functions.Action0
import rx.functions.Action1
import javax.inject.Singleton

/**
 * 'Open' class and values for mockito to subclass
 * http://confluence.jetbrains.com/display/Kotlin/Classes+and+Inheritance
 */

Singleton
open class BookManager(val ignore: List<String>) {

    // TODO: Remove static reference to Books.installed()
    open val installedBooks = Observable.from(Books.installed()!!.getBooks())
            ?.filter { !ignore.contains(it!!.getInitials()) }
            ?.cache() ?: throw NoBooksInstalledException()

    var refreshComplete = false

    init {
        // TODO: Cleaner way of expressing this?
        installedBooks.subscribe(Action1<Book> { result -> },
                Action1<Throwable> { error -> },
                Action0 { refreshComplete = true })
    }
}