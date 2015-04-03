package org.bspeice.minimalbible.activity.viewer;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.OGHolder;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
import org.bspeice.minimalbible.activity.search.BasicSearch;
import org.bspeice.minimalbible.activity.search.MBIndexManager;
import org.bspeice.minimalbible.activity.settings.MinimalBibleSettings;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.IndexStatus;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class BibleViewer extends BaseActivity implements Injector {

    @Inject
    @Named("MainBook")
    Book mainBook;

    @Inject
    BibleViewerPreferences prefs;

    @Inject
    PublishSubject<BookScrollEvent> scrollEventPublisher;

    @Inject
    MBIndexManager indexManager;

    @InjectView(R.id.navigation_drawer)
    BibleMenu bibleMenu;

    @InjectView(R.id.content)
    BibleView bibleContent;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ObjectGraph bvObjectGraph;

    /**
     * Build a scoped object graph for anything used by the BibleViewer
     * and inject ourselves
     * TODO: Refactor so buildObjGraph doesn't have side effects
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
     *
     * @param savedInstanceState Android's savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inject(this);

        // Check that we have a book installed
        if (mainBook == null) {
            // No books installed, start the downloader.
            Intent i = new Intent(this, DownloadActivity.class);
            startActivityForResult(i, 0);
            finish();
            return;
        }

        setContentView(R.layout.activity_bible_viewer);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        // Set up the hamburger menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        setInsetToolbar(toolbar);


        // If a new chapter is selected, make sure we close the drawer
        // It's a long lookup chain, but still holds to Law of Demeter
        scrollEventPublisher.subscribe(new Action1<BookScrollEvent>() {
            @Override
            public void call(BookScrollEvent bookScrollEvent) {
                BibleViewer.this.drawerLayout.closeDrawers();
            }
        });

        bibleMenu.doInitialize(mainBook, scrollEventPublisher);
        bibleContent.doInitialize(mainBook, prefs, scrollEventPublisher);
    }

    /**
     * Re-start the activity
     * Mostly this is used for handling a search completing, and we need to
     * display the result.
     */
    @Override
    protected void onStart() {
        super.onStart();
        handleSearchIntent(getIntent());
    }

    /**
     * Handle navigating to the chapter if we are started by searching.
     * The scrollNum will be -1 if we weren't started by search.
     *
     * @param i The intent the activity was started with
     */
    public void handleSearchIntent(Intent i) {
        Integer scrollNum = ViewerIntent.Companion.decodeSearchResult(i);
        if (scrollNum > 0) {
            scrollEventPublisher.onNext(new BookScrollEvent(mainBook, scrollNum));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewer, menu);

        // Set up search - most of it is handled by the SearchView itself, but
        // we do have some work so that it knows which activity to start
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        // And we can't call getActionView() directly, because it needs API 11+
        final MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        // The Android docs instruct you to set up search in the current activity.
        // We want the search to actually run elsewhere.
        ComponentName cN = new ComponentName(this, BasicSearch.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cN));

        // Finally, search menu should be hidden by default - show it once we can guarantee
        // than an index is created for the current book
        displaySearchMenu(item, mainBook, indexManager);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MinimalBibleSettings.class);
            startActivityForResult(i, 0);
        } else if (id == R.id.action_downloads) {
            Intent i = new Intent(this, DownloadActivity.class);
            startActivityForResult(i, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Display the search menu as needed -
     * Specifically, make the menu hidden by default, and show it once the book
     * has actually been indexed.
     *
     * @param item         The menu item to switch visibility of
     * @param b            The book controlling whether the menu is visible
     * @param indexManager Manager to generate the index if it doesn't yet exist.
     */
    public void displaySearchMenu(final MenuItem item, final Book b,
                                  final MBIndexManager indexManager) {
        if (b.getIndexStatus() == IndexStatus.DONE) {
            item.setVisible(true);
            return;
        }

        item.setVisible(false);

        if (indexManager.shouldIndex(b)) {
            indexManager.buildIndex(b)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<IndexStatus>() {
                        @Override
                        public void call(IndexStatus indexStatus) {
                            item.setVisible(indexManager.indexReady(b));
                        }
                    });
        }

        item.setVisible(indexManager.indexReady(b));
    }
}
