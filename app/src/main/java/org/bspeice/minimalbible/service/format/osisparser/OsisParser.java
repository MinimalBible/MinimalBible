package org.bspeice.minimalbible.service.format.osisparser;

import android.util.Log;

import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Verse;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 * Parse a single verse into a VerseContent object.
 * Need to benchmark if I should process ranges at a time...
 */
public class OsisParser extends DefaultHandler {

    VerseContent verseContent;
    // ArrayDeque requires API 9
    Stack<Boolean> doWrite;

    public OsisParser(Verse v) {
        verseContent = new VerseContent(v);
        doWrite = new Stack<Boolean>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        String name = getName(localName, qName);

        if (name.equals(OSISUtil.OSIS_ELEMENT_VERSE)) {
            doWrite.push(true);
        } else {
            doWrite.push(false);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        doWrite.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (ch != null && doWrite.peek()) {
            Log.e(getClass().getName(), new String(ch, start, length));
            verseContent.appendContent(new String(ch, start, length));
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    public String getName(String eName, String qName) {
        if (eName != null && eName.length() > 0) {
            return eName;
        } else {
            return qName;
        }
    }

    public int getId(Attributes attributes) {
        if (attributes == null) {
            return 0;
        }

        String osisId = attributes.getValue("", OSISUtil.OSIS_ELEMENT_VERSE);
        if (osisId == null) {
            return 0;
        }

        String[] parts = osisId.split("\\.");
        return Integer.valueOf(parts[parts.length - 1]);
    }

    public VerseContent getContent() {
        return this.verseContent;
    }
}
