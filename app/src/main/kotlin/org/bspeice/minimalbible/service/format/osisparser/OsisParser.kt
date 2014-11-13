package org.bspeice.minimalbible.service.format.osisparser

import org.xml.sax.helpers.DefaultHandler
import org.crosswire.jsword.passage.Verse
import java.util.ArrayDeque
import org.xml.sax.Attributes
import org.crosswire.jsword.book.OSISUtil
import org.bspeice.minimalbible.SafeValDelegate
import kotlin.properties.Delegates

/**
 * Created by bspeice on 9/10/14.
 */

class OsisParser() : DefaultHandler() {

    // Don't pass a verse as part of the constructor, but still guarantee
    // that it will exist
    public var verse: Verse by SafeValDelegate()
    val verseContent: VerseContent by Delegates.lazy { VerseContent(verse) }

    // TODO: Implement a stack to keep min API 8
    val doWrite = ArrayDeque<Boolean>()

    fun getJson() = verseContent.toJson()

    override fun startElement(uri: String, localName: String,
                              qName: String, attributes: Attributes) {
        when (localName) {
            OSISUtil.OSIS_ELEMENT_VERSE -> doWrite.push(true)
            else -> doWrite.push(false)
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        doWrite.pop()
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (doWrite.peek())
            verseContent.appendContent(String(ch))
    }
}