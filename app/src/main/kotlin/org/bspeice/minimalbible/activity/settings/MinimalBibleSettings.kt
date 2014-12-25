package org.bspeice.minimalbible.activity.settings

import android.preference.PreferenceActivity
import android.os.Bundle
import org.bspeice.minimalbible.R
import android.support.v7.widget.Toolbar
import org.bspeice.minimalbible.activity.BaseActivity
import android.widget.LinearLayout

/**
 * Created by bspeice on 12/1/14.
 */
class MinimalBibleSettings() : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super<PreferenceActivity>.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)
        setContentView(R.layout.activity_settings)
        BaseActivity.setupStatusBar(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.action_settings)

        val root = findViewById(R.id.container) as LinearLayout
        BaseActivity.setupInsets(this, root)
    }
}