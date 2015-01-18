package org.bspeice.minimalbible

/**
 * Massive credit over here:
 * http://blog.omalley.id.au/2013/07/27/null-handling-in-kotlin.html
 *
 * The trick is that a non-nullable upper bound is placed on an optional
 * nullable object - effectively, you get the real object or throw an exception.
 */
public fun <T : Any> T?.orError(message: String): T {
    return if (this == null) throw IllegalArgumentException(message) else this
}