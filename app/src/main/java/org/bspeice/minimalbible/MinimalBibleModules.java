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
import org.crosswire.jsword.index.IndexStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Entry point for the default modules used by MinimalBible
 */
@Module(library = true)
public class MinimalBibleModules {
    MinimalBible app;

    public MinimalBibleModules(MinimalBible app) {
        this.app = app;
    }

    @Provides
    @Singleton
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
    @Singleton
    List<String> invalidBooks() {
        List<String> list = new ArrayList<>();
        list.add("ABU"); // Missing content
        list.add("ERen_no"); // Thinks its installed, when it isn't. Triggers NPE
        list.add("ot1nt2"); // Thinks its installed, when it isn't. Triggers NPE

        return list;
    }

    //TODO: Move this to a true async

    /**
     * Provide a raw reference to the books installed. Please don't use this, chances are
     * you should go through List<Book> since it excludes the invalid books.
     *
     * @return
     */
    @Provides
    @Singleton
    Books provideInstalledBooks() {
        return Books.installed();
    }

    /**
     * Use this to get the list of books installed, as filtered by what should be excluded
     *
     * @param b            The raw Books instance to get the installed list from
     * @param invalidBooks The books to exclude from usage
     * @return The books available for using
     */
    @Provides
    List<Book> provideInstalledBooks(Books b, final List<String> invalidBooks) {
        List<Book> rawBooks = b.getBooks();
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
    @Singleton
    BibleViewerPreferences providePrefs() {
        return Esperandro.getPreferences(BibleViewerPreferences.class, app);
    }

    @Provides
    @Named("MainBook")
    Book provideMainBook(BookManager bookManager, final BibleViewerPreferences prefs,
                         MBIndexManager indexManager) {
        final AtomicReference<Book> mBook = new AtomicReference<Book>(null);
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

        Book b = mBook.get();
        if (b.getIndexStatus() != IndexStatus.DONE) {
            indexManager.buildIndex(b);
        }

        return b;
    }

    @Provides
    @Singleton
    BookManager bookManager(List<String> exclude) {
        return new BookManager(exclude);
    }

    @Provides
    @Singleton
    IndexManager indexManager() {
        return IndexManagerFactory.getIndexManager();
    }

    @Provides
    MBIndexManager mbIndexManager(IndexManager indexManager) {
        return new MBIndexManager(indexManager);
    }
}
