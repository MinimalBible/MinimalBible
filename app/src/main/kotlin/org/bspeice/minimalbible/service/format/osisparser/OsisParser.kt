package org.bspeice.minimalbible.service.format.osisparser

import org.xml.sax.helpers.DefaultHandler
import org.crosswire.jsword.passage.Verse
import java.util.ArrayDeque
import org.xml.sax.Attributes
import org.crosswire.jsword.book.OSISUtil
import org.crosswire.jsword.book.BookData
import org.crosswire.jsword.book.Book
import kotlin.properties.Delegates
import org.bspeice.minimalbible.service.format.osisparser.handler.TagHandler
import org.bspeice.minimalbible.service.format.osisparser.handler.VerseHandler
import org.bspeice.minimalbible.service.format.osisparser.handler.UnknownHandler
import android.text.SpannableStringBuilder
import org.bspeice.minimalbible.service.format.osisparser.handler.DivineHandler

/**
 * Parse out the OSIS XML into whatever we want!
 * TODO: Speed up parsing. This is the single most expensive repeated operation
 */
class OsisParser(val builder: SpannableStringBuilder) : DefaultHandler() {

    // Don't pass a verse as part of the constructor, but still guarantee
    // that it will exist
    var verseContent: VerseContent by Delegates.notNull()

    // TODO: Implement a stack to keep min API 8
    val handlerStack = ArrayDeque<TagHandler>()

    fun appendVerse(b: Book, v: Verse): VerseContent {
        verseContent = VerseContent(v)
        BookData(b, v).getSAXEventProvider() provideSAXEvents this
        return verseContent
    }

    override fun startElement(uri: String, localName: String,
                              qName: String, attributes: Attributes) {
        when (localName) {
            OSISUtil.OSIS_ELEMENT_VERSE -> handlerStack push VerseHandler()
            "divineName" -> handlerStack push DivineHandler()
            else -> handlerStack push UnknownHandler(localName)
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        handlerStack.pop()
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        handlerStack.peek().render(builder, verseContent, String(ch))
    }
}