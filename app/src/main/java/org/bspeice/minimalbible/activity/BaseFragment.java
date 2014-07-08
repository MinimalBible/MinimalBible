package org.bspeice.minimalbible.activity;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Base class that defines all behavior common to Fragments in MinimalBible
 */
public class BaseFragment extends Fragment {

    /**
     * Calculate the offset we need to display properly if the System bar is translucent
     * @param context The {@link android.app.Activity} we are displaying in
     * @param view The {@link android.view.View} we need to calculate the offset for.
     */
    protected static void setInsets(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager tintManager = new SystemBarTintManager(context);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        view.setPadding(0, config.getPixelInsetTop(true), config.getPixelInsetRight(), config.getPixelInsetBottom());
    }
}
