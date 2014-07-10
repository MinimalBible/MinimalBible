package org.bspeice.minimalbible.activity.viewer;

import dagger.Module;

/**
 * Created by bspeice on 6/18/14.
 */
@Module(
        injects = {
                BibleViewer.class,
                BookFragment.class
        }
)
public class BibleViewerModules {
}
