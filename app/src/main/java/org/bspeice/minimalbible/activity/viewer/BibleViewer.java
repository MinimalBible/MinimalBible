package org.bspeice.minimalbible.activity.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.OGHolder;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
import org.bspeice.minimalbible.activity.navigation.NavDrawerFragment;
import org.bspeice.minimalbible.service.manager.BookManager;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;

import dagger.ObjectGraph;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class BibleViewer extends BaseActivity implements
        NavDrawerFragment.NavigationDrawerCallbacks,
        Injector {

    @Inject BookManager bookManager;

    private ObjectGraph bvObjectGraph;
    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    private ExpListNavDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Build a scoped object graph for anything used by the BibleViewer
     */
    private void buildObjGraph() {
        if (bvObjectGraph == null) {
            OGHolder holder = OGHolder.get(this);
            bvObjectGraph = holder.fetchGraph();
            if (bvObjectGraph == null) {
                bvObjectGraph = MinimalBible.get(this)
                        .plus(new BibleViewerModules(this));
                holder.persistGraph(bvObjectGraph);
            }
        }
        bvObjectGraph.inject(this);
    }

    @Override
    public void inject(Object o) {
        buildObjGraph();
        bvObjectGraph.inject(o);
    }

    /**
     * Set up the application
     * TODO: Get the main book, rather than the first installed book.
     *
     * @param savedInstanceState Android's savedInstanceState
     */
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        this.inject(this);

        // If no books are installed, we need to download one first. However,
        // RxJava will error if there's nothing installed.
        bookManager.getInstalledBooks()
                .first()
                .onErrorReturn(new Func1<Throwable, Book>() {
                    @Override
                    public Book call(Throwable throwable) {
                        // If there are no books installed...
                        Log.e(getLocalClassName(), "No books are currently installed, starting DownloadManager");
                        Intent i = new Intent(BibleViewer.this, DownloadActivity.class);
                        startActivityForResult(i, 0);
                        finish();
                        return null;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        Log.e("BibleViewer", "Subscribed to display book: " + book.getName());
                        displayMainBook(book);
                    }
                });
        setContentView(R.layout.activity_bible_viewer);

        mNavigationDrawerFragment = (ExpListNavDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// Handle a navigation movement
	}

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        mTitle = title;
        actionBar.setTitle(title);
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
			getMenuInflater().inflate(R.menu.main, menu);
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
		} else if (id == R.id.action_downloads) {
			startActivity(new Intent(this, DownloadActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

    private void displayMainBook(Book b) {
        Log.d("BibleViewer", "Initializing main book: " + b.getName());
        Log.d("MainThread?", Boolean.toString(Looper.myLooper() == Looper.getMainLooper()));
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = BookFragment.newInstance(b.getName());
        fragmentManager.beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }

}
