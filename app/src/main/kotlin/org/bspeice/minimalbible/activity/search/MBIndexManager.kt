package org.bspeice.minimalbible.activity.search

import org.crosswire.jsword.index.IndexManager
import org.crosswire.jsword.book.Book
import android.util.Log
import rx.schedulers.Schedulers
import rx.Observable
import rx.subjects.PublishSubject
import org.crosswire.jsword.index.IndexStatus

/**
 * There's already an IndexManager, that's why the funky name
 */
class MBIndexManager(val indexManager: IndexManager) {

    /**
     * Do the hard work of actually building the book index.
     * Returns a PublishSubject<> that completes when the
     * index is complete. Also is nice enough to broadcast
     * what work is being done when.
     */
    fun buildIndex(b: Book): PublishSubject<IndexStatus> {
        val indexStatus: PublishSubject<IndexStatus> = PublishSubject.create();
        Observable.just(b)
                .observeOn(Schedulers.computation())
                .subscribe({
                    indexStatus.onNext(b.getIndexStatus())

                    indexManager scheduleIndexCreation b

                    indexStatus.onNext(b.getIndexStatus())
                    indexStatus.onCompleted()
                }, { Log.e("MBIndexManager", "Exception building index: $it", it) })

        return indexStatus
    }

    fun removeIndex(b: Book) = indexManager.deleteIndex(b)
}