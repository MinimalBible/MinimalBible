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
}
