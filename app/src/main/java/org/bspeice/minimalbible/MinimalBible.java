package org.bspeice.minimalbible;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

/**
 * Created by Bradlee Speice on 7/5/2014.
 */
public class MinimalBible extends Application {
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjGraph();
    }

    public void buildObjGraph() {
        mObjectGraph = ObjectGraph.create(Modules.list(this));
    }

    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public static MinimalBible get(Context ctx) {
        return (MinimalBible)ctx.getApplicationContext();
    }
}
