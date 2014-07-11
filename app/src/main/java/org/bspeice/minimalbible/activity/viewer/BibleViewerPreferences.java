package org.bspeice.minimalbible.activity.viewer;

import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by bspeice on 7/11/14.
 */
@SharedPreferences(name = "BibleViewerPreferences")
public interface BibleViewerPreferences {

    String defaultBookName();
    void defaultBookName(String defaultBookName);
}
