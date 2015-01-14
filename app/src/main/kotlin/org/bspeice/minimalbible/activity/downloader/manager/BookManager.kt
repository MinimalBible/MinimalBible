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
import org.crosswire.jsword.util.IndexDownloader
import org.crosswire.common.progress.Progress

/**
 * Single point of authority for what is being downloaded and its progress
 * Please note that you should never be modifying installedBooks,
 * only operate on installedBooksList
 */
class BookManager(private val installedBooks: Books,
                  val rM: RefreshManager,
                  val downloadEvents: PublishSubject<DLProgressEvent>) :
        WorkListener, BooksListener {

    private val bookJobNamePrefix = Progress.INSTALL_BOOK.substringBeforeLast("%s")
    private val indexJobNamePrefix = Progress.DOWNLOAD_SEARCH_INDEX.substringBeforeLast("%s")

    /**
     * List of jobs currently active by their job name
     */
    val inProgressJobNames: MutableMap<String, Book> = hashMapOf()

    /**
     * Cached copy of downloads in progress so views displaying this info can get it quickly.
     */
    val inProgressDownloads: MutableMap<Book, DLProgressEvent> = hashMapOf()

    /**
     * A list of books that is locally maintained - installedBooks isn't always up-to-date
     */
    val installedBooksList: MutableList<Book> = installedBooks.getBooks() ?: linkedListOf();

    {
        JobManager.addWorkListener(this)
        installedBooks.addBooksListener(this)
        downloadEvents.subscribe { this.inProgressDownloads[it.b] = it }
    }

    /**
     * Build what the installer creates the job name as.
     * This technically could be static, but Kotlin won't have none of that.
     *
     * @param b The book to predict the download job name of
     * @return The name of the job that will/is download/ing this book
     */
    fun getJobNames(b: Book) = listOf("${bookJobNamePrefix}${b.getInitials()}",
            "${indexJobNamePrefix}${b.getInitials()}")

    fun downloadBook(b: Book) {
        // First, look up where the Book came from
        val installerObs = Observable.just(rM installerFromBook b)

        // And subscribe on two different threads for the download
        // Not sure why we need two threads, guessing it's because the
        // thread is closed when the install event is done
        installerObs
                .observeOn(Schedulers.newThread())
                .subscribe {
                    // Download the actual book
                    it subscribe { it install b }
                }

        installerObs
                .observeOn(Schedulers.newThread())
                .subscribe {
                    // Download the book index
                    it subscribe { IndexDownloader.downloadIndex(b, it) }
                }

        // Then notify everyone that we're starting
        downloadEvents onNext DLProgressEvent.beginningEvent(b)

        // Finally register the jobs in progress
        getJobNames(b).forEach { this.inProgressJobNames[it] = b }
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

        val book = inProgressJobNames[job.getJobID()] as Book
        val oldEvent = inProgressDownloads[book] ?: DLProgressEvent.beginningEvent(book)

        var newEvent: DLProgressEvent
        if (job.getJobID().contains(bookJobNamePrefix))
            newEvent = oldEvent.copy(bookProgress = job.getWork())
        else
            newEvent = oldEvent.copy(indexProgress = job.getWork())

        downloadEvents onNext newEvent

        if (newEvent.averageProgress == DLProgressEvent.PROGRESS_COMPLETE) {
            inProgressDownloads remove inProgressJobNames[job.getJobID()]
            inProgressJobNames remove job.getJobID()
        } else
            inProgressDownloads.put(book, newEvent)

    }

    override fun workStateChanged(ev: WorkEvent) {
//        Log.d("BookDownloadManager", ev.toString())
    }

    override fun bookAdded(booksEvent: BooksEvent) {
        // Update the local list of available books
        installedBooksList add booksEvent.getBook()
    }

    override fun bookRemoved(booksEvent: BooksEvent) {
        installedBooksList remove booksEvent.getBook()
    }
}
