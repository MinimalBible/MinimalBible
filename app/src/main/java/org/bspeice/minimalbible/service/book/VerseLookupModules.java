package org.bspeice.minimalbible.service.book;

import android.support.v4.util.LruCache;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bspeice on 9/1/14.
 */
@Module(injects = VerseLookupService.class)
public class VerseLookupModules {
    private static final int MAX_SIZE = 1000000; // 1MB

    /**
     * Create a new LruCache. We're free to create new ones since they're all backed by the file
     * system anyways.
     *
     * @return The LruCache to use
     */
    @Provides
    LruCache<String, String> getLruCache() {
        return new LruCache<String, String>(MAX_SIZE);
    }
}
