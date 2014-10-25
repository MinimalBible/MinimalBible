package org.bspeice.minimalbible.activity.viewer

import org.crosswire.jsword.passage.Verse
import org.bspeice.minimalbible.service.book.VerseLookupService
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import org.crosswire.jsword.book.Book
import java.util.ArrayList
import android.util.Log
import rx.subjects.PublishSubject
import org.crosswire.jsword.book.getVersification

/**
 * Created by bspeice on 9/14/14.
 */

class BibleViewClient(val b: Book, val lookup: VerseLookupService,
                      val subject: PublishSubject<String>?) : WebViewClient() {

    // We can receive and return only primitives and Strings. Still means we can use JSON :)
    JavascriptInterface fun getVerse(ordinal: Int): String {
        val v = Verse(b.getVersification(), ordinal)
        // TODO: WebView should notify us what verse it's on
        subject?.onNext(v.getBook().toString() + " " + v.getChapter() + ":" + v.getVerse())
        return lookup.getJsonVerse(v)
    }

    JavascriptInterface fun getVerses(first: Int, count: Int): String {
        Log.e("getVerses", "First: " + first + " count: " + count)
        val verses: MutableList<String> = ArrayList<String>()
        var trueCount: Int
        var trueFirst: Int
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
        Log.e("getVerses", "return verses size: " + verses.size.toString())
        return verses.toString()
    }
}