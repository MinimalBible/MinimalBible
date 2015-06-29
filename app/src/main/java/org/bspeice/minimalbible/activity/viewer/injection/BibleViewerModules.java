package org.bspeice.minimalbible.activity.viewer.injection;

import org.bspeice.minimalbible.activity.viewer.BibleViewer;
import org.bspeice.minimalbible.activity.viewer.BookScrollEvent;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

/**
 * Modules used for the BibleViewer activity
 */
@Module
public class BibleViewerModules {
    BibleViewer activity;

    public BibleViewerModules(BibleViewer activity) {
        this.activity = activity;
    }


    @Provides
    PublishSubject<BookScrollEvent> scrollEventPublisher() {
        return PublishSubject.create();
    }
}