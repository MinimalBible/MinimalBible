package org.bspeice.minimalbible;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;

import org.bspeice.minimalbible.test.activity.FragmentTestActivity;

/**
 * Created by bspeice on 7/9/14.
 */
public class FragmentTestActivityTest extends ActivityInstrumentationTestCase2<FragmentTestActivity> {

    public FragmentTestActivityTest() {
        super(FragmentTestActivity.class);
    }

    public void testCanStartFragmentTestActivity() {
        assertNotNull(getActivity());
    }

    /**
     * Test that a Fragment is created properly in our TestCase for testing.
     */
    public static class ValidFragmentTest extends Fragment {
        public final static String FIELD_SHOULD_BE = "FIELD SHOULD HAVE THIS VALUE";
        public String actualField;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.actualField = FIELD_SHOULD_BE;
        }
    }
    public void testValidFragment() {
        ValidFragmentTest f = new ValidFragmentTest();
        getActivity().startFragment(f);
        assertEquals(ValidFragmentTest.FIELD_SHOULD_BE, f.actualField);
    }
}
