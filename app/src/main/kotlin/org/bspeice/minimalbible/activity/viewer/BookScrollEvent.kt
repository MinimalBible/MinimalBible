package org.bspeice.minimalbible.activity.viewer

import org.crosswire.jsword.versification.BibleBook

/**
 * Created by bspeice on 11/26/14.
 */
data class BookScrollEvent(val b: BibleBook, val chapter: Int) {}
