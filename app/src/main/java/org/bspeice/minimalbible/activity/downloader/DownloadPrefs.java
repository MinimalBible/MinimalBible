package org.bspeice.minimalbible.activity.downloader;

import de.devland.esperandro.annotations.SharedPreferences;

/**
 * SharedPreferences interface to be built by Esperandro
 */
@SharedPreferences
public interface DownloadPrefs {

    boolean hasEnabledDownload();

    void hasEnabledDownload(boolean hasEnabledDownload);

    boolean hasShownDownloadDialog();

    void hasShownDownloadDialog(boolean hasShownDownloadDialog);

    long downloadRefreshedOn();

    void downloadRefreshedOn(long downloadRefreshedOn);

}
