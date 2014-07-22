package org.bspeice.minimalbible;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

import dagger.ObjectGraph;

/**
 * Holding location for activity object graphs.
 * This technique could be extended to other things, but honestly,
 * everything it could be extended to likely needs to be in
 * an ObjectGraph anyway.
 * This works because getSupportFragmentManager() is scoped to each activity.
 * TODO: Prove the above claim.
 */
public class OGHolder extends Fragment {
    private final static String TAG = "OGHolder";

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void persistGraph(ObjectGraph graph) {
        mObjectGraph = graph;
    }

    public ObjectGraph fetchGraph() {
        return mObjectGraph;
    }

    public static OGHolder get(FragmentActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        OGHolder holder = (OGHolder) manager.findFragmentByTag(TAG);
        if (holder == null) {
            holder = new OGHolder();
            manager.beginTransaction().add(holder, TAG).commit();
        }
        return holder;
    }
}
