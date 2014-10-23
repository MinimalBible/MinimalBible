package org.bspeice.minimalbible.test.activity.downloader.manager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MBTestCase;
import org.bspeice.minimalbible.activity.downloader.DownloadPrefs;
import org.bspeice.minimalbible.activity.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activity.downloader.manager.InstalledManager;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.mockito.Mockito;

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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the InstalledManager
 * Currently due to limitations with JSword (which I'm currently investigating) you can't delete
 * books without restarting the application. That is, if you install it, there must be a restart
 * in between it being deleted. Unfortunately, that means that this TestCase really can't guarantee
 * much, since I can't install a book at runtime to be removed.
 */
public class InstalledManagerTest extends MBTestCase implements Injector {
    ObjectGraph mObjectGraph;
    @Inject
    InstalledManager iM;
    @Inject
    Books installedBooks;

    @Override
    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    @Override
    public void setUp() {
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

    @Module(injects = {InstalledManager.class,
            InstalledManagerTest.class,
            RefreshManager.class,
            BookDownloadManager.class})
    @SuppressWarnings("unused")
    static class IMTestModules {
        Injector i;
        ConnectivityManager manager;
        DownloadPrefs prefs;
        public IMTestModules(Injector i) {
            this.i = i;

            // Set reasonable defaults for the manager and preferences, can over-ride if need-be
            manager = mock(ConnectivityManager.class);
            NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

            when(manager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
            when(mockNetworkInfo.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);

            prefs = mock(DownloadPrefs.class);
        }

        @Provides
        @Singleton
        Injector provideInjector() {
            return this.i;
        }

        @Provides
        @Singleton
        Books provideInstalledBooks() {
            return Books.installed();
        }

        @Provides
        List<Book> provideInstalledBooksList(Books b) {
            return b.getBooks();
        }

        @Provides
        @Singleton
        Collection<Installer> provideInstallers() {
            return new InstallManager().getInstallers().values();
        }

        void setConnectivityManager(ConnectivityManager manager) {
            this.manager = manager;
        }

        void setPrefs(DownloadPrefs prefs) {
            this.prefs = prefs;
        }

        @Provides
        @Singleton
        RefreshManager refreshManager(Collection<Installer> installers) {
            return new RefreshManager(installers,
                    prefs, manager);
        }
    }
}