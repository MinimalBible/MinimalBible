package org.bspeice.minimalbible.activity.viewer;

import org.bspeice.minimalbible.MinimalBibleModules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

/**
 * Modules used for the BibleViewer activity
 */
@Module(
        injects = {
                BibleViewer.class,
        },
        addsTo = MinimalBibleModules.class
)
@SuppressWarnings("unused")
public class BibleViewerModules {
    BibleViewer activity;

    public BibleViewerModules(BibleViewer activity) {
        this.activity = activity;
    }


    @Provides
    @Singleton
    PublishSubject<BookScrollEvent> scrollEventPublisher() {
        return PublishSubject.create();
    }
}