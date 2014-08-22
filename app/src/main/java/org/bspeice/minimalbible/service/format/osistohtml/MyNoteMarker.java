package org.bspeice.minimalbible.service.format.osistohtml;

import org.bspeice.minimalbible.service.format.osistohtml.OsisToHtmlSaxHandler.VerseInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;

import java.util.HashSet;
import java.util.Set;

/**
 * Display an img if the current verse has MyNote
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class MyNoteMarker {

    private Set<Integer> myNoteVerses = new HashSet<Integer>();

    private OsisToHtmlParameters parameters;

    private VerseInfo verseInfo;

    private HtmlTextWriter writer;

    public MyNoteMarker(OsisToHtmlParameters parameters, VerseInfo verseInfo, HtmlTextWriter writer) {
        this.parameters = parameters;
        this.verseInfo = verseInfo;
        this.writer = writer;

        // create hashmap of verses to optimise verse note lookup
        myNoteVerses.clear();
        if (parameters.getVersesWithNotes() != null) {
            for (Key key : parameters.getVersesWithNotes()) {
                Verse verse = KeyUtil.getVerse(key);
                myNoteVerses.add(verse.getVerse());
            }
        }
    }


    public String getTagName() {
        return "";
    }

    /**
     * just after verse start tag
     */
    public void start() {
        if (myNoteVerses != null && parameters.isShowMyNotes()) {
            if (myNoteVerses.contains(verseInfo.currentVerseNo)) {
                writer.write("<img src='file:///android_asset/images/pencil16x16.png' class='myNoteImg'/>");
            }
        }
    }

    public void end() {
    }
}
