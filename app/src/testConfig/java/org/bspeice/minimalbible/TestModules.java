package org.bspeice.minimalbible;

import org.bspeice.minimalbible.activity.download.DownloadActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Bradlee Speice on 7/5/2014.
 */
@Module(injects = DownloadActivity.class,
        overrides = true)
public class TestModules {

    public static CharSequence testActivityTitle = "Test";
    @Provides CharSequence provideString() {
        return testActivityTitle;
    }
}
