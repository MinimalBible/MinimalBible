package org.bspeice.minimalbible.test.activity.downloader.manager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.activity.downloader.DownloadPrefs;
import org.bspeice.minimalbible.activity.downloader.manager.BookManager;
import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.jayway.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

// TODO: Fix @Ignore'd tests
public class BookManagerTest implements Injector {

    ObjectGraph mObjectGraph;
    @Inject
    BookManager bookManager;
    @Inject
    RefreshManager refreshManager;
    @Inject
    Books installedBooks;

    @Override
    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    @Before
    public void setUp() {
        BookDownloadManagerTestModules modules = new BookDownloadManagerTestModules(this);
        mObjectGraph = ObjectGraph.create(modules);
        mObjectGraph.inject(this);
    }

    Observable<Book> installableBooks() {
        return refreshManager.getFlatModules()
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return !installedBooks.getBooks().contains(book);
                    }
                });
    }

    @Ignore
    public void testInstallBook() throws Exception {
        final Book toInstall = installableBooks().toBlocking().first();

        bookManager.installBook(toInstall);

        final AtomicBoolean signal = new AtomicBoolean(false);
        bookManager.getDownloadEvents()
                .subscribe(new Action1<DLProgressEvent>() {
                    @Override
                    public void call(DLProgressEvent dlProgressEvent) {
                        if (dlProgressEvent.getB().getInitials().equals(toInstall.getInitials())
                                && dlProgressEvent.getProgress() == DLProgressEvent.PROGRESS_COMPLETE) {
                            signal.set(true);
                        }
                    }
                });

        await().atMost(60, TimeUnit.SECONDS)
                .untilTrue(signal);
    }

    @Ignore
    public void testJobIdMatch() {
        final Book toInstall = installableBooks().toBlocking().first();
        final String jobName = bookManager.getJobId(toInstall);
        final AtomicBoolean jobNameMatch = new AtomicBoolean(false);

        JobManager.addWorkListener(new WorkListener() {
            @Override
            public void workProgressed(WorkEvent ev) {
                Log.d("testJobIdMatch", ev.getJob().getJobID() + " " + jobName);
                if (ev.getJob().getJobID().equals(jobName)) {
                    jobNameMatch.set(true);
                }
            }

            @Override
            public void workStateChanged(WorkEvent ev) {
            }
        });

        bookManager.installBook(toInstall);
        await().atMost(5, TimeUnit.SECONDS)
                .untilTrue(jobNameMatch);
    }

    @Test
    public void testLocalListUpdatedAfterAdd() {
        Book mockBook = mock(Book.class);
        BooksEvent event = mock(BooksEvent.class);
        when(event.getBook()).thenReturn(mockBook);

        bookManager.bookAdded(event);
        assertTrue(bookManager.getInstalledBooksList().contains(mockBook));
    }

    /**
     * This test requires deep knowledge of how to remove a book in order to test,
     * but the Kotlin interface is nice!
     */
    @Test
    public void testLocalListUpdatedAfterRemove() throws BookException {
        BookDriver driver = mock(BookDriver.class);

        Book mockBook = mock(Book.class);
        Book secondMockBook = mock(Book.class);
        when(mockBook.getDriver()).thenReturn(driver);

        BooksEvent event = mock(BooksEvent.class);
        when(event.getBook()).thenReturn(mockBook);

        bookManager.getInstalledBooksList().add(mockBook);
        assertTrue(bookManager.getInstalledBooksList().contains(mockBook));
        bookManager.removeBook(mockBook, secondMockBook);
        assertFalse(bookManager.getInstalledBooksList().contains(mockBook));
        verify(driver, times(1)).delete(secondMockBook);
    }

    /**
     * Make sure that when workProgressed is fired, the correct progress event gets triggered
     * Previously, integer roundoff errors led to some strange results
     */
    @Test
    public void testWorkProgressedCorrectProgress() {
        Book mockBook = mock(Book.class);
        when(mockBook.getInitials()).thenReturn("mockBook");
        String bookJobName = bookManager.getJobId(mockBook);
        bookManager.getBookMappings().put(bookJobName, mockBook);

        // Percent to degrees
        int workProgress = 1;
        int totalWork = 2;
        final int circularProgress = 180;
        WorkEvent ev = mock(WorkEvent.class);
        Progress p = mock(Progress.class);

        when(p.getJobID()).thenReturn(bookJobName);
        when(p.getWorkDone()).thenReturn(workProgress);
        when(p.getTotalWork()).thenReturn(totalWork);

        when(ev.getJob()).thenReturn(p);

        final AtomicBoolean progressCorrect = new AtomicBoolean(false);
        bookManager.getDownloadEvents()
                .subscribe(new Action1<DLProgressEvent>() {
                    @Override
                    public void call(DLProgressEvent dlProgressEvent) {
                        progressCorrect.set(circularProgress == dlProgressEvent.toCircular());
                    }
                });

        bookManager.workProgressed(ev);

        await().atMost(10, TimeUnit.SECONDS)
                .untilTrue(progressCorrect);
    }

    /**
     * Modules needed for this test case
     */
    @Module(injects = {BookManager.class,
            RefreshManager.class,
            BookManagerTest.class})
    @SuppressWarnings("unused")
    public static class BookDownloadManagerTestModules {
        Injector i;
        ConnectivityManager manager;
        DownloadPrefs prefs;

        BookDownloadManagerTestModules(Injector i) {
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
        Books provideBooks() {
            return Books.installed();
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

        @Provides
        @Singleton
        BookManager bookDownloadManager(Books installed, RefreshManager rm) {
            return new BookManager(installed, rm);
        }
    }
}