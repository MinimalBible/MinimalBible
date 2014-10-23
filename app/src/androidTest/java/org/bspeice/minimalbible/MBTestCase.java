package org.bspeice.minimalbible;

import android.test.AndroidTestCase;

/**
 * A TestCase specifically for running Android JUnit tests.
 * This is not intended to replace InstrumentationTest, etc., just
 * replace the bare JUnit tests.
 */
public class MBTestCase extends AndroidTestCase {

    public void setUp() {
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
    }
}
