package org.bspeice.minimalbible.service.book;

import android.support.v4.util.LruCache;
import android.util.Log;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.service.osisparser.OsisParser;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Verse;
import org.xml.sax.SAXException;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * This class has a simple purpose, but implements the dirty work needed to make it happen.
 * The idea is this: someone wants the text for a verse, we look it up quickly.
 * This means aggressive caching, cache prediction, and classes letting us know that some verses
 * may be needed in the future (i.e. searching).
 * <p/>
 * There is one VerseLookupService per Book, but multiple VerseLookupServices can work with
 * the same book. Because the actual caching mechanism is disk-based, we're safe.
 * <p/>
 * TODO: Statistics on cache hits/misses vs. verses cached
 */
public class VerseLookupService implements Action1<Verse> {


    Book book;

    @Inject
    LruCache<String, String> cache;
    /**
     * The listener is responsible for delegating calls to cache verses.
     * This way, @notifyVerse can just tell the listener what's what,
     * and the listener can delegate to another thread.
     */
    private PublishSubject<Verse> listener = PublishSubject.create();

    public VerseLookupService(Injector i, Book b) {
        listener.subscribeOn(Schedulers.io())
                .subscribe(this);
        this.book = b;
        i.inject(this);
    }

    /**
     * Get the text for a corresponding verse
     * First, check the cache. If that doesn't exist, manually get the verse.
     * In all cases, notify that we're looking up a verse so we can get the surrounding ones.
     *
     * @param v The verse to look up
     * @return The HTML text for this verse (\<p\/>)
     */
    public String getHTMLVerse(Verse v) {
        if (contains(v)) {
            return cache.get(getEntryName(v));
        } else {
            // The awkward method calls below are so notifyVerse doesn't
            // call the same doVerseLookup
            String verseContent = doVerseLookup(v);
            notifyVerse(v);
            return verseContent;
        }
    }

    /**
     * Perform the ugly work of getting the actual data for a verse
     * TODO: Return a verse object, JS should be left to templating.
     * @param v The verse to look up
     * @return The string content of this verse
     */
    public String doVerseLookup(Verse v) {
        BookData bookData = new BookData(book, v);
        try {
            SAXEventProvider provider = bookData.getSAXEventProvider();
//            OsisToHtmlSaxHandler handler = new OsisToHtmlSaxHandler(new OsisToHtmlParameters());
            OsisParser handler = new OsisParser(v);
            provider.provideSAXEvents(handler);
            Log.e(this.getClass().getName(), handler.toString());
            return handler.getContent().getContent();
        } catch (BookException e) {
            e.printStackTrace();
            return "Unable to locate " + v.toString() + "!";
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Not necessary, but helpful if you let us know ahead of time we should pre-cache a verse.
     * For example, if something showed up in search results, it'd be helpful to start
     * looking up some of the results.
     *
     * @param v The verse we should pre-cache
     */
    public void notifyVerse(Verse v) {
        listener.onNext(v);
    }

    /**
     * Let someone know if the cache contains a verse we want
     * Also provides a nice wrapper if the underlying cache isn't working properly.
     *
     * @param v The verse to check
     * @return Whether we can retrieve the verse from our cache
     */
    public boolean contains(Verse v) {
        return cache.get(getEntryName(v)) != null;
    }

    /**
     * Given a verse, what should it's name in the cache be?
     * Example: Matthew 7:7 becomes:
     * MAT_7_7
     *
     * @param v The verse we need to generate a name for
     * @return The name this verse should have in the cache
     */
    private String getEntryName(Verse v) {
        return v.getBook().toString() + "_" +
                v.getChapter() + "_" +
                v.getVerse();
    }

    /*------------------------------------------------------------------------
        IO Thread operations below
    ------------------------------------------------------------------------*/

    /**
     * The listener has let us know that we need to look up a verse. So, look up
     * that one first, and get its surrounding verses as well just in case.
     * We can safely assume we are not on the main thread.
     *
     * @param verse The verse we need to look up
     */

    @Override
    public void call(Verse verse) {

    }
}
