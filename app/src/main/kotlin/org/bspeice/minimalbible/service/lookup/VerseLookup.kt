package org.bspeice.minimalbible.service.lookup

import org.crosswire.jsword.book.Book
import android.support.v4.util.LruCache
import rx.functions.Action1
import org.crosswire.jsword.passage.Verse
import rx.subjects.PublishSubject
import rx.schedulers.Schedulers
import org.bspeice.minimalbible.service.format.osisparser.OsisParser
import org.crosswire.jsword.book.getVersification

/**
 * Do the low-level work of getting a verse's content
 * This class is currently impossible to test because I can't mock Verse objects
 */
open class VerseLookup(val b: Book) : Action1<Verse> {

    val cache = VerseCache()
    /**
     * The listener servers to let other objects notify us we should pre-cache verses
     */
    val listener: PublishSubject<Verse> = PublishSubject.create();

    {
        listener.observeOn(Schedulers.io())
                .subscribe(this)
    }

    fun getVerseId(v: Verse) = v.getOrdinal()

    fun getJson(v: Verse): String =
            if (cache contains v)
                cache[getVerseId(v)]
            else {
                val content = doLookup(v)
                notify(v)
                content
            }

    /**
     * Perform the ugly work of getting the actual data for a verse
     * Note that we build the verse object, JS should be left to determine how
     * it is displayed.
     *
     * @param v The verse to look up
     * @return The JSON content of this verse
     */
    fun doLookup(v: Verse): String = OsisParser().getJson(b, v)
    fun doLookup(ordinal: Int): String = OsisParser()
            .getJson(b, Verse(b.getVersification(), ordinal))

    /**
     * Not necessary, but helpful if you let us know ahead of time we should pre-cache a verse.
     * For example, if something showed up in search results, it'd be helpful to start
     * looking up some of the results.
     *
     * @param v The verse we should pre-cache
     */
    fun notify(v: Verse) = listener onNext v

    /**
     * Let someone know if the cache contains a verse we want
     * Also provides a nice wrapper if the underlying cache isn't working properly.
     *
     * @param v The verse to check
     * @return Whether we can retrieve the verse from our cache
     */
    open fun contains(v: Verse) = cache[v.getOrdinal()] != null

    // IO Thread operations begin here

    /**
     * Someone was nice enough to let us know that a verse was recently called,
     * we should probably cache its neighbors!
     */
    override fun call(t1: Verse?) {

    }
}

open class VerseCache : LruCache<Int, String>(1000000) {

    fun getId(v: Verse) = v.getOrdinal()
    fun contains(v: Verse) = (this get getId(v)) != null
}