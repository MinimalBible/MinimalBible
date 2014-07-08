package org.bspeice.minimalbible;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.crosswire.jsword.book.sword.SwordBookPath;

import java.io.File;

import dagger.ObjectGraph;

/**
 * Created by Bradlee Speice on 7/5/2014.
 */
public class MinimalBible extends Application implements Injector {
    private String TAG = "MinimalBible";
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjGraph();
        setJswordHome();
    }

    public void buildObjGraph() {
        mObjectGraph = ObjectGraph.create(Modules.list(this));
    }

    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public ObjectGraph plus(Object... modules) {
        return mObjectGraph.plus(modules);
    }

    public static MinimalBible get(Context ctx) {
        return (MinimalBible)ctx.getApplicationContext();
    }

    /**
     * Notify jSword that it needs to store files in the Android internal directory
     * NOTE: Android will uninstall these files if you uninstall MinimalBible.
     */
    @SuppressWarnings("null")
    private void setJswordHome() {
        // We need to set the download directory for jSword to stick with
        // Android.
        String home = this.getFilesDir().toString();
        Log.d(TAG, "Setting jsword.home to: " + home);
        System.setProperty("jsword.home", home);
        System.setProperty("sword.home", home);
        SwordBookPath.setDownloadDir(new File(home));
        Log.d(TAG, "Sword download path: " + SwordBookPath.getSwordDownloadDir());
    }
}
