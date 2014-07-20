package org.bspeice.minimalbible.activity.downloader.manager;

import android.util.Log;

import org.bspeice.minimalbible.Injector;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Manager to keep track of which books have been installed
 */
@Singleton
public class InstalledManager implements BooksListener {

    @Inject Books installedBooks;
    @Inject List<Book> installedBooksList;

    private String TAG = "InstalledManager";

    @Inject
    InstalledManager(Injector injector) {
        injector.inject(this);
        installedBooksList.addAll(installedBooks.getBooks());
        installedBooks.addBooksListener(this);
    }

    public boolean isInstalled(Book b) {
        return installedBooksList.contains(b);
    }

    public Observable<Book> getInstalledBooks() {
        // This method is needed to provide a fresher copy of what's installed
        // than Books.getInstalled() does.
        return Observable.from(installedBooksList);
    }

    @Override
    public void bookAdded(BooksEvent booksEvent) {
        Log.d(TAG, "Book added: " + booksEvent.getBook().toString());
        Book b = booksEvent.getBook();
        if (!installedBooksList.contains(b)) {
            installedBooksList.add(b);
        }
    }

    @Override
    public void bookRemoved(BooksEvent booksEvent) {
        Log.d(TAG, "Book removed: " + booksEvent.getBook().toString());
        Book b = booksEvent.getBook();
        if (installedBooksList.contains(b)) {
            installedBooksList.remove(b);
        }
    }

    /**
     * Remove a book from being installed.
     * Currently only supports books that have been installed outside the current application run.
     * Not quite sure why this is, but And-Bible exhibits the same behavior.
     * @param b The book to remove
     * @return Whether the book was removed.
     */
    public boolean removeBook(Book b) {
        try {
            // This worked in the past, but isn't now...
            // installedBooks.remove(b);
            Book realBook = installedBooks.getBook(b.getInitials());
            b.getDriver().delete(realBook);
            return true;
        } catch (BookException e) {
            Log.e("InstalledManager", "Unable to remove book (already uninstalled?): " + e.getLocalizedMessage());
            return false;
        }
    }
}
