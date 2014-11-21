/**
 * Created by bspeice on 11/20/14.
 */

import org.junit.Test
import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent
import org.crosswire.jsword.book.Book
import org.mockito.Mockito
import org.junit.Assert

class DLProgressEventTest {

    val b = Mockito.mock(javaClass<Book>())

    Test fun fiftyPercentIsOneEighty() {

        val e = DLProgressEvent(50, b)
        Assert.assertEquals(180, e.toCircular())
    }
}
