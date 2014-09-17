package org.bspeice.minimalbible.activity.viewer

import org.crosswire.jsword.passage.Verse
import org.bspeice.minimalbible.service.book.VerseLookupService
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.versification.getVersification
import java.util.ArrayList
import android.util.Log

/**
 * Created by bspeice on 9/14/14.
 */

class BibleViewClient(b: Book, lookup: VerseLookupService) : WebViewClient() {
    val b = b
    val lookup = lookup

    // We can receive and return only primitives and Strings. Still means we can use JSON :)
    JavascriptInterface fun getVerse(ordinal: Int): String {
        val v = Verse(b.getVersification(), ordinal)
        return lookup.getJsonVerse(v) as String
    }

    JavascriptInterface fun getVerses(first: Int, count: Int): String {
        Log.e("getVerses", "First: " + first + " count: " + count)
        val verses: MutableList<String> = ArrayList<String>()
        for (i in first..first + count - 1) {
            verses.add(getVerse(i))
        }
        return verses.toString()
    }
}