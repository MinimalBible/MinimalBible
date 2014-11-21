/**
 * Created by bspeice on 11/20/14.
 */

import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent
import org.crosswire.jsword.book.Book
import org.mockito.Mockito.mock
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class DLProgressEventSpecs : Spek() {{

    given("a DLProgressEvent created with 50% progress and a mock book") {
        val mockBook = mock(javaClass<Book>())
        val dlEvent = DLProgressEvent(50, mockBook)

        on("getting the progress in degrees") {
            val progressDegrees = dlEvent.toCircular()

            it("should be 180 degrees") {
                assertEquals(180, progressDegrees)
            }
        }
    }
}
}
