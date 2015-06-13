package org.bspeice.minimalbible

import org.junit.Before
import org.junit.Test
import kotlin.properties.Delegates

class SafeValDelegateKotlinTest {

    var delegate: SafeValDelegate<String> by Delegates.notNull()

    Before fun setUp() {
        delegate = SafeValDelegate()
    }

    Test(expected = IllegalStateException::class)
    fun testDelegateNullSafety() {
        delegate.get(null, PropertyMetadataImpl(""))
    }

    Test(expected = IllegalStateException::class)
    fun testDelegateAssignOnce() {
        delegate.set(null, PropertyMetadataImpl(""), "")
        delegate.set(null, PropertyMetadataImpl(""), "")
    }
}