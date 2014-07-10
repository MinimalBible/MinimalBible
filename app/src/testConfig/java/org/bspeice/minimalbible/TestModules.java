package org.bspeice.minimalbible;


import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
import org.crosswire.jsword.book.BookCategory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Bradlee Speice on 7/5/2014.
 */
@Module(injects = DownloadActivity.class,
        overrides = true,
        library = true)
public class TestModules {

    public static CharSequence testActivityTitle = "Test";
    @Provides CharSequence provideString() {
        return testActivityTitle;
    }

    /**
     * Provide an application-wide hub to enable/disable a "testing" mode
     * Each application is free to interpret what this means, but allows for programming
     * different behavior to respond to different testing needs in code that can't be mocked
     * *cough cough* `Activities`.
     * @return
     */
    @Provides
    @Named("Testing")
    boolean isTest() {
        return isTest;
    }

    @Provides @Singleton
    @Named("ValidCategories")
    List<BookCategory> provideValidCategories() {
        return new ArrayList<BookCategory>() {{
            add(BookCategory.BIBLE);
            add(BookCategory.COMMENTARY);
            add(BookCategory.DICTIONARY);
            add(BookCategory.MAPS);
        }};
    }

    private boolean isTest;

    public void setTestMode(boolean isTest) {
        this.isTest = isTest;
    }

    public boolean getTestMode() {
        return isTest;
    }
}
