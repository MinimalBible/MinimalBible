package org.bspeice.minimalbible;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Bradlee Speice on 7/5/2014.
 */
@Module(injects = DownloadActivity.class,
        overrides = true)
public class TestModules {

    @Provides String provideString() {
        return "Test";
    }
}
