package org.bspeice.minimalbible.test.format.osisparser;

import android.annotation.SuppressLint;

import org.bspeice.minimalbible.service.format.osisparser.OsisParser;
import org.crosswire.jsword.book.OSISUtil;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * State machine testing, oh boy!
 */
public class OsisParserTest {

    OsisParser parser;

    @Before
    public void setUp() {
        parser = new OsisParser();
    }

    @SuppressLint("NewApi")
    @Test
    public void testDoWriteEnabledVerse() {
        Attributes attributes = mock(Attributes.class);
        parser.startElement("", OSISUtil.OSIS_ELEMENT_VERSE, "", attributes);
        assertTrue(parser.getDoWrite().peek());
    }

    @SuppressLint("NewApi")
    private void parserAssert(OsisParser parser, String element) {
        Attributes attributes = mock(Attributes.class);
        parser.startElement("", element, "", attributes);
        assertFalse(parser.getDoWrite().isEmpty());
        parser.getDoWrite().pop();
    }

    @Test
    public void testDoWriteAlwaysAdded() {
        parserAssert(parser, OSISUtil.OSIS_ELEMENT_VERSE);
        parserAssert(parser, "");
        parserAssert(parser, "random string");
        parserAssert(parser, OSISUtil.OSIS_ELEMENT_CELL);
    }

    @SuppressLint("NewApi")
    @Test
    public void testEndElementPopsQueue() {
        parser.getDoWrite().add(true);
        parser.endElement("", "", "");
        assertTrue(parser.getDoWrite().isEmpty());
    }
}
