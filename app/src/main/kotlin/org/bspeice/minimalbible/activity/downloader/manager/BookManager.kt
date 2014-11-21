package org.bspeice.minimalbible.activity.downloader.manager

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
import org.crosswire.jsword.book.BookException

/**
 * Single point of authority for what is being downloaded and its progress
 * Please note that you should never be modifying installedBooks,
 * only operate on installedBooksList
 */
//TODO: Install indexes for Bibles
//TODO: Figure out how to get Robolectric to mock the Log, rather than removing the calls
class BookManager(private val installedBooks: Books, val rM: RefreshManager) :
        WorkListener, BooksListener {

    /**
     * Cached copy of downloads in progress so views displaying this info can get it quickly.
     */
    // TODO: Combine to one map
    val bookMappings: MutableMap<String, Book> = hashMapOf()
    val inProgressDownloads: MutableMap<Book, DLProgressEvent> = hashMapOf()

    /**
     * A list of books that is locally maintained - installedBooks isn't always up-to-date
     */
    val installedBooksList: MutableList<Book> = installedBooks.getBooks() ?: linkedListOf()
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
    fun getJobId(b: Book) = "INSTALL_BOOK-${b.getInitials()}"

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
                .subscribe { it.toBlocking().first() install b }

        downloadEvents onNext DLProgressEvent(DLProgressEvent.PROGRESS_BEGINNING, b)
    }

    /**
     * For whatever reason, not just any old "book" reference will do. We need to actually
     * get a reference corresponding to a physically installed book for the driver to remove.
     * Plus, it makes the removeBook method easier to test.
     * @param b The book to find the actual driver for
     * @return The driver corresponding to the physical book
     */
    fun getRealBook(b: Book): Book = installedBooks getBook b.getInitials()

    /**
     * Remove a book from being installed.
     * Currently only supports books that have been installed outside the current application run.
     * Not quite sure why this is, but And-Bible exhibits the same behavior.
     * Also, I'll document it for the future: It seems like a book is only remove if you return
     * true from this method. Which is incredibly strange, because this method should have no
     * effect on the actual process of deleting a book. Even so, things work when
     * I return true here.
     * @param b The book to remove
     * @return Whether the book was removed.
     */
    fun removeBook(b: Book, realBook: Book = getRealBook(b)): Boolean {
        try {
            b.getDriver() delete realBook
            installedBooksList remove b
            return true
        } catch (e: BookException) {
//            Log.e("InstalledManager",
//                    "Unable to remove book (already uninstalled?): ${e.getDetailedMessage()}")
            return false
        }
    }
    /**
     * Check the status of a book download in progress.
     * @param b The book to get the current progress of
     * @return The most recent DownloadProgressEvent for the book, or null if not downloading
     */
    fun getDownloadProgress(b: Book) = inProgressDownloads[b]

    fun isInstalled(b: Book) = installedBooksList contains b

    // TODO: I have a strange feeling I can simplify this further...
    override fun workProgressed(ev: WorkEvent) {
        val job = ev.getJob()
        bookMappings.filter { it.getKey() == job.getJobID() }
                .map {
                    // We multiply by 100 first to avoid integer truncation
                    // Also avoids roundoff error. Neat trick, but I'm spending just as much time
                    // documenting it as implementing the floating point would take
                    val event = DLProgressEvent(job.getWorkDone() * 100 / job.getTotalWork(), it.getValue())
                    downloadEvents onNext event

                    if (job.getWorkDone() == job.getTotalWork()) {
                        inProgressDownloads remove bookMappings[job.getJobID()]
                        bookMappings remove job.getJobID()
                    } else
                        inProgressDownloads.put(it.getValue(), event)
                }
    }

    override fun workStateChanged(ev: WorkEvent) {
//        Log.d("BookDownloadManager", ev.toString())
    }

    override fun bookAdded(booksEvent: BooksEvent) {
        // It's possible the install finished before we received a progress event for it,
        // we handle that case here.
        val b = booksEvent.getBook()
//        Log.d("BookDownloadManager", "Book added: ${b.getName()}")
        inProgressDownloads remove b

        // Not sure why, but the inProgressDownloads might not have our book,
        // so we always trigger the PROGRESS_COMPLETE event.
        downloadEvents onNext DLProgressEvent(DLProgressEvent.PROGRESS_COMPLETE, b)

        // And update the locally available list
        installedBooksList add b
    }

    override fun bookRemoved(booksEvent: BooksEvent) {
        installedBooksList remove booksEvent.getBook()
    }
}
