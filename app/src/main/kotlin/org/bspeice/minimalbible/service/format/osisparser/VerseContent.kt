/**
 * Created by bspeice on 9/9/14.
 */
package org.bspeice.minimalbible.service.format.osisparser

import com.google.gson.Gson
import org.crosswire.jsword.passage.Verse
import java.util.ArrayList

//TODO: JSON Streaming parsing? http://instagram-engineering.tumblr.com/post/97147584853/json-parsing
class VerseContent(v: Verse) {
    val id = v.getOrdinal()
    val bookName = v.getName()
    val chapter = v.getChapter()
    val verseNum = v.getVerse()
    val chapterTitle = ""
    val paraTitle = ""
    val references: MutableList<VerseReference> = ArrayList()
    var content = ""

    public val json: String
        get() = Gson().toJson(this)

    public fun toJson(): String {
        // Lazy load Gson - not likely that we'll call this method multiple times, so
        // don't have to worry about a penalty there.
        return Gson().toJson(this)
    }

    public fun appendContent(content: String) {
        this.content += content
    }
}