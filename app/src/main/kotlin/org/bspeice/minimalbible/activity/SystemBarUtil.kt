package org.bspeice.minimalbible.activity

import android.view.View
import android.os.Build
import com.readystatesoftware.systembartint.SystemBarTintManager
import android.app.Activity

/**
 * Created by bspeice on 12/22/14.
 */

fun View.setInset(a: Activity) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
        val config = SystemBarTintManager(a).getConfig()
        this.setPadding(0, config.getPixelInsetTop(false),
                config.getPixelInsetRight(), config.getPixelInsetBottom())
    }
}
