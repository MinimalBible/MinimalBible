package org.bspeice.minimalbible;

import android.app.Application;
import android.util.Log;

import org.bspeice.minimalbible.activity.search.MBIndexManager;
import org.bspeice.minimalbible.activity.viewer.BibleViewerPreferences;
import org.bspeice.minimalbible.service.manager.BookManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Entry point for the default modules used by MinimalBible
 */
@Module
public class MinimalBibleModules {
    MinimalBible app;

    public MinimalBibleModules(MinimalBible app) {
        this.app = app;
    }

    @Provides
    Application provideApplication() {
        return app;
    }

    /**
     * Provide a list of book names that are known bad. This can be because they trigger NPE,
     * or are just missing lots of content, etc.
     *
     * @return the list of books (by name) to ignore
     */
    @Provides
    List<String> invalidBooks() {
        List<String> list = new ArrayList<>();
        list.add("ABU"); // Missing content
        list.add("ERen_no"); // Thinks its installed, when it isn't. Triggers NPE
        list.add("ot1nt2"); // Thinks its installed, when it isn't. Triggers NPE

        return list;
    }

    /**
     * Use this to get the list of books installed, as filtered by what should be excluded
     *
     * @param invalidBooks The books to exclude from usage
     * @return The books available for using
     */
    @Provides
    List<Book> provideInstalledBooks(final List<String> invalidBooks) {
        List<Book> rawBooks = Books.installed().getBooks();
        return Observable.from(rawBooks)
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return !invalidBooks.contains(book.getInitials());
                    }
                })
                .toList().toBlocking().first();
    }

    @Provides
    BibleViewerPreferences providePrefs() {
        return Esperandro.getPreferences(BibleViewerPreferences.class, app);
    }

    /**
     * TODO: Assume a book exists, throw an error if nothing available
     *
     * @param bookManager
     * @param prefs
     * @return
     */
    @Provides
    Book provideMainBook(BookManager bookManager, final BibleViewerPreferences prefs) {
        final AtomicReference<Book> mBook = new AtomicReference<>(null);
        bookManager.getInstalledBooks()
                .first(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return book.getInitials().equals(prefs.defaultBookInitials());
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

                prefs.defaultBookInitials(fallback.getName());
                mBook.set(fallback);
            } catch (NoSuchElementException e) {
                // If no books are installed, there's really nothing we can do...
                Log.d("BibleViewerModules", "No books are installed, so can't select a main book.");
                return null;
            }
        }

        return mBook.get();
    }

    @Provides
    BookManager bookManager(List<String> exclude) {
        return new BookManager(exclude);
    }

    @Provides
    IndexManager indexManager() {
        return IndexManagerFactory.getIndexManager();
    }

    @Provides
    MBIndexManager mbIndexManager(IndexManager indexManager) {
        return new MBIndexManager(indexManager);
    }
}
