package org.bspeice.minimalbible.service.format.osistohtml;

import org.bspeice.minimalbible.service.format.osistohtml.OsisToHtmlSaxHandler.VerseInfo;
import org.crosswire.jsword.book.OSISUtil;
import org.xml.sax.Attributes;

import static org.bspeice.minimalbible.service.format.Constants.HTML;

/**
 * Write the verse number at the beginning of a verse
 * Also handle verse per line
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class VerseHandler {

    private OsisToHtmlParameters parameters;

    private VerseInfo verseInfo;

    private int writerRollbackPosition;

    private HtmlTextWriter writer;

    public VerseHandler(OsisToHtmlParameters parameters, VerseInfo verseInfo, HtmlTextWriter writer) {
        this.parameters = parameters;
        this.verseInfo = verseInfo;
        this.writer = writer;
    }


    public String getTagName() {
        return OSISUtil.OSIS_ELEMENT_VERSE;
    }

    public void startAndUpdateVerse(Attributes attrs) {
        writerRollbackPosition = writer.getPosition();

        if (attrs != null) {
            verseInfo.currentVerseNo = TagHandlerHelper.osisIdToVerseNum(attrs.getValue("", OSISUtil.OSIS_ATTR_OSISID));
        } else {
            verseInfo.currentVerseNo++;
        }

        if (parameters.isVersePerline()) {
            //close preceding verse
            if (verseInfo.currentVerseNo > 1) {
                writer.write("</div>");
            }
            // start current verse
            writer.write("<div>");
        }

        writeVerse(verseInfo.currentVerseNo);
    }

    public void end() {
        if (!verseInfo.isTextSinceVerse) {
            writer.removeAfter(writerRollbackPosition);
        }
    }

    private void writeVerse(int verseNo) {
        verseInfo.positionToInsertBeforeVerse = writer.getPosition();

        // The id is used to 'jump to' the verse using javascript so always need the verse tag with an id
        // Do not show verse 0
        StringBuilder verseHtml = new StringBuilder();
        if (parameters.isShowVerseNumbers() && verseNo != 0) {
            verseHtml.append(" <span class='verse' id='").append(verseNo).append("'>").append(verseNo).append("</span>").append(HTML.NBSP);
        } else {
            // we really want an empty span but that is illegal and causes problems such as incorrect verse calculation in Psalms
            // so use something that will hopefully interfere as little as possible - a zero-width-space
            // also put a space before it to allow a separation from the last word of previous verse or to be ignored if start of line
            verseHtml.append(" <span class='verse' id='").append(verseNo).append("'/>&#x200b;</span>");
        }
        writer.write(verseHtml.toString());
    }
}
