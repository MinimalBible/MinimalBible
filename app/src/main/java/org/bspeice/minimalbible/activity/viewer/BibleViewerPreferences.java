package org.bspeice.minimalbible.activity.viewer;

import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by bspeice on 7/11/14.
 */
@SharedPreferences
public interface BibleViewerPreferences {

    String defaultBookInitials();

    void defaultBookInitials(String defaultBookInitials);

    @Default(ofInt = 14)
    int baseTextSize();

    void baseTextSize(int baseTextSize);

    int currentChapter();

    void currentChapter(int currentChapter);
}
