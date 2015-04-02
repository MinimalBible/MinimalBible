package org.bspeice.minimalbible.activity.downloader.manager

import com.jayway.awaitility.Awaitility
import org.bspeice.minimalbible.activity.downloader.DownloadPrefs
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.install.Installer
import org.jetbrains.spek.api.Spek
import org.mockito.Matchers.anyLong
import org.mockito.Mockito.*
import rx.Subscriber
import rx.schedulers.Schedulers
import java.util.Calendar
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by bspeice on 1/3/15.
 */

class RefreshManagerSpek() : Spek() {init {

    fun buildRefreshmanager(installers: List<Installer>, prefs: DownloadPrefs) =
            RefreshManager(installers, listOf(""), prefs, null)

    fun buildMockPrefs(): DownloadPrefs {
        val currentTime = Calendar.getInstance().getTime().getTime()
        val eighteenDaysAgo = currentTime - 1555200
        val mockPrefs = mock(javaClass<DownloadPrefs>())
        `when`(mockPrefs.downloadRefreshedOn())
                .thenReturn(eighteenDaysAgo)

        return mockPrefs
    }

    given("a mock installer") {
        val installer = mock(javaClass<Installer>())

        on("creating a new RefreshManager and mock preferences") {
            val mockPrefs = buildMockPrefs()
            buildRefreshmanager(listOf(installer, installer), mockPrefs)

            it("should not have updated the prefs as part of the constructor") {
                verify(mockPrefs, never())
                        .downloadRefreshedOn(anyLong())
            }
        }

        on("creating a new RefreshManager and mock preferences") {
            val mockPrefs = buildMockPrefs()
            val rM = buildRefreshmanager(listOf(installer, installer), mockPrefs)
            reset(mockPrefs)

            it("should not update the prefs after the first installer") {
                // The process to do actually validate this is tricky. We have to block
                // the Observable from producing before we can validate the preferences -
                // I don't want to race the Observable since it's possible it's on another thread.
                // So, we use backpressure (request(1)) to force the observable to
                // produce only one result.
                val success = AtomicBoolean(false)
                rM.availableModules
                        .subscribe(object : Subscriber<Map<Installer, List<Book>>>() {
                            override fun onCompleted() {
                            }

                            override fun onError(e: Throwable?) {
                            }

                            override fun onStart() {
                                super.onStart()
                                request(1)
                            }

                            override fun onNext(t: Map<Installer, List<Book>>?) {
                                // Verify the mock - if verification doesn't pass, we won't reach
                                // the end of this method and set our AtomicBoolean to true
                                verify(mockPrefs, never())
                                        .downloadRefreshedOn(anyLong())
                                success.set(true)
                            }
                        })

                Awaitility.waitAtMost(2, TimeUnit.SECONDS)
                        .untilTrue(success)
            }
        }

        on("creating a new RefreshManager and mock preferences") {
            val mockPrefs = buildMockPrefs()
            val rM = buildRefreshmanager(listOf(installer, installer), mockPrefs)
            reset(mockPrefs)

            it("should update the prefs after completed") {
                val complete = AtomicBoolean(false)
                rM.availableModules.observeOn(Schedulers.immediate())
                        .subscribe({}, {}, {
                            complete.set(true)
                        })

                Awaitility.waitAtMost(3, TimeUnit.SECONDS)
                        .untilTrue(complete)

                verify(mockPrefs, times(1))
                        .downloadRefreshedOn(anyLong())
            }
        }
    }
}
}
