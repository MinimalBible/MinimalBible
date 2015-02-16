package org.bspeice.minimalbible.activity.search

import org.crosswire.jsword.index.IndexManager
import org.crosswire.jsword.book.Book
import android.util.Log
import rx.schedulers.Schedulers
import rx.Observable

/**
 * There's already an IndexManager, that's why the funky name
 */
class MBIndexManager(val indexManager: IndexManager) {

    fun buildIndex(b: Book) {
        Observable.just(b)
                .observeOn(Schedulers.computation())
                .subscribe({
                    try {
                        Log.e("MBIndexManager", "Beginning index status: ${b.getIndexStatus()}")
                        indexManager scheduleIndexCreation b
                        Log.e("MBIndexManager", "Ending index status: ${b.getIndexStatus()}")
                    } catch (e: Exception) {
                        Log.e("MBIndexManager", "Exception building index: ${e}", e)
                    }
                }, {
                    Log.e("MBIndexManager", "Exception building index: $it", it)
                })
        Log.d("MBIndexManager", "Building index for ${b.getInitials()}")
    }

    fun removeIndex(b: Book) = indexManager.deleteIndex(b)
}