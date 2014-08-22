package org.bspeice.minimalbible.service.format;

/**
 * Info on a note or cross reference
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class Note {

    public static final String SUMMARY = "summary";
    ;
    public static final String DETAIL = "detail";
    private static final String TAG = "Note";
    private String noteRef;
    private String noteText;

    public Note(int verseNo, String noteRef, String noteText, NoteType noteType, String osisRef) {
        super();
        this.noteRef = noteRef;
        this.noteText = noteText;
    }

    @Override
    public String toString() {
        return noteRef + ":" + noteText;
    }

    public enum NoteType {TYPE_GENERAL, TYPE_REFERENCE}
}
