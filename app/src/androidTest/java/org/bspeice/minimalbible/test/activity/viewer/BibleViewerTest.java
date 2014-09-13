package org.bspeice.minimalbible.test.activity.viewer;

import android.test.ActivityInstrumentationTestCase2;

import org.bspeice.minimalbible.Modules;
import org.bspeice.minimalbible.activity.viewer.BibleViewer;
import org.bspeice.minimalbible.service.manager.BookManager;
import org.crosswire.jsword.book.Book;

import java.util.ArrayList;

import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BibleViewerTest extends ActivityInstrumentationTestCase2<BibleViewer> {

    public BibleViewerTest() {
        super(BibleViewer.class);
    }

    public void setUp() {
        // For some reason this test requires us to set the dexcache...
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext()
                .getCacheDir().toString());
    }

    /**
     * It may happen to be the case that we start and there are no installed books.
     * This likely triggers a runtime exception from Rx, and is no excuse for dying.
     */
    public void testInitializationNoInstalledBooks() {
        BookManager mockBookManager = mock(BookManager.class);
        when(mockBookManager.getInstalledBooks()).thenReturn(
                Observable.from(new ArrayList<Book>()));
        Modules.testModules.setBookManager(mockBookManager);

        assertNotNull(getActivity());
    }

}