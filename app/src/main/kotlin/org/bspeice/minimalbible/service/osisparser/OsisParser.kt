package org.bspeice.minimalbible.service.osisparser

import org.crosswire.jsword.book.OSISUtil
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.util.ArrayDeque
import org.crosswire.jsword.passage.Verse

/**
 * Parse the OSIS SAX to an object we can actually use.
 * Technically we don't need the verse reference currently, but it will make persisting
 * everything easier later.
 */

class OsisParser(verse: Verse) : DefaultHandler() {

    var content: VerseContent = VerseContent()
    val doWrite: ArrayDeque<Boolean> = ArrayDeque()
    val verse: Verse = verse

    // Android Studio complains, but the override below compiles since the java
    // has a @NotNull contract
    override fun startElement(uri: String, localName: String,
                              qName: String, attributes: Attributes) {

        val name = getName(localName, qName)

        if (name.equals(OSISUtil.OSIS_ELEMENT_VERSE)) {
            doWrite.push(true)
            content.id = getId(attributes)
        } else {
            doWrite.push(false)
        }
    }

    // Android Studio complains, but the override below compiles since the java
    // has a @NotNull contract
    override fun endElement(uri: String?, localName: String, qName: String) {
        doWrite.pop()
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        if (ch != null && doWrite.peek() as Boolean)
            content.appendContent(String(ch))
    }

    fun getName(eName: String?, qName: String): String {
        if (eName != null && eName.length > 0)
            return eName
        else
            return qName
    }

    fun getId(attrs: Attributes?): Int {
        if (attrs == null)
            return 0

        val osisId: String? = attrs.getValue("", OSISUtil.OSIS_ELEMENT_VERSE)
        if (osisId == null)
            return 0

        val parts: Array<String> = osisId.split("\\.")
        return parts[parts.size - 1].toInt()
    }
}

