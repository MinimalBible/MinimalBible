package org.bspeice.minimalbible.service.format.osisparser;

import org.crosswire.jsword.passage.Verse;

/**
 * Created by bspeice on 9/9/14.
 */
public class VerseReference {
    private Verse verse;
    private int index;

    public Verse getVerse() {
        return verse;
    }

    public void setVerse(Verse verse) {
        this.verse = verse;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
