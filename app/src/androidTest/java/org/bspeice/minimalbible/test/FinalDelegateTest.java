package org.bspeice.minimalbible.test;

import org.bspeice.minimalbible.FinalDelegate;
import org.bspeice.minimalbible.MBTestCase;

import kotlin.PropertyMetadataImpl;

/**
 * Test that the FinalDelegate actually obeys its contract
 */
public class FinalDelegateTest extends MBTestCase {

    FinalDelegate<String> delegate;

    public void setUp() {
        delegate = new FinalDelegate<String>();
    }

    public void testDelegateNullSafety() {
        try {
            delegate.get(null, new PropertyMetadataImpl(""));
        } catch (IllegalStateException e) {
            return;
        }

        fail("Exception not thrown!");
    }

    public void testDelegateAssignOnce() {
        try {
            delegate.set(null, new PropertyMetadataImpl(""), "");
            delegate.set(null, new PropertyMetadataImpl(""), "");
        } catch (IllegalStateException e) {
            return;
        }

        fail("Allowed to set twice!");
    }
}
