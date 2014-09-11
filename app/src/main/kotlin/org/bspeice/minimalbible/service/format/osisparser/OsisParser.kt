package org.bspeice.minimalbible.service.format.osisparser

import org.xml.sax.helpers.DefaultHandler
import org.crosswire.jsword.passage.Verse
import java.util.ArrayDeque
import org.xml.sax.Attributes
import org.crosswire.jsword.book.OSISUtil

/**
 * Created by bspeice on 9/10/14.
 */

class OsisParser(v: Verse) : DefaultHandler() {

    val verseContent = VerseContent(v)
    val doWrite = ArrayDeque<Boolean>()

    // Android Studio complains about compilation, but the method
    // has @NotNull annotations, so we program for those.
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {

        if (localName.equals(OSISUtil.OSIS_ELEMENT_VERSE))
            doWrite.push(true)
        else
            doWrite.push(false)
    }
    // Android Studio complains about compilation, but the method
    // has @NotNull annotations, so we program for those.
    override fun endElement(uri: String?, localName: String, qName: String) {
        doWrite.pop()
    }
    override fun characters(ch: CharArray?, start: Int, length: Int) {
        if (doWrite.peek() as Boolean)
            verseContent.appendContent(String(ch as CharArray))
    }
}