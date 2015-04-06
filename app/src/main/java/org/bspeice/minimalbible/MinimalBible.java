package org.bspeice.minimalbible;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.crosswire.jsword.book.sword.SwordBookPath;

import java.io.File;

import dagger.ObjectGraph;

/**
 * Set up the application!
 */
public class MinimalBible extends Application implements Injector {
    private String TAG = "MinimalBible";
    private ObjectGraph mObjectGraph;

    public static MinimalBible get(Context ctx) {
        return (MinimalBible) ctx.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init().setLogLevel(LogLevel.NONE);
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

    /**
     * Notify jSword that it needs to store files in the Android internal directory
     * NOTE: Android will uninstall these files if you uninstall MinimalBible.
     */
    @SuppressWarnings("null")
    private void setJswordHome() {
        // We need to set the download directory for jSword to stick with
        // Android.
        String home = this.getFilesDir().toString();
        Logger.d(TAG, "Setting jsword.home to: " + home);
        System.setProperty("jsword.home", home);
        System.setProperty("sword.home", home);
        SwordBookPath.setDownloadDir(new File(home));
        Logger.d(TAG, "Sword download path: " + SwordBookPath.getSwordDownloadDir());
    }
}