package org.bspeice.minimalbible.activity.downloader.manager;

import android.util.Log;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manager to keep track of which books have been installed
 */
@Singleton
public class InstalledManager implements BooksListener {

    private Books installedBooks;
    private List<Book> installedBooksList;
    private String TAG = "InstalledManager";

    @Inject InstalledManager() {}

    /**
     * Register our manager to receive events on Book install
     * This is a relatively expensive operation,
     * so we don't put it in the constructor.
     */
    public void initialize() {
        //TODO: Move this to a true async, rather than separate initialize() function
        installedBooks = Books.installed();
        installedBooksList = installedBooks.getBooks();
        installedBooks.addBooksListener(this);
    }

    public boolean isInstalled(Book b) {
        if (installedBooks == null) {
            initialize();
        }
        return installedBooksList.contains(b);
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

    public void removeBook(Book b) {
        if (installedBooks == null) {
            initialize();
        }
        // Not sure why we need to call this multiple times, but...
        while (Books.installed().getBooks().contains(b)) {
            try {
                // This worked in the past, but isn't now...
                // installedBooks.remove(b);
                Book realBook = installedBooks.getBook(b.getInitials());
                b.getDriver().delete(realBook);
            } catch (BookException e) {
                Log.e("InstalledManager", "Unable to remove book (already uninstalled?): " + e.getLocalizedMessage());
            }
        }
    }
}
