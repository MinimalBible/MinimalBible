package org.bspeice.minimalbible.test.activity.downloader;

import org.bspeice.minimalbible.activity.downloader.BookListFragment;
import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
import org.crosswire.jsword.book.BookCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * Created by bspeice on 11/17/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest = "../app/src/main/AndroidManifest.xml")
public class BookListFragmentTest {

    @Before
    public void setUp() {
        DownloadActivity activity = Robolectric.buildActivity(DownloadActivity.class)
                .create().start().visible().get();
        BookListFragment fragment = BookListFragment.newInstance(BookCategory.BIBLE);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(fragment, null)
                .commit();
    }

    @Test
    public void testItsWorking() {
        assertTrue(true);
    }
}
