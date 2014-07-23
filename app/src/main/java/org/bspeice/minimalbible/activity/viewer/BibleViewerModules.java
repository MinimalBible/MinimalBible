package org.bspeice.minimalbible.activity.viewer;

import android.util.Log;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.activity.viewer.bookutil.VersificationUtil;
import org.crosswire.jsword.book.Book;

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
                ViewerNavDrawerFragment.class,
                BibleNavAdapter.class
        },
        library = true
)
public class BibleViewerModules {
    BibleViewer activity;

    public BibleViewerModules(BibleViewer activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    Injector provideInjector() {
        return activity;
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
            Book fallback;
            fallback = bookManager.getInstalledBooks()
                    .toBlocking().first();

            prefs.defaultBookName(fallback.getName());
            return fallback;

        } else {
            return mBook.get();
        }
    }

    @Provides
    VersificationUtil provideVersificationUtil() {
        return new VersificationUtil();
    }
}