package org.bspeice.minimalbible;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Holding location for activity object graphs.
 * This technique could be extended to other things, but honestly,
 * everything it could be extended to likely needs to be in
 * an ObjectGraph anyway.
 * This works because getSupportFragmentManager() is scoped to each activity.
 */
public class OGHolder<T> extends Fragment {
    private final static String TAG = "OGHolder";

    private T mComponent;

    public static OGHolder get(FragmentActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        OGHolder holder = (OGHolder) manager.findFragmentByTag(TAG);
        if (holder == null) {
            holder = new OGHolder();
            manager.beginTransaction().add(holder, TAG).commit();
        }
        return holder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public T getmComponent() {
        return mComponent;
    }

    public void setmComponent(T mComponent) {
        this.mComponent = mComponent;
    }
}
