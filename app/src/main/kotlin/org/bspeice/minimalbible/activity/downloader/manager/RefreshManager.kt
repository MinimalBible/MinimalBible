package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.jsword.book.install.Installer
import java.util.concurrent.atomic.AtomicBoolean
import rx.Observable
import org.crosswire.jsword.book.Book
import rx.schedulers.Schedulers
import java.util.Calendar
import org.bspeice.minimalbible.activity.downloader.DownloadPrefs
import android.net.ConnectivityManager

/**
 * Created by bspeice on 10/22/14.
 */

class RefreshManager(val installers: Collection<Installer>,
                     val prefs: DownloadPrefs,
                     val connManager: ConnectivityManager?) {
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

    val fifteenDaysAgo = Calendar.getInstance().getTime().getTime() - 1296000

    fun doReload(enabledDownload: Boolean, lastUpdated: Long, onWifi: Boolean): Boolean =
            if (!enabledDownload || !onWifi)
                false
            else if (lastUpdated < fifteenDaysAgo)
                true
            else
                false

    fun doReload(): Boolean = doReload(prefs.hasEnabledDownload(),
            prefs.downloadRefreshedOn(),
            // TODO: Functional is awesome, but this might be a bit ridiculous
            (if (connManager?.getActiveNetworkInfo() != null)
                connManager!!.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI
            else
                false)
    )

    fun installerFromBook(b: Book): Observable<Installer> = Observable.just(
            availableModules.filter {
                it.flatMap { it.value } contains b
            }.toBlocking().first().entrySet().first().getKey())
}