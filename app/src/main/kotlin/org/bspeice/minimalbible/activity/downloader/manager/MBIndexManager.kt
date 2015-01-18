package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.jsword.index.IndexManager
import rx.subjects.PublishSubject
import org.crosswire.jsword.book.Book
import rx.Observable
import rx.schedulers.Schedulers
import android.util.Log

/**
 * There's already an IndexManager, that's why the funky name
 */
class MBIndexManager(val downloadEvents: PublishSubject<DLProgressEvent>,
                     val indexManager: IndexManager) {

    val subscription = downloadEvents subscribe { handleDlEvent(it) }

    fun handleDlEvent(event: DLProgressEvent): Unit =
            if (event.progress == DLProgressEvent.PROGRESS_COMPLETE) {
                subscription.unsubscribe()
                buildIndex(event.b)
            }

    fun buildIndex(b: Book) {
        Observable.just(b)
                .observeOn(Schedulers.computation())
                .subscribe {
                    try {
                        Log.d("MBIndexManager", "Beginning index status: ${b.getIndexStatus()}")
                        indexManager scheduleIndexCreation b
                        Log.d("MBIndexManager", "Ending index status: ${b.getIndexStatus()}")
                    } catch (e: Exception) {
                        Log.e("MBIndexManager", "Exception building index: ${e}", e)
                    }
                }
        Log.d("MBIndexManager", "Building index for ${b.getInitials()}")
    }

    fun removeIndex(b: Book) = indexManager.deleteIndex(b)
}
