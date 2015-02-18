package org.bspeice.minimalbible.activity.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

/**
 * Right now this is the only Robolectric test. Some clarifications on this should be made:
 * There's an ongoing issue with the ActionBarDrawerToggle, meaning that the BasicSearch
 * activity is the only one eligible for testing
 * (https://github.com/robolectric/robolectric/issues/1424)
 * <p/>
 * Additionally, Robolectric only supports up to Jellybean, which is why the emulateSdk.
 * Finally, in Gradle, tests run relative to app-test, whereas the IDE may try and run
 * things in the project root. Be careful when changing the manifest location.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest = "../app/src/main/AndroidManifest.xml")
public class BasicSearchTest {

    @Test
    public void testBuildActivity() {
        BasicSearch activity = Robolectric.buildActivity(BasicSearch.class)
                .create().get();
        assertNotNull(activity);
    }
}
