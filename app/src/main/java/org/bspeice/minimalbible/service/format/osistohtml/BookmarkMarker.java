package org.bspeice.minimalbible.service.format.osistohtml;

import org.bspeice.minimalbible.service.format.osistohtml.OsisToHtmlSaxHandler.VerseInfo;
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
public class BookmarkMarker {

    private Set<Integer> bookmarkedVerses = new HashSet<Integer>();

    private OsisToHtmlParameters parameters;

    private VerseInfo verseInfo;

    private HtmlTextWriter writer;

    private boolean bookmarkOpenTagWritten = false;

    public BookmarkMarker(OsisToHtmlParameters parameters, VerseInfo verseInfo, HtmlTextWriter writer) {
        this.parameters = parameters;
        this.verseInfo = verseInfo;
        this.writer = writer;

        // create hashset of verses to optimise verse note lookup
        bookmarkedVerses.clear();
        if (parameters.getVersesWithBookmarks() != null) {
            for (Verse verse : parameters.getVersesWithBookmarks()) {
                bookmarkedVerses.add(verse.getVerse());
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
        if (bookmarkedVerses != null && parameters.isShowBookmarks()) {
            if (bookmarkedVerses.contains(verseInfo.currentVerseNo)) {
                writer.write("<img src='file:///android_asset/images/GoldStar16x16.png' class='myNoteImg'/>");
//				writer.write("<span class='bookmark'>");
                bookmarkOpenTagWritten = true;
            }
        }
    }

    public void end() {
        if (bookmarkOpenTagWritten) {
//			writer.write("</span>");
            bookmarkOpenTagWritten = false;
        }
    }
}
