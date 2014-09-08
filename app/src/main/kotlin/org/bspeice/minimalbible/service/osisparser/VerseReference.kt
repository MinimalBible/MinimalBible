package org.bspeice.minimalbible.service.osisparser

import org.crosswire.jsword.passage.Verse

/**
 * Created by bspeice on 9/7/14.
 */

class VerseReference(verse: Verse, index: Int) {
    public val verse: Verse = verse
    public val index: Int = index
}