package org.bspeice.minimalbible.service.lookup

import org.crosswire.jsword.book.Book
import android.support.v4.util.LruCache
import rx.functions.Action1
import org.crosswire.jsword.passage.Verse
import rx.subjects.PublishSubject
import rx.schedulers.Schedulers
import org.crosswire.jsword.book.BookData
import org.bspeice.minimalbible.service.format.osisparser.OsisParser

/**
 * Created by bspeice on 11/12/14.
 */
open class VerseLookup(val b: Book,
                       val cache: LruCache<Int, String> = LruCache(1000000)) : Action1<Verse> {
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
            if (contains(v))
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
    fun doLookup(v: Verse): String {
        val data = BookData(b, v)
        val provider = data.getSAXEventProvider()
        val handler = OsisParser()
        handler.verse = v
        provider provideSAXEvents handler
        return handler.getJson()
    }

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
    fun contains(v: Verse) = cache[v.getOrdinal()] != null

    // IO Thread operations begin here

    /**
     * Someone was nice enough to let us know that a verse was recently called,
     * we should probably cache its neighbors!
     */
    override fun call(t1: Verse?) {

    }
}

class DefaultVerseLookup(b: Book) : VerseLookup(b) {}
