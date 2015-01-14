package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.jsword.book.Book

/**
 * Created by bspeice on 11/11/14.
 */
data class DLProgressEvent(val bookProgress: Int,
                           val indexProgress: Int,
                           val b: Book) {
    class object {
        val PROGRESS_COMPLETE = 100
        val PROGRESS_BEGINNING = 0

        /**
         * Build a DLProgressEvent that is just beginning
         * Mostly just a nice shorthand
         */
        fun beginningEvent(b: Book) = DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING,
                DLProgressEvent.PROGRESS_BEGINNING, b)
    }

    val averageProgress: Int
        get() = (bookProgress + indexProgress) / 2

    fun toCircular() = (averageProgress.toFloat() * 360 / 100).toInt()
}
