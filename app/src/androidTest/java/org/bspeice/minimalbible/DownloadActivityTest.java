package org.bspeice.minimalbible;

import android.test.ActivityInstrumentationTestCase2;

import org.bspeice.minimalbible.activity.downloader.DownloadActivity;

import java.lang.reflect.Field;

/**
 * Created by Bradlee Speice on 7/5/2014.
 */
/* Please note that it is necessary to extend the ActivityInstrumentationTestCase2 if you plan
    on using an activity that sets up any underlying fragments (includes navigation drawers).
    The ActivityUnitTestCase doesn't set up enough of the Activity lifecycle.
 */
public class DownloadActivityTest extends
        ActivityInstrumentationTestCase2<DownloadActivity> {

    public DownloadActivityTest() {
        super(DownloadActivity.class);
    }

    public void testAndroidTestInjection() {
        DownloadActivity a = getActivity();
        assertNotNull(a);

        Class c = a.getClass();
        try {
            // getField() is public-only
            Field fTitle = c.getDeclaredField("testInject");
            fTitle.setAccessible(true);
            CharSequence title = (CharSequence)fTitle.get(a);
            assertEquals(TestModules.testActivityTitle, title);
        } catch (NoSuchFieldException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
}
