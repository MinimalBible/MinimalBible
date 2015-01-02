package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.jsword.book.install.Installer
import rx.Observable
import org.crosswire.jsword.book.Book
import rx.schedulers.Schedulers
import java.util.Calendar
import org.bspeice.minimalbible.activity.downloader.DownloadPrefs
import android.net.ConnectivityManager
import org.crosswire.jsword.book.BookComparators
import java.util.Date

/**
 * Created by bspeice on 10/22/14.
 */

class RefreshManager(val installers: Collection<Installer>,
                     val exclude: List<String>,
                     val prefs: DownloadPrefs,
                     val connManager: ConnectivityManager?) {

    val availableModules: Observable<Map<Installer, List<Book>>> =
            Observable.from(installers)
                    .map {
                        if (doReload()) {
                            it.reloadBookList()
                            prefs.downloadRefreshedOn(Date().getTime())
                        }
                        val validBooks = it.getBooks()
                                .filterNot { exclude contains it.getInitials() }
                        mapOf(Pair(it, validBooks))
                    }
                    .subscribeOn(Schedulers.io())
                    .cache();

    val flatModules: Observable<Book> =
            availableModules
                    // Map -> Lists
                    .flatMap { Observable.from(it.values()) }
                    // Lists -> Single list
                    .flatMap { Observable.from(it) }

    val flatModulesSorted = flatModules.toSortedList {(book1, book2) ->
        BookComparators.getInitialComparator().compare(book1, book2)
    };

    val fifteenDaysAgo = Calendar.getInstance().getTime().getTime() - 1296000

    fun doReload(downloadEnabled: Boolean, lastUpdated: Long,
                 networkState: Int? = ConnectivityManager.TYPE_DUMMY): Boolean =
            if (!downloadEnabled || networkState != ConnectivityManager.TYPE_WIFI)
                false
            else if (lastUpdated < fifteenDaysAgo)
                true
            else
                false

    fun doReload(): Boolean = doReload(prefs.hasEnabledDownload(),
            prefs.downloadRefreshedOn(),
            connManager?.getActiveNetworkInfo()?.getType())

    fun installerFromBook(b: Book): Observable<Installer> = Observable.just(
            availableModules.filter {
                it.flatMap { it.value } contains b
            }.toBlocking().first().entrySet().first().getKey())
}