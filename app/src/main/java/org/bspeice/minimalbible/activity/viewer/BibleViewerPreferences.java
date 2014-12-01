package org.bspeice.minimalbible.activity.viewer;

import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by bspeice on 7/11/14.
 */
@SharedPreferences
public interface BibleViewerPreferences {

    String defaultBookName();
    void defaultBookName(String defaultBookName);

    @Default(ofInt = 14)
    int baseTextSize();

    void baseTextSize(int baseTextSize);
}
