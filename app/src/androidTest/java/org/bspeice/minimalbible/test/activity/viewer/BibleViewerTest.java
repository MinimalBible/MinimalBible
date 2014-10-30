package org.bspeice.minimalbible.test.activity.viewer;

import android.test.ActivityInstrumentationTestCase2;

import org.bspeice.minimalbible.activity.viewer.BibleViewer;

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
     *
     * Test disabled until I can get some refactoring done
     */
    /*
    public void testInitializationNoInstalledBooks() {
        BookManager mockBookManager = mock(BookManager.class);
        when(mockBookManager.getInstalledBooks()).thenReturn(
                Observable.from(new ArrayList<Book>()));
        Modules.testModules.setBookManager(mockBookManager);

        assertNotNull(getActivity());
    }
    */
}