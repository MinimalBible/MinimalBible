package org.bspeice.minimalbible.activity.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.MinimalBibleModules;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Module;
import dagger.ObjectGraph;

/**
 * Created by bspeice on 12/29/14.
 */
public class MinimalBibleSettings extends PreferenceActivity
        implements Injector {

    ObjectGraph settingsGraph;
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

    private ObjectGraph buildObjGraph() {
        MinimalBible app = (MinimalBible) getApplicationContext();

        return app.plus(new SettingsModule());
    }

    @Override
    public void inject(Object o) {
        if (settingsGraph == null) {
            settingsGraph = buildObjGraph();
        }
        settingsGraph.inject(o);
    }

    @Module(injects = AvailableBookPreference.class,
            addsTo = MinimalBibleModules.class)
    class SettingsModule {

    }
}
