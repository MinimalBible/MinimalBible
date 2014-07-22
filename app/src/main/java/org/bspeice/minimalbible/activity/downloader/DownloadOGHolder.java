package org.bspeice.minimalbible.activity.downloader;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import dagger.ObjectGraph;

/**
 * Created by bspeice on 7/21/14.
 */
public class DownloadOGHolder extends Fragment {

    ObjectGraph holder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void persistObjectGraph(ObjectGraph holder) {
        this.holder = holder;
    }

    public ObjectGraph fetchObjectGraph() {
        return this.holder;
    }
}
