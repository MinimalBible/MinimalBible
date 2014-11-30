package org.bspeice.minimalbible.service.format.osisparser

import org.xml.sax.helpers.DefaultHandler
import org.crosswire.jsword.passage.Verse
import java.util.ArrayDeque
import org.xml.sax.Attributes
import org.crosswire.jsword.book.OSISUtil
import org.crosswire.jsword.book.BookData
import org.crosswire.jsword.book.Book
import kotlin.properties.Delegates

/**
 * Parse out the OSIS XML into whatever we want!
 * TODO: Speed up parsing. This is the single most expensive repeated operation
 */
class OsisParser() : DefaultHandler() {

    // Don't pass a verse as part of the constructor, but still guarantee
    // that it will exist
    var verseContent: VerseContent by Delegates.notNull()

    // TODO: Implement a stack to keep min API 8
    val doWrite = ArrayDeque<Boolean>()

    fun getVerse(b: Book, v: Verse): VerseContent {
        verseContent = VerseContent(v)
        BookData(b, v).getSAXEventProvider() provideSAXEvents this
        return verseContent
    }

    override fun startElement(uri: String, localName: String,
                              qName: String, attributes: Attributes) {
        when (localName) {
            OSISUtil.OSIS_ELEMENT_VERSE -> doWrite.push(true)
            "divineName" -> doWrite.push(true)
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