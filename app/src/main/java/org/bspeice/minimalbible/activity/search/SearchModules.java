package org.bspeice.minimalbible.activity.search;

import android.support.annotation.Nullable;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.IndexManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchModules {

    @Provides
    SearchProvider searchProvider(@Nullable Book book,
                                  IndexManager indexManager) {
        return new SearchProvider(indexManager, book);
    }
}
