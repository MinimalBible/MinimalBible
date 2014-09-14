/**
 * Created by bspeice on 9/9/14.
 */
package org.bspeice.minimalbible.service.format.osisparser

import com.google.gson.Gson
import org.crosswire.jsword.passage.Verse
import java.util.ArrayList

//TODO: JSON Streaming parsing? http://instagram-engineering.tumblr.com/post/97147584853/json-parsing
class VerseContent(v: Verse?) {
    var id = v?.getOrdinal() ?: 0
    var bookName = v?.getName() ?: ""
    var chapter = v?.getChapter() ?: 0
    var verseNum = v?.getVerse() ?: 0
    var content = ""
    var chapterTitle = ""
    var paraTitle = ""
    var references: MutableList<VerseReference> = ArrayList()

    public fun toJson(): String {
        // Lazy load Gson - not likely that we'll call this method multiple times, so
        // don't have to worry about a penalty there.
        return Gson().toJson(this) as String
    }

    public fun appendContent(content: String) {
        this.content += content
    }
}