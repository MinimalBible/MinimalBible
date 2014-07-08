package org.bspeice.minimalbible.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.bspeice.minimalbible.R;

/**
 * Wrapper for activities in MinimalBible to make sure we can support
 * common functionality between them all.
 */
public class BaseActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Only set the tint if the device is running KitKat or above
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(getResources().getColor(
					R.color.statusbar));
		}
	}

}
