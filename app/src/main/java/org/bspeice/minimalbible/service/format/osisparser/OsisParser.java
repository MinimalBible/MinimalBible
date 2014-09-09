package org.bspeice.minimalbible.service.format.osisparser;

import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Verse;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 * Created by bspeice on 9/8/14.
 */
public class OsisParser extends DefaultHandler {

    VerseContent verseContent;
    // ArrayDeque requires API 9
    Stack<Boolean> doWrite;
    Verse verse;

    public OsisParser(Verse v) {
        this.verse = v;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        String name = getName(localName, qName);

        if (name.equals(OSISUtil.OSIS_ELEMENT_VERSE)) {
            doWrite.push(true);
            verseContent.setId(getId(attributes));
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
            verseContent.appendContent(new String(ch));
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
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
