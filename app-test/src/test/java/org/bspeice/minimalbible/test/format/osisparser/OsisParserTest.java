package org.bspeice.minimalbible.test.format.osisparser;

import android.annotation.SuppressLint;

import org.bspeice.minimalbible.service.format.osisparser.OsisParser;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Verse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.Attributes;

import static org.junit.Assert.assertEquals;
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

    // During initial development, I accidentally set up the verseContent
    // as a value computed every time - so you'd get a new "content" every time
    // you tried to update it. Thus, if you updated the content only once, you're fine.
    // Try and update multiple times, and things would start going crazy.
    // TODO: Why is this ignored?
    @SuppressLint("NewApi")
    @SuppressWarnings("unused")
    @Ignore
    public void ignoreTestVerseContentConsistent() {
        String string1 = "1";
        String string2 = "2";

        // Yes, I need intimate knowledge of the state machine to run this test
        // Also, Verse is final, so I can't mock that, which is why this test is ignored.
        Verse mockVerse = mock(Verse.class);
        parser.setVerse(mockVerse);
        parser.getDoWrite().push(true);
        parser.characters(string1.toCharArray(), 0, string1.length());
        assertEquals(parser.getVerseContent().getContent(), string1);

        parser.characters(string2.toCharArray(), 0, string2.length());
        assertEquals(parser.getVerseContent().getContent(), string1 + string2);
    }
}
