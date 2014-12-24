package org.bspeice.minimalbible.activity.settings

import android.preference.PreferenceActivity
import android.os.Bundle
import org.bspeice.minimalbible.R

/**
 * Created by bspeice on 12/1/14.
 */
// TODO: Fix the status bar color/transparency, and lack of toolbar
class MinimalBibleSettings() : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super<PreferenceActivity>.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)
    }
}