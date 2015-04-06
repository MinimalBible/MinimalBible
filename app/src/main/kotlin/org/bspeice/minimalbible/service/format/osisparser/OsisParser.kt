package org.bspeice.minimalbible.service.format.osisparser

import android.text.SpannableStringBuilder
import org.bspeice.minimalbible.MinimalBible
import org.bspeice.minimalbible.R
import org.bspeice.minimalbible.service.format.osisparser.handler.*
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.BookData
import org.crosswire.jsword.book.OSISUtil
import org.crosswire.jsword.passage.Verse
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.util.ArrayDeque
import kotlin.properties.Delegates

/**
 * Parse out the OSIS XML into whatever we want!
 * This takes in a SpannableStringBuilder to modify. Normally I'm not a fan
 * of mutability, but due to the need for absolute efficiency in this class,
 * that's what we're going with.
 * TODO: Speed up parsing. This is the single most expensive repeated operation
 */
class OsisParser() : DefaultHandler() {

    // Don't pass a verse as part of the constructor, but still guarantee
    // that it will exist
    var verseContent: VerseContent by Delegates.notNull()
    var builder: SpannableStringBuilder by Delegates.notNull()

    // TODO: Implement a stack to keep min API 8
    val handlerStack = ArrayDeque<TagHandler>()

    fun appendVerse(b: Book, v: Verse,
                    builder: SpannableStringBuilder): VerseContent {
        verseContent = VerseContent(v)
        this.builder = builder
        BookData(b, v).getSAXEventProvider() provideSAXEvents this
        return verseContent
    }

    /**
     * Parse a verse and return its content
     * Only good for parsing a single verse at a time,
     * but gives a cleaner API to work with (and means that
     * we can just use the default constructor)
     */
    fun parseVerse(b: Book, v: Verse): SpannableStringBuilder {
        val mBuilder = SpannableStringBuilder()
        appendVerse(b, v, mBuilder)
        return mBuilder
    }

    override fun startElement(uri: String, localName: String,
                              qName: String, attributes: Attributes) {
        when (localName) {
            OSISUtil.OSIS_ELEMENT_VERSE -> handlerStack push VerseHandler()
            "divineName" -> handlerStack push DivineHandler()
            "q" -> handlerStack push QHandler(MinimalBible.getAppContext()
                    .getResources().getColor(R.color.divineSpeech))
            else -> handlerStack push UnknownHandler(localName)
        }

        handlerStack.peek().start(attributes, verseContent, builder)
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        val tagHandler = handlerStack.pop()
        tagHandler.end(verseContent, builder)
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        handlerStack.peek().render(builder, verseContent, String(ch))
    }
}