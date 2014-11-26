package org.bspeice.minimalbible.service.format.osisparser

import org.xml.sax.helpers.DefaultHandler
import org.crosswire.jsword.passage.Verse
import java.util.ArrayDeque
import org.xml.sax.Attributes
import org.crosswire.jsword.book.OSISUtil
import org.bspeice.minimalbible.SafeValDelegate
import org.crosswire.jsword.book.BookData
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.getVersification
import kotlin.properties.Delegates

/**
 * Created by bspeice on 9/10/14.
 */

class OsisParser() : DefaultHandler() {

    // Don't pass a verse as part of the constructor, but still guarantee
    // that it will exist
    var verseContent: VerseContent by Delegates.notNull()

    // TODO: Implement a stack to keep min API 8
    val doWrite = ArrayDeque<Boolean>()

    fun getJson(b: Book, v: Verse): String {
        // I don't always set up my constructors to have faces, but when I do...
        verseContent = VerseContent(v = v)
        BookData(b, v).getSAXEventProvider() provideSAXEvents this
        return verseContent.json
    }

    fun getVerse(b: Book, v: Int): VerseContent {
        val verse = b.getVersification().decodeOrdinal(v)
        verseContent = VerseContent(verse)
        BookData(b, verse).getSAXEventProvider() provideSAXEvents this
        return verseContent
    }

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
            verseContent = verseContent appendContent String(ch)
    }
}