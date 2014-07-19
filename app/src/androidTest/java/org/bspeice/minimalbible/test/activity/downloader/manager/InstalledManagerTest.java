package org.bspeice.minimalbible.test.activity.downloader.manager;

import android.util.Log;

import junit.framework.TestCase;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.activity.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activity.downloader.manager.InstalledManager;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.sword.SwordBook;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.jayway.awaitility.Awaitility.await;

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
    @Inject BookDownloadManager bDM;
    @Inject RefreshManager rM;
    @Inject Books installedBooks;

    @Override
    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public void setUp() throws Exception {
        super.setUp();
        mObjectGraph = ObjectGraph.create(new IMTestModules(this));
        mObjectGraph.inject(this);

        // Guarantee something is installed
        getInstalledBooks()
                .count()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer count) {
                        if (count <= 0) {
                            Log.i("InstalledManagerTest", "Nothing installed!");
                            final AtomicBoolean isInstalled = new AtomicBoolean(false);
                            final Book toInstall = rM.getAvailableModulesFlattened().toBlocking().first();
                            bDM.installBook(toInstall);
                            bDM.getDownloadEvents()
                                    .subscribe(new Action1<DLProgressEvent>() {
                                        @Override
                                        public void call(DLProgressEvent dlProgressEvent) {
                                            if (dlProgressEvent.getProgress() == DLProgressEvent.PROGRESS_COMPLETE &&
                                                    dlProgressEvent.getB().getName().equals(toInstall.getName())) {

                                                isInstalled.set(true);
                                            }
                                        }
                                    });
                            await().atMost(30, TimeUnit.SECONDS)
                                    .untilTrue(isInstalled);
                        }
                    }
                });
    }

    public Observable<Book> getInstalledBooks() {
       /* The golden copy for testing of what's installed.
       NOTE: Currently, I have yet to find a guaranteed way to know if a book
       is installed or not. So while the test cases are semantically correct,
       nothing is actually proven until I can guarantee this list is correct.
       */
        // TODO: Guarantee that we return newly-installed books
        return Observable.from(installedBooks.getBooks())
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return book.getDriver().isDeletable(book);
                    }
                })
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        // Not sure why, but this book can't be deleted...
                        return !book.getInitials().equals("ot1nt2");
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
        final AtomicBoolean isRemoved = new AtomicBoolean(false);
        getInstalledBooks()
                .first()
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        iM.removeBook(book);

                        // The AbstractBook returns false all the time, make sure we have
                        // an actual implementation
                        Log.w("testRemoveBook", book.getInitials());
                        isRemoved.set(!book.getDriver().isDeletable(book) &&
                                book instanceof SwordBook);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        fail(throwable.getLocalizedMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        assertTrue(isRemoved.get());
                    }
                });
    }
}