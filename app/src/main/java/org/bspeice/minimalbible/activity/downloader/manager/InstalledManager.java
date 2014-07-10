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
        installedBooks.addBooksListener(this);
    }

    public boolean isInstalled(Book b) {
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
