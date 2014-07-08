package org.bspeice.minimalbible.activity.downloader;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.bspeice.minimalbible.activity.BaseNavigationDrawerFragment;
import org.crosswire.jsword.book.BookCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.ObjectGraph;

public class DownloadActivity extends BaseActivity implements
		BaseNavigationDrawerFragment.NavigationDrawerCallbacks,
        Injector {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private DownloadNavDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

    @Inject @Named("ValidCategories")
    List<BookCategory> validCategories;

    private ObjectGraph daObjectGraph;

    /**
     * Build a scoped object graph for anything used by the DownloadActivity
     */
    private void buildObjGraph() {
        if (daObjectGraph == null) {
            daObjectGraph = MinimalBible.get(this)
                    .plus(new DownloadActivityModules(this));
        }
        daObjectGraph.inject(this);
    }

    @Override
    public void inject(Object o) {
        buildObjGraph();
        daObjectGraph.inject(o);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        buildObjGraph();
		setContentView(R.layout.activity_download);

		mNavigationDrawerFragment = (DownloadNavDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
        //TODO: Switch to AutoFactory pattern, rather than newInstance()
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						BookListFragment.newInstance(validCategories.get(position))).commit();
	}

	public void onSectionAttached(String category) {
		mTitle = category;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.download, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
