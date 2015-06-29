package org.bspeice.minimalbible;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.crosswire.jsword.book.sword.SwordBookPath;

import java.io.File;

/**
 * Set up the application!
 */
public class MinimalBible extends Application {
    private static Context mContext;

    public static MinimalBible get(Context ctx) {
        return (MinimalBible) ctx.getApplicationContext();
    }

    public static Context getAppContext() {
        Logger.v("Statically accessing context, please refactor that.");
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Logger.init().setLogLevel(LogLevel.NONE);
        setJswordHome();
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
        Logger.d("Setting jsword.home to: " + home);
        System.setProperty("jsword.home", home);
        System.setProperty("sword.home", home);
        SwordBookPath.setDownloadDir(new File(home));
        Logger.d("Sword download path: " + SwordBookPath.getSwordDownloadDir());
    }
}