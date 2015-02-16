package org.bspeice.minimalbible.activity.search;

import org.bspeice.minimalbible.MinimalBibleModules;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.IndexManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = BasicSearch.class,
        addsTo = MinimalBibleModules.class
)
public class SearchModules {

    @Provides
    SearchProvider searchProvider(@Named("MainBook") Book book,
                                  IndexManager indexManager) {
        return new SearchProvider(book, indexManager);
    }


}
