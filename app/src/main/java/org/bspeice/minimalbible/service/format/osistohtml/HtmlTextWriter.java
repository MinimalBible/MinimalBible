package org.bspeice.minimalbible.service.format.osistohtml;

/**
 * Write characters out to a StringBuilder - used while creating html for display
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's authors.
 */
public class HtmlTextWriter {

    private StringBuilder writer;

    private int dontWriteRequestCount = 0;

    private int writeTempStoreRequestCount = 0;
    private StringBuilder tempStore = new StringBuilder();

    // allow insert at a certain position
    private String overwrittenString = "";

    public HtmlTextWriter() {
        writer = new StringBuilder();
    }

    public void write(String htmlText) {
        if (dontWriteRequestCount > 0) {
            // ignore all text
        } else if (writeTempStoreRequestCount == 0) {
            writer.append(htmlText);
        } else {
            tempStore.append(htmlText);
        }
    }

    /**
     * allow pre-verse headings
     */
    public void beginInsertAt(int insertOffset) {
        overwrittenString = writer.substring(insertOffset);
        writer.delete(insertOffset, writer.length());
    }

    /**
     * finish inserting and restore overwritten tail of string
     */
    public void finishInserting() {
        writer.append(overwrittenString);
        overwrittenString = "";
    }

    public int getPosition() {
        return writer.length();
    }

    public void removeAfter(int position) {
        writer.delete(position, writer.length());
    }

    public void reset() {
        writer.setLength(0);
    }

    public void writeToTempStore() {
        writeTempStoreRequestCount++;
    }

    public void finishWritingToTempStore() {
        writeTempStoreRequestCount--;
    }

    public void clearTempStore() {
        tempStore.delete(0, tempStore.length());
    }

    public String getTempStoreString() {
        return tempStore.toString();
    }

    public String getHtml() {
        return writer.toString();
    }

    public void setDontWrite(boolean dontWrite) {
        if (dontWrite) {
            dontWriteRequestCount++;
        } else {
            dontWriteRequestCount--;
        }
    }
}
