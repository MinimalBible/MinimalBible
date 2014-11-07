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

    protected static SystemBarTintManager.SystemBarConfig getConfig(Activity context) {
        return new SystemBarTintManager(context).getConfig();
    }

    /**
     * Calculate the offset we need to display properly if the System bar is translucent
     * @param context The {@link android.app.Activity} we are displaying in
     * @param view The {@link android.view.View} we need to calculate the offset for.
     */
    @SuppressWarnings("unused")
    protected static void setInsets(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager.SystemBarConfig config = getConfig(context);
        view.setPadding(0, config.getPixelInsetTop(true), config.getPixelInsetRight(), config.getPixelInsetBottom());
    }

    protected static void setInsetsSpinner(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager.SystemBarConfig config = getConfig(context);
        int marginTopBottom = config.getPixelInsetBottom() / 3;
        view.setPadding(0, config.getPixelInsetTop(true) + marginTopBottom,
                config.getPixelInsetRight(),
                marginTopBottom);
    }

}
