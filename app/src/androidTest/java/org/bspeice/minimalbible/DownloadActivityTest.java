package org.bspeice.minimalbible;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.util.Log;

/**
 * Created by Bradlee Speice on 7/5/2014.
 */
/* Please note that it is necessary to extend the ActivityInstrumentationTestCase2 if you plan
    on using an activity that sets up any underlying fragments (includes navigation drawers).
    The ActivityUnitTestCase doesn't set up enough of the Activity lifecycle.
 */
public class DownloadActivityTest extends ActivityInstrumentationTestCase2<DownloadActivity> {

    public DownloadActivityTest() {
        super(DownloadActivity.class);
    }

    public void testAndroidTestInjection() {
        DownloadActivity a = getActivity();
        assertNotNull(a);

        Log.w("DownloadActivityTest", a.actionTitle);
        assertEquals(a.actionTitle, a.actionTitle, "Test");
    }
}
