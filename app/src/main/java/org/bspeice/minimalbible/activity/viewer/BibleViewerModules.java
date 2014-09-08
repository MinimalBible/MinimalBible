package org.bspeice.minimalbible.activity.viewer;

import android.util.Log;

import org.bspeice.minimalbible.activity.navigation.ExpListNavAdapter;
import org.bspeice.minimalbible.activity.viewer.bookutil.VersificationUtil;
import org.bspeice.minimalbible.service.book.VerseLookupModules;
import org.crosswire.jsword.book.Book;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by bspeice on 6/18/14.
 */
@Module(
        injects = {
                BibleViewer.class,
                BookFragment.class,
                ExpListNavDrawerFragment.class,
                ExpListNavAdapter.class
        },
        includes = VerseLookupModules.class
)
public class BibleViewerModules {
    BibleViewer activity;

    public BibleViewerModules(BibleViewer activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    BibleViewerPreferences providePrefs() {
        return Esperandro.getPreferences(BibleViewerPreferences.class, activity);
    }

    @Provides
    @Named("MainBook")
    Book provideMainBook(BookManager bookManager, final BibleViewerPreferences prefs) {
        final AtomicReference<Book> mBook = new AtomicReference<Book>(null);
        bookManager.getInstalledBooks()
                .first(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return book.getName().equals(prefs.defaultBookName());
                    }
                })
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        mBook.set(book);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("BibleViewerModules", throwable.getLocalizedMessage());
                    }
                });

        if (mBook.get() == null) {
            try {
                Book fallback;
                fallback = bookManager.getInstalledBooks()
                        .onErrorReturn(new Func1<Throwable, Book>() {
                            @Override
                            public Book call(Throwable throwable) {
                                // If there's no book installed, we can't select the main one...
                                return null;
                            }
                        })
                        .toBlocking().first();

                prefs.defaultBookName(fallback.getName());
                return fallback;
            } catch (NoSuchElementException e) {
                // If no books are installed, there's really nothing we can do...
                Log.d("BibleViewerModules", "No books are installed, so can't select a main book.");
                return null;
            }
        } else {
            return mBook.get();
        }
    }

    @Provides
    @Singleton
    BookManager bookManager() {
        return new BookManager();
    }

    @Provides
    VersificationUtil provideVersificationUtil() {
        return new VersificationUtil();
    }
}