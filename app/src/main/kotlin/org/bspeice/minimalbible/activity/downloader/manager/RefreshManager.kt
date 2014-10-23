package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.jsword.book.install.Installer
import java.util.concurrent.atomic.AtomicBoolean
import rx.Observable
import org.crosswire.jsword.book.Book
import rx.schedulers.Schedulers

/**
 * Created by bspeice on 10/22/14.
 */

class RefreshManager(val installers: Collection<Installer>) {
    val refreshComplete = AtomicBoolean()
    val availableModules: Observable<Map<Installer, List<Book>>> =
            Observable.from(installers)
                    .map {
                        if (doReload()) {
                            it.reloadBookList()
                        }
                        mapOf(Pair(it, it.getBooks()))
                    }
                    .subscribeOn(Schedulers.io())
                    .cache();

    val availableModulesFlat: Observable<Book>
        get() = availableModules
                // Map -> Lists
                .flatMap { Observable.from(it.values()) }
                // Lists -> Single list
                .flatMap { Observable.from(it) };

    // Constructor - Split from the value creation because `subscribe` returns
    // the subscriber object, not the underlying value
    {
        availableModules.subscribe({}, {}, { refreshComplete set true })
    }

    fun doReload(): Boolean = true

    fun installerFromBook(b: Book): Observable<Installer> = Observable.just(
            availableModules.filter {
                it.flatMap { it.value } contains b
            }
                    .toBlocking().first().entrySet().first().getKey())
}