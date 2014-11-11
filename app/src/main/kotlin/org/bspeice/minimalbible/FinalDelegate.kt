package org.bspeice.minimalbible

/**
 * The purpose of this delegate is to guarantee null-safety, while
 * also ensuring a pseudo-val type. If you try to read before use, error.
 * If you try to set multiple times, error.
 */

public class FinalDelegate<T : Any>() {
    private var value: T? = null
    private var didAssign: Boolean = false

    public fun get(thisRef: Any?, desc: PropertyMetadata): T {
        return value ?: throw IllegalStateException("Property ${desc.name} should be initialized before get")
    }

    public fun set(thisRef: Any?, desc: PropertyMetadata, value: T) {
        if (!didAssign) {
            this.value = value
            this.didAssign = true
        } else
            throw IllegalStateException("Property ${desc.name} should not be assigned multiple times")
    }
}
