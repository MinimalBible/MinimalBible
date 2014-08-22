package org.bspeice.minimalbible.test.activity.downloader.manager;

import android.util.Log;

import junit.framework.TestCase;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.activity.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activity.downloader.manager.InstalledManager;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Test the InstalledManager
 * Currently due to limitations with JSword (which I'm currently investigating) you can't delete
 * books without restarting the application. That is, if you install it, there must be a restart
 * in between it being deleted. Unfortunately, that means that this TestCase really can't guarantee
 * much, since I can't install a book at runtime to be removed.
 */
public class InstalledManagerTest extends TestCase implements Injector {
    ObjectGraph mObjectGraph;

    @Module(injects = {InstalledManager.class,
            InstalledManagerTest.class,
            RefreshManager.class,
            BookDownloadManager.class})
    static class IMTestModules {
        Injector i;
        public IMTestModules(Injector i) {
            this.i = i;
        }

        @Provides @Singleton
        Injector provideInjector() {
            return this.i;
        }

        @Provides @Singleton
        Books provideInstalledBooks() {
            return Books.installed();
        }

        @Provides
        List<Book> provideInstalledBooksList(Books b) {
            return b.getBooks();
        }

        @Provides @Singleton
        Collection<Installer> provideInstallers() {
            return new InstallManager().getInstallers().values();
        }
    }

    @Inject InstalledManager iM;
    @Inject Books installedBooks;

    @Override
    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public void setUp() throws Exception {
        super.setUp();
        mObjectGraph = ObjectGraph.create(new IMTestModules(this));
        mObjectGraph.inject(this);

        // Unfortunately, unless something is already installed, we can't actually remove anything
        int count = getInstalledBooks().count().toBlocking().first();

        if (count <= 0) {
            Log.w("InstalledManagerTest", "No books available, test can not guarantee anything.");
        }
    }

    public Observable<Book> getInstalledBooks() {
       /* The golden copy for testing of what's installed.
       NOTE: Currently, I have yet to find a guaranteed way to immediately delete
       a book that is freshly installed. While the tests are semantically correct, unfortunately,
       this test case specifically doesn't guarantee much of anything.
       */
        return Observable.from(installedBooks.getBooks())
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        // Not sure why, but this book can't be deleted...
                        return book.getDriver().isDeletable(book);
                    }
                });
    }

    public void testIsInstalled() throws Exception {
        final AtomicBoolean foundMismatch = new AtomicBoolean(false);
        getInstalledBooks()
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        // Skip if we've already found a mismatch
                        if (!foundMismatch.get()) {
                            // We've already filtered to what we know is installed,
                            // so set to true if iM doesn't think it's installed.
                            foundMismatch.set(!iM.isInstalled(book));
                        }
                    }
                });
        assertFalse(foundMismatch.get());
    }

    /**
     * Test that we can remove a book. Currently this test is neutered until I can fix
     * issues with @link{getInstalledBooks}.
     * @throws Exception
     */
    public void testRemoveBook() throws Exception {
        final Book book = getInstalledBooks().toBlocking().first();

        final AtomicBoolean didRemove = new AtomicBoolean(false);

        installedBooks.addBooksListener(new BooksListener() {
            @Override
            public void bookAdded(BooksEvent ev) {

            }
            @Override
            public void bookRemoved(BooksEvent ev) {
                if (ev.getBook().equals(book)) {
                    didRemove.set(true);
                }
            }
        });

        iM.removeBook(book);
        if (!didRemove.get()) {
            Log.w("testRemoveBook", "Could not remove book, not necessarily fatal.");
        }
    }
}