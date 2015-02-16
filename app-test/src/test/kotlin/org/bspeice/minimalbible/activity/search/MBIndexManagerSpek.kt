package org.bspeice.minimalbible.activity.search

import org.jetbrains.spek.api.Spek
import org.mockito.Mockito
import org.crosswire.jsword.index.IndexManager
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.index.IndexStatus
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals
import com.jayway.awaitility.Awaitility
import java.util.concurrent.TimeUnit

/**
 * Created by bspeice on 2/16/15.
 */
class MBIndexManagerSpek() : Spek() {{

    given("a mock IndexManager, Book, and real MBIndexManager") {
        val returnDelay: Long = 1000
        val mockIndex = Mockito.mock(javaClass<IndexManager>())
        val mockBook = Mockito.mock(javaClass<Book>())
        val indexManager = MBIndexManager(mockIndex)

        val firstStatus = IndexStatus.UNDONE
        val secondStatus = IndexStatus.DONE

        // We sleep the first response to give us time to actually subscribe
        Mockito.`when`(mockBook.getIndexStatus())
                .thenAnswer { Thread.sleep(returnDelay); firstStatus }
                .thenReturn(secondStatus)

        on("attempting to create the index") {
            val firstNext = AtomicReference<IndexStatus>()
            val secondNext = AtomicReference<IndexStatus>()
            val completedReference = AtomicBoolean(false)

            val subject = indexManager.buildIndex(mockBook)

            subject.subscribe({
                if (firstNext.get() == null)
                    firstNext.set(it)
                else
                    secondNext.set(it)
            },
                    {},
                    { completedReference.set(true) })

            it("should fire an onComplete so we can continue further validation") {
                Awaitility.waitAtMost(returnDelay * 2, TimeUnit.MILLISECONDS)
                        .untilTrue(completedReference)
            }

            it("should fire the correct first status") {
                assertEquals(firstStatus, firstNext.get())
            }

            it("should fire the correct second status") {
                assertEquals(secondStatus, secondNext.get())
            }
        }
    }

    given("a mock IndexManager, Book, and real MBIndexManager") {
        val indexManager = Mockito.mock(javaClass<IndexManager>())
        val book = Mockito.mock(javaClass<Book>())
        val mbIndex = MBIndexManager(indexManager)

        on("trying to remove a book's index") {
            mbIndex.removeIndex(book)

            it("should call the IndexManager.deleteIndex() function") {
                Mockito.verify(indexManager, Mockito.times(1)) deleteIndex book
            }
        }
    }
}
}
