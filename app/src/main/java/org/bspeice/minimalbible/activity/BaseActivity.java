package org.bspeice.minimalbible.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.bspeice.minimalbible.R;

/**
 * Wrapper for activities in MinimalBible to make sure we can support
 * common functionality between them all.
 */
public class BaseActivity extends ActionBarActivity {

    // TODO: Refactor these methods to a utility class
    public static void setupStatusBar(Activity activity) {
        // Only set the tint if the device is running KitKat or above
        // TODO: Can this be set as part of styles.xml?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(activity.getResources()
                    .getColor(R.color.colorPrimary));
        }
    }

    public static void setupInsets(Activity context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager.SystemBarConfig config = getConfig(context);
            view.setPadding(0, config.getPixelInsetTop(false),
                    config.getPixelInsetRight(), config.getPixelInsetBottom());
        }
    }

    /**
     * Calculate the offset needed for a Toolbar
     * The reason we need a separate method is because we don't want
     * the SystemBarTintManager calculating an offset for the navigation bar
     *
     * @param context
     * @param view
     */
    public static void setupToolbar(Activity context, Toolbar view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager.SystemBarConfig config = getConfig(context);
            view.setPadding(0, config.getPixelInsetTop(false),
                    config.getPixelInsetRight(), 0);
        }
    }

    protected static SystemBarTintManager.SystemBarConfig getConfig(Activity context) {
        return new SystemBarTintManager(context).getConfig();
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

    /**
     * Calculate the offset we need to display properly if the System bar is translucent
     *
     * @param view    The {@link android.view.View} we need to calculate the offset for.
     */
    @SuppressWarnings("unused")
    protected void setInsets(View view) {
        setupInsets(this, view);
    }

    protected void setInsetToolbar(Toolbar toolbar) {
        setupToolbar(this, toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupStatusBar(this);
    }
}
