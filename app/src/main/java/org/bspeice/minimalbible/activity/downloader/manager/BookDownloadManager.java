package org.bspeice.minimalbible.activity.downloader.manager;

import android.content.Context;
import android.util.Log;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

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

    @Inject
    Provider<BookDownloadThread> dlThreadProvider;

    @Inject
    public BookDownloadManager(Injector injector) {
        bookMappings = new HashMap<String, Book>();
        inProgressDownloads = new HashMap<Book, DLProgressEvent>();
        JobManager.addWorkListener(this);
        injector.inject(this);
        Books.installed().addBooksListener(this);
    }

    public void installBook(Book b) {
        BookDownloadThread dlThread = dlThreadProvider.get();
        dlThread.downloadBook(b);
        addJob(BookDownloadThread.getJobId(b), b);
        downloadEvents.onNext(new DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING, b));
    }

    public void addJob(String jobId, Book b) {
        bookMappings.put(jobId, b);
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
                DLProgressEvent event = new DLProgressEvent(job.getWorkDone(),
                        job.getTotalWork(), b);
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
