package org.bspeice.minimalbible.service.manager

import rx.Observable
import org.crosswire.jsword.book.Books
import rx.functions.Action1
import org.crosswire.jsword.book.Book
import rx.functions.Action0
import org.bspeice.minimalbible.exception.NoBooksInstalledException
import javax.inject.Singleton

/**
 * 'Open' class and values for mockito to subclass
 * http://confluence.jetbrains.com/display/Kotlin/Classes+and+Inheritance
 */

Singleton
open class BookManager() {
    // Some extra books like to think they're installed, but trigger NPE all over the place...
    val ignore = array("ERen_no", "ot1nt2");


    open val installedBooks = Observable.from(Books.installed()!!.getBooks())
            ?.filter { !ignore.contains(it!!.getInitials()) }
            ?.cache() ?: throw NoBooksInstalledException()

    var refreshComplete = false;

    {
        // TODO: Cleaner way of expressing this?
        installedBooks.subscribe(Action1<Book> { result -> },
                Action1<Throwable> { error -> },
                Action0 { refreshComplete = true })
    }
}