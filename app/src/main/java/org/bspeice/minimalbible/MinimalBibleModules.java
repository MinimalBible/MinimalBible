package org.bspeice.minimalbible;

import android.app.Application;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
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

    @Provides @Singleton
    Application provideApplication() {
        return app;
    }

    /**
     * Provide a list of book names that are known bad. This can be because they trigger NPE,
     * or are just missing lots of content, etc.
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
                        return invalidBooks.contains(book.getInitials());
                    }
                })
                .toList().toBlocking().first();
    }
}
