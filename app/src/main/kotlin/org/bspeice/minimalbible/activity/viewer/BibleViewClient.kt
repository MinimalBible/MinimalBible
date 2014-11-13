package org.bspeice.minimalbible.activity.viewer

import org.crosswire.jsword.passage.Verse
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import org.crosswire.jsword.book.Book
import android.util.Log
import rx.subjects.PublishSubject
import org.crosswire.jsword.book.getVersification
import org.bspeice.minimalbible.service.lookup.VerseLookup

/**
 * Created by bspeice on 9/14/14.
 */

class BibleViewClient(val b: Book, val lookup: VerseLookup,
                      val subject: PublishSubject<String>?) : WebViewClient() {

    // We can receive and return only primitives and Strings. Still means we can use JSON :)
    JavascriptInterface fun getVerse(ordinal: Int): String {
        val v = Verse(b.getVersification(), ordinal)
        // TODO: WebView should notify us what verse it's on
        subject?.onNext("${v.getBook()} ${v.getChapter()}:${v.getVerse()}")
        return lookup getJson v
    }

    JavascriptInterface fun getVerses(first: Int, count: Int): String {
        Log.e("getVerses", "First: $first count: $count")
        val verses: MutableList<String> = linkedListOf()
        val trueCount: Int
        val trueFirst: Int
        when {
            first < 0 - count -> return ""
            first < 0 -> {
                trueCount = count + first // Equivalent to count - abs(first)
                trueFirst = 0
            }
            else -> {
                trueCount = count
                trueFirst = first
            }
        }

        for (i in trueFirst..trueFirst + trueCount - 1) {
            verses.add(getVerse(i))
        }
        Log.e("getVerses", "return verses size: ${verses.size}")
        return verses.toString()
    }
}