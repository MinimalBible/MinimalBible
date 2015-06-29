package org.bspeice.minimalbible.activity.downloader.injection;

import org.bspeice.minimalbible.activity.downloader.DownloadActivity;

import dagger.Component;

@Component(modules = ValidCategoryModule.class)
public interface DownloadActivityComponent {

    public DownloadActivity injectDownloadActivity(DownloadActivity activity);
}
