/**
 * Created by bspeice on 9/9/14.
 */
package org.bspeice.minimalbible.service.format.osisparser

import org.crosswire.jsword.passage.Verse
import java.util.ArrayList

// TODO: Refactor to a VerseInfo class, not the actual content to support "streaming" parsing
data class VerseContent(val v: Verse,
                        val id: Int = v.getOrdinal(),
                        val bookName: String = v.getName(),
                        val chapter: Int = v.getChapter(),
                        val verseNum: Int = v.getVerse(),
                        val chapterTitle: String = "",
                        val paraTitle: String = "",
                        val references: MutableList<VerseReference> = ArrayList()) {}