package org.bspeice.minimalbible.activity.viewer.injection;

import org.bspeice.minimalbible.activity.viewer.BibleViewer;
import org.bspeice.minimalbible.common.injection.InvalidBookModule;
import org.bspeice.minimalbible.common.injection.MainBookModule;

import dagger.Component;

@Component(modules = {MainBookModule.class, BibleViewerModules.class,
        InvalidBookModule.class})
public interface ViewerComponent {

    public BibleViewer injectBibleViewer(BibleViewer viewer);
}
