package org.bspeice.minimalbible;

import android.app.Application;

import javax.inject.Named;
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

    /**
     * This field allows us to set application-wide whether we are in a test or not
     * Allows components on down the line to know whether they should set some things up or not.
     * Additionally, not a Singleton so we can enable/disable testing mode as needed. However,
     * for production, it's always false.
     * @return Whether we are in a test - false
     */
    @Provides
    @Named("Testing")
    boolean isTest() {
        return false;
    }
}
