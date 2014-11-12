package org.bspeice.minimalbible.activity.downloader.manager

import android.util.Log;

import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Single point of authority for what is being downloaded and its progress
 */
//TODO: Install indexes for Bibles
class BookDownloadManager(val installedBooks: Books, val rM: RefreshManager) :
        WorkListener, BooksListener {

    /**
     * Cached copy of downloads in progress so views displaying this info can get it quickly.
     */
    // TODO: Combine to one map
    var bookMappings: MutableMap<String, Book> = hashMapOf()
    var inProgressDownloads: MutableMap<Book, DLProgressEvent> = hashMapOf()
    val downloadEvents: PublishSubject<DLProgressEvent> = PublishSubject.create();

    {
        JobManager.addWorkListener(this)
        installedBooks.addBooksListener(this)
    }

    /**
     * Build what the installer creates the job name as.
     * This technically could be static, but Kotlin won't have none of that.
     *
     * @param b The book to predict the download job name of
     * @return The name of the job that will/is download/ing this book
     */
    fun getJobId(b: Book) = "INSTALL_BOOK-" + b.getInitials()

    fun installBook(b: Book) {
        downloadBook(b)
        addJob(getJobId(b), b)
        downloadEvents onNext DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING, b)
    }

    fun addJob(jobId: String, b: Book) {
        bookMappings.put(jobId, b)
    }

    fun downloadBook(b: Book) {
        // First, look up where the Book came from
        Observable.just(rM installerFromBook b)
                .subscribeOn(Schedulers.io())
                .subscribe { it.toBlocking().first().install(b) }

        downloadEvents onNext DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING, b)
    }

    // TODO: I have a strange feeling I can simplify this further...
    override fun workProgressed(ev: WorkEvent) {
        val job = ev.getJob()
        bookMappings.filter { it.getKey() == job.getJobID() }
                .map {
                    val event = DLProgressEvent(job.getWorkDone() / job.getTotalWork() * 100,
                            it.getValue())
                    downloadEvents onNext event

                    if (job.getWorkDone() == job.getTotalWork()) {
                        inProgressDownloads remove bookMappings.get(job.getJobID())
                        bookMappings remove job.getJobID()
                    } else
                        inProgressDownloads.put(it.getValue(), event)
                }
    }

    /**
     * Check the status of a book download in progress.
     * @param b The book to get the current progress of
     * @return The most recent DownloadProgressEvent for the book, or null if not downloading
     */
    fun getDownloadProgress(b: Book) = inProgressDownloads.get(b)

    override fun workStateChanged(ev: WorkEvent) {
        Log.d("BookDownloadManager", ev.toString())
    }

    override fun bookAdded(booksEvent: BooksEvent) {
        // It's possible the install finished before we received a progress event for it,
        // we handle that case here.
        val b = booksEvent.getBook()
        Log.d("BookDownloadManager", "Book added: " + b.getName())
        inProgressDownloads remove b

        // Not sure why, but the inProgressDownloads might not have our book,
        // so we always trigger the PROGRESS_COMPLETE event.
        downloadEvents onNext DLProgressEvent(DLProgressEvent.PROGRESS_COMPLETE, b)
    }

    override fun bookRemoved(booksEvent: BooksEvent) {
    }
}

