package org.bspeice.minimalbible.activity.search;

import org.bspeice.minimalbible.MinimalBibleModules;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = BasicSearch.class,
        addsTo = MinimalBibleModules.class
)
public class SearchModules {

    @Provides
    SearchProvider searchProvider() {
        return new SearchProvider();
    }
}
