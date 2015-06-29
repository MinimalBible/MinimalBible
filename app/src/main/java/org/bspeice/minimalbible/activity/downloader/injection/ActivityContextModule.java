package org.bspeice.minimalbible.activity.downloader.injection;

import org.bspeice.minimalbible.activity.downloader.DownloadActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityContextModule {

    DownloadActivity activity;

    public ActivityContextModule(DownloadActivity activity) {
        this.activity = activity;
    }

    @Provides
    DownloadActivity downloadActivity() {
        return activity;
    }
}
