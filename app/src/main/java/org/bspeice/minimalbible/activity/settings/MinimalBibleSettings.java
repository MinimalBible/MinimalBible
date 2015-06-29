package org.bspeice.minimalbible.activity.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MinimalBibleSettings extends PreferenceActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.container)
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.activity_settings);
        BaseActivity.setupStatusBar(this);

        ButterKnife.inject(this);

        toolbar.setTitle(R.string.action_settings);
        BaseActivity.setupInsets(this, root);
    }
}
