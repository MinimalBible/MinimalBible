package org.bspeice.minimalbible.activity.downloader.manager

import org.bspeice.minimalbible.activity.search.MBIndexManager
import org.crosswire.common.progress.JobManager
import org.crosswire.common.progress.Progress
import org.crosswire.common.progress.WorkEvent
import org.crosswire.common.progress.WorkListener
import org.crosswire.jsword.book.*
import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

/**
 * Single point of authority for what is being downloaded and its progress
 * Please note that you should never be modifying installedBooks,
 * only operate on installedBooksList
 */
class BookManager(private val installedBooks: Books,
                  val rM: RefreshManager,
                  val downloadEvents: PublishSubject<DLProgressEvent>,
                  val indexManager: MBIndexManager) :
        WorkListener, BooksListener {

    private val bookJobNamePrefix = Progress.INSTALL_BOOK.substringBeforeLast("%s")

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
    val installedBooksList: MutableList<Book> = installedBooks.getBooks() ?: linkedListOf()

    init {
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
    fun getJobName(b: Book) = "${bookJobNamePrefix}${b.getInitials()}"

    fun downloadBook(b: Book) {
        // First, look up where the Book came from
        val installerObs = Observable.just(rM installerFromBook b)

        // And subscribe on two different threads for the download
        // Not sure why we need two threads, guessing it's because the
        // thread is closed when the install event is done
        installerObs
                .observeOn(Schedulers.newThread())
                // Download the actual book
                .subscribe { it subscribe { it install b } }

        // Then notify everyone that we're starting
        downloadEvents onNext DLProgressEvent.beginningEvent(b)

        // Finally register the jobs in progress
        inProgressJobNames[getJobName(b)] = b
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
            // Order matters for the test suite as this line will trigger NPE during testing
            // In production, doesn't make a difference, so leave this below the
            // installedBooksList remove
            indexManager removeIndex realBook
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

    /**
     * This method gets called as progress continues on downloading a book.
     * To be honest, I don't know that there's any contract about what thread
     * this is called on.
     * By any means, if the job hasn't been registered as in progress,
     * don't emit an event - we don't know what book we're operating on.
     */
    override fun workProgressed(ev: WorkEvent) {
        val job = ev.getJob()
        val book = inProgressJobNames[job.getJobID()]

        if (book == null)
            return

        val oldEvent = inProgressDownloads[book] ?: DLProgressEvent.beginningEvent(book)
        val newEvent = oldEvent.copy(progress = job.getWork())

        downloadEvents onNext newEvent

        if (newEvent.progress == DLProgressEvent.PROGRESS_COMPLETE) {
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
