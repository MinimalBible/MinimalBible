package org.bspeice.minimalbible;

import android.app.Application;

import org.bspeice.minimalbible.activity.downloader.DownloadActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Entry point for the default modules used by MinimalBible
 */
@Module(library = true)
public class MinimalBibleModules {
    MinimalBible app;

    public MinimalBibleModules(MinimalBible app) {
        this.app = app;
    }

    @Provides @Singleton
    Application provideApplication() {
        return app;
    }
}
