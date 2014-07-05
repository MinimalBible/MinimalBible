package org.bspeice.minimalbible;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.util.Log;

/**
 * Created by Bradlee Speice on 7/5/2014.
 */
public class DownloadActivityTest extends ActivityUnitTestCase<DownloadActivity> {

    public DownloadActivityTest() {
        super(DownloadActivity.class);
    }

    public void testAndroidTestInjection() {
        MinimalBible m = new MinimalBible();
        m.buildObjGraph();
        setApplication(m);

        startActivity(new Intent(getInstrumentation().getTargetContext(),
                DownloadActivity.class), null, null);

        DownloadActivity a = getActivity();
        assertNotNull(a);

        Log.w("DownloadActivityTest", a.actionTitle);
        assertEquals(a.actionTitle, a.actionTitle, "Test");
    }
}
