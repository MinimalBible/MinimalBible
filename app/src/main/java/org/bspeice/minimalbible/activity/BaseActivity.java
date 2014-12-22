package org.bspeice.minimalbible.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.bspeice.minimalbible.R;

/**
 * Wrapper for activities in MinimalBible to make sure we can support
 * common functionality between them all.
 */
public class BaseActivity extends ActionBarActivity {

    protected static SystemBarTintManager.SystemBarConfig getConfig(Activity context) {
        return new SystemBarTintManager(context).getConfig();
    }

    /**
     * Calculate the offset we need to display properly if the System bar is translucent
     *
     * @param context The {@link android.app.Activity} we are displaying in
     * @param view    The {@link android.view.View} we need to calculate the offset for.
     */
    @SuppressWarnings("unused")
    protected static void setInsets(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager.SystemBarConfig config = getConfig(context);
        view.setPadding(0, config.getPixelInsetTop(true), config.getPixelInsetRight(), config.getPixelInsetBottom());
    }

    @SuppressWarnings("unused")
    protected static void setInsetsSpinner(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager.SystemBarConfig config = getConfig(context);
        int marginTopBottom = config.getPixelInsetBottom() / 3;
        view.setPadding(0, config.getPixelInsetTop(true) + marginTopBottom,
                config.getPixelInsetRight(),
                marginTopBottom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Only set the tint if the device is running KitKat or above
        // TODO: Can this be set as part of styles.xml?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getResources().getColor(
                    R.color.colorPrimary));
        }
    }
}
