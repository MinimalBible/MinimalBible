package org.bspeice.minimalbible.activity.downloader.manager;

import android.util.Log;

import org.bspeice.minimalbible.Injector;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Wrapper to convert JSword progress events to MinimalBible EventBus-based
 */
//TODO: Make sure that jobs have the correct name
//TODO: Install indexes for Bibles
@Singleton
public class BookDownloadManager implements WorkListener, BooksListener {
    /**
     * Mapping of Job ID to the EventBus we should trigger progress on
     */
    private final Map<String, Book> bookMappings;
    /**
     * Cached copy of downloads in progress so views displaying this info can get it quickly.
     */
    private final Map<Book, DLProgressEvent> inProgressDownloads;
    private final PublishSubject<DLProgressEvent> downloadEvents = PublishSubject.create();
    @Inject Books installedBooks;
    @Inject RefreshManager refreshManager;

    @Inject
    public BookDownloadManager(Injector injector) {
        bookMappings = new HashMap<String, Book>();
        inProgressDownloads = new HashMap<Book, DLProgressEvent>();
        JobManager.addWorkListener(this);
        injector.inject(this);
        installedBooks.addBooksListener(this);
    }

    /**
     * Build what the installer creates the job name as.
     * Likely prone to be brittle.
     *
     * @param b The book to predict the download job name of
     * @return The name of the job that will/is download/ing this book
     */

    public static String getJobId(Book b) {
        return "INSTALL_BOOK-" + b.getInitials();
    }

    public void installBook(Book b) {
        downloadBook(b);
        addJob(getJobId(b), b);
        downloadEvents.onNext(new DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING, b));
    }

    public void addJob(String jobId, Book b) {
        bookMappings.put(jobId, b);
    }

    public void downloadBook(final Book b) {
        // So, the JobManager can't be injected, but we'll make do

        // First, look up where the Book came from
        Observable.just(refreshManager.installerFromBook(b))
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Observable<Installer>>() {
                    @Override
                    public void call(Observable<Installer> installerObservable) {
                        try {
                            installerObservable.toBlocking().first().install(b);
                        } catch (InstallException e) {
                            e.printStackTrace();
                        }
                    }
                });

        getDownloadEvents()
                .onNext(new DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING, b));
    }

    @Override
    public void workProgressed(WorkEvent ev) {
        Progress job = ev.getJob();
        Log.d("BookDownloadManager", "Download in progress: " + job.getJobID() + " - " + job.getJobName() + " " + job.getWorkDone() + "/" + job.getTotalWork());
        if (bookMappings.containsKey(job.getJobID())) {
            Book b = bookMappings.get(job.getJobID());

            if (job.getWorkDone() == job.getTotalWork()) {
                // Download is complete
                inProgressDownloads.remove(bookMappings.get(job.getJobID()));
                bookMappings.remove(job.getJobID());
                downloadEvents.onNext(new DLProgressEvent(DLProgressEvent.PROGRESS_COMPLETE, b));
            } else {
                // Track the ongoing download
                DLProgressEvent event = new DLProgressEvent(
                        (job.getWorkDone() / job.getTotalWork()) * 100,
                        b);
                inProgressDownloads.put(b, event);
                downloadEvents.onNext(event);
            }
        }
    }

    /**
     * Check the status of a book download in progress.
     * @param b The book to get the current progress of
     * @return The most recent DownloadProgressEvent for the book, or null if not downloading
     */
    public DLProgressEvent getInProgressDownloadProgress(Book b) {
        if (inProgressDownloads.containsKey(b)) {
            return inProgressDownloads.get(b);
        } else {
            return null;
        }
    }

    public PublishSubject<DLProgressEvent> getDownloadEvents() {
        return downloadEvents;
    }

    @Override
    public void workStateChanged(WorkEvent ev) {
        Log.d("BookDownloadManager", ev.toString());
    }

    @Override
    public void bookAdded(BooksEvent booksEvent) {
        // It's possible the install finished before we received a progress event for it,
        // we handle that case here.
        Book b = booksEvent.getBook();
        Log.d("BookDownloadManager", "Book added: " + b.getName());
        if (inProgressDownloads.containsKey(b)) {
            inProgressDownloads.remove(b);
        }
        // Not sure why, but the inProgressDownloads might not have our book,
        // so we always trigger the PROGRESS_COMPLETE event.
        // TODO: Make sure all books get to the inProgressDownloads
        downloadEvents.onNext(new DLProgressEvent(DLProgressEvent.PROGRESS_COMPLETE, b));
    }

    @Override
    public void bookRemoved(BooksEvent booksEvent) {
        // Not too worried about this just yet.
    }
}
