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
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.fail

/**
 * Created by bspeice on 2/16/15.
 */
data class Mocks() {
    val mockBook = Mockito.mock(javaClass<Book>())
    val mockIndex = Mockito.mock(javaClass<IndexManager>())
    val indexManager = MBIndexManager(mockIndex)
}

class MBIndexManagerSpek() : Spek() {{

    given("a mock IndexManager, Book, and real MBIndexManager") {
        val mocks = Mocks()
        val mockBook = mocks.mockBook
        val indexManager = mocks.indexManager

        val firstStatus = IndexStatus.UNDONE
        val secondStatus = IndexStatus.DONE

        on("setting up the book and attempting to create the index") {
            val firstNext = AtomicReference<IndexStatus>()
            val secondNext = AtomicReference<IndexStatus>()
            val completedReference = AtomicBoolean(false)

            Mockito.`when`(mockBook.getIndexStatus())
                    // MBIndexManager checks status
                    .thenReturn(firstStatus)
                    // First actual status
                    .thenReturn(firstStatus)
                    // Second actual status
                    .thenReturn(secondStatus)

            val subject = indexManager.buildIndex(mockBook)

            subject.subscribe({
                if (firstNext.get() == null)
                    firstNext.set(it)
                else
                    secondNext.set(it)
            },
                    {},
                    { completedReference.set(true) })

            // Wait until completed
            Awaitility.waitAtMost(1, TimeUnit.SECONDS).untilTrue(completedReference);

            it("should fire the correct first status") {
                assertEquals(firstStatus, firstNext.get())
            }

            it("should fire the correct second status") {
                assertEquals(secondStatus, secondNext.get())
            }

            it("should fire the onCompleted event") {
                assertTrue(completedReference.get())
            }
        }
    }

    given("a mock IndexManager, Book, and real MBIndexManager") {
        val mocks = Mocks()
        val indexManager = mocks.mockIndex
        val book = mocks.mockBook
        val mbIndex = mocks.indexManager

        on("trying to remove a book's index") {
            mbIndex.removeIndex(book)

            it("should call the IndexManager.deleteIndex() function") {
                Mockito.verify(indexManager, Mockito.times(1)) deleteIndex book
            }
        }
    }

    given("a Book that is indexed and real MBIndexManager") {
        val mocks = Mocks()
        val book = mocks.mockBook
        val indexManager = mocks.indexManager

        Mockito.`when`(book.getIndexStatus())
                .thenReturn(IndexStatus.DONE)

        on("trying to determine whether we should index") {
            it("should not try to index") {
                assertFalse(indexManager shouldIndex book)
            }
        }

        on("trying to determine if an index is ready") {
            it("should let us know that everything is ready") {
                assertTrue(indexManager indexReady book)
            }
        }

        on("attempting to index anyway") {
            it("should throw an error") {
                try {
                    indexManager buildIndex book
                    fail()
                } catch (e: IllegalStateException) {
                    // Intentionally empty body
                }
            }
        }
    }

    given("a Book with an indexing error") {
        val mocks = Mocks()
        val book = mocks.mockBook
        val mockIndex = mocks.mockIndex
        val indexManager = mocks.indexManager

        Mockito.`when`(book.getIndexStatus())
                .thenReturn(IndexStatus.INVALID)

        on("trying to determine whether we should index") {
            it("should try to index again and over-write the original") {
                assertTrue(indexManager shouldIndex book)
            }
        }

        on("trying to determine if an index is ready") {
            it("should inform us that the index is most certainly not ready") {
                assertFalse(indexManager indexReady book)
            }
        }

        on("attempting to index") {
            indexManager buildIndex book

            it("should run the index") {
                Mockito.verify(mockIndex, Mockito.times(1))
                        .scheduleIndexCreation(book);
            }
        }
    }

    given("a Book in process of being indexed") {
        val mocks = Mocks()
        val book = mocks.mockBook
        val indexManager = mocks.indexManager

        Mockito.`when`(book.getIndexStatus())
                .thenReturn(IndexStatus.CREATING)

        on("trying to determine whether we should index") {
            it("should not create a second indexing thread") {
                assertFalse(indexManager shouldIndex book)
            }
        }

        on("trying to determine if the index is ready") {
            it("should let us know that the index is still in progress") {
                assertFalse(indexManager shouldIndex book)
            }
        }

        on("attempting to index anyway") {
            it("should throw an error to let us know it will not index") {
                try {
                    indexManager buildIndex book
                    fail()
                } catch (e: IllegalStateException) {
                    // Intentionally empty body
                }
            }
        }
    }
}
}
