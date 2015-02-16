package org.bspeice.minimalbible.activity.search

import org.crosswire.jsword.index.IndexManager
import org.crosswire.jsword.book.Book
import android.util.Log
import rx.schedulers.Schedulers
import rx.Observable
import org.crosswire.jsword.index.IndexStatus
import rx.subjects.ReplaySubject

/**
 * There's already an IndexManager, that's why the funky name
 */
class MBIndexManager(val indexManager: IndexManager) {

    fun shouldIndex(b: Book) = b.getIndexStatus() == IndexStatus.UNDONE
    fun indexReady(b: Book) = b.getIndexStatus() == IndexStatus.DONE

    /**
     * Do the hard work of actually building the book index.
     * Returns a PublishSubject<> that completes when the
     * index is complete. Also is nice enough to broadcast
     * what work is being done when.
     */
    fun buildIndex(b: Book): ReplaySubject<IndexStatus> {
        if (!shouldIndex(b)) {
            Log.e("MBIndexManager", "Current status is ${b.getIndexStatus()}, not creating index")
            throw IllegalStateException("Don't try and index a book that should not get it.")
        }

        val indexStatus: ReplaySubject<IndexStatus> = ReplaySubject.create();
        Observable.just(b)
                .observeOn(Schedulers.computation())
                .subscribe({
                    indexStatus.onNext(b.getIndexStatus())
                    Log.e("MBIndexManager", "Building index for ${b.getInitials()}, ${b.getIndexStatus()}")

                    indexManager scheduleIndexCreation b
                    Log.e("MBIndexManager", "Done building index for ${b.getInitials()}, ${b.getIndexStatus()}")

                    indexStatus.onNext(b.getIndexStatus())
                    indexStatus.onCompleted()
                }, { Log.e("MBIndexManager", "Exception building index: $it", it) })

        return indexStatus
    }

    fun removeIndex(b: Book) = indexManager.deleteIndex(b)
}