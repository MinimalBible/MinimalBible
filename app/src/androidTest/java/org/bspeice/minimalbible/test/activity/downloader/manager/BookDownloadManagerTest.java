package org.bspeice.minimalbible.test.activity.downloader.manager;

import android.util.Log;

import junit.framework.TestCase;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.activity.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

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

public class BookDownloadManagerTest extends TestCase implements Injector {

    ObjectGraph mObjectGraph;

    /**
     * Modules needed for this test case
     */
    @Module(injects = {BookDownloadManager.class,
            RefreshManager.class,
            BookDownloadManagerTest.class})
    public static class BookDownloadManagerTestModules {
        Injector i;

        BookDownloadManagerTestModules(Injector i) {
            this.i = i;
        }

        @Provides
        @Singleton
        Injector provideInjector() {
            return i;
        }

        @Provides @Singleton
        Books provideBooks() {
            return Books.installed();
        }

        @Provides @Singleton
        Collection<Installer> provideInstallers() {
            return new InstallManager().getInstallers().values();
        }
    }

    @Inject BookDownloadManager bookDownloadManager;
    @Inject RefreshManager refreshManager;
    @Inject Books installedBooks;

    @Override
    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public void setUp() {
        BookDownloadManagerTestModules modules = new BookDownloadManagerTestModules(this);
        mObjectGraph = ObjectGraph.create(modules);
        mObjectGraph.inject(this);
    }

    Observable<Book> installableBooks() {
        return refreshManager.getAvailableModulesFlattened()
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return !installedBooks.getBooks().contains(book);
                    }
                });
    }

    public void testInstallBook() throws Exception {
        final Book toInstall = installableBooks().toBlocking().first();

        bookDownloadManager.installBook(toInstall);

        final AtomicBoolean signal = new AtomicBoolean(false);
        bookDownloadManager.getDownloadEvents()
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

    public void testJobIdMatch() {
        final Book toInstall = installableBooks().toBlocking().first();
        final String jobName = BookDownloadManager.getJobId(toInstall);
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

        bookDownloadManager.installBook(toInstall);
        await().atMost(1, TimeUnit.SECONDS)
                .untilTrue(jobNameMatch);
    }
}