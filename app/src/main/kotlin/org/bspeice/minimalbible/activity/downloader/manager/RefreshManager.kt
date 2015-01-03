package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.jsword.book.install.Installer
import rx.Observable
import org.crosswire.jsword.book.Book
import rx.schedulers.Schedulers
import java.util.Calendar
import org.bspeice.minimalbible.activity.downloader.DownloadPrefs
import android.net.ConnectivityManager
import org.crosswire.jsword.book.BookComparators

/**
 * Created by bspeice on 10/22/14.
 */

class RefreshManager(val installers: Collection<Installer>,
                     val exclude: List<String>,
                     val prefs: DownloadPrefs,
                     val connManager: ConnectivityManager?) {

    val currentTime = Calendar.getInstance().getTime().getTime()
    val fifteenDaysAgo = currentTime - 1296000

    val availableModules: Observable<Map<Installer, List<Book>>> =
            Observable.from(installers)
                    .map {
                        if (performReload())
                            it.reloadBookList()

                        // TODO: mapOf(it to booksFromInstaller)
                        mapOf(Pair(it,
                                booksFromInstaller(it, exclude)))
                    }
                    // Don't update timestamps until done. Additionally, make this operation
                    // part of the pipeline, so it remains a cold observable
                    .doOnCompleted { prefs.downloadRefreshedOn(currentTime) }
                    .subscribeOn(Schedulers.io())
                    .cache()

    val flatModules: Observable<Book> =
            availableModules
                    // Map -> Lists
                    .flatMap { Observable.from(it.values()) }
                    // Lists -> Single list
                    .flatMap { Observable.from(it) }

    val flatModulesSorted = flatModules.toSortedList {(book1, book2) ->
        BookComparators.getInitialComparator().compare(book1, book2)
    }

    fun doReload(downloadEnabled: Boolean, lastUpdated: Long,
                 networkState: Int? = ConnectivityManager.TYPE_DUMMY): Boolean =
            if (!downloadEnabled || networkState != ConnectivityManager.TYPE_WIFI)
                false
            else if (lastUpdated < fifteenDaysAgo)
                true
            else
                false

    fun performReload() =
            doReload(prefs.hasEnabledDownload(),
                    prefs.downloadRefreshedOn(),
                    connManager?.getActiveNetworkInfo()?.getType())

    fun booksFromInstaller(inst: Installer, exclude: List<String>) =
            inst.getBooks().filterNot { exclude contains it.getInitials() }

    fun installerFromBook(b: Book): Observable<Installer> = Observable.just(
            availableModules.filter {
                it.flatMap { it.value } contains b
            }.toBlocking().first().entrySet().first().getKey())
}