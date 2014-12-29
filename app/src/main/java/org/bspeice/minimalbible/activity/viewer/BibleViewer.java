package org.bspeice.minimalbible.activity.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.OGHolder;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
import org.bspeice.minimalbible.activity.settings.MinimalBibleSettings;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
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

        // Currently you must set up the menu in exactly this order. Do I need to refactor this?
        bibleMenu.setScrollEventPublisher(scrollEventPublisher);
        bibleMenu.setBible(mainBook);

        // If a new chapter is selected, make sure we close the drawer
        // We can't specify `this` as the subscriber since we can't
        // extend Action1
        scrollEventPublisher.subscribe(new Action1<BookScrollEvent>() {
            @Override
            public void call(BookScrollEvent bookScrollEvent) {
                closeMenu();
            }
        });

        // Set up the view to respond to scroll events as well. Again, exact order is needed.
        bibleContent.setScrollPublisher(scrollEventPublisher);
        bibleContent.setBook(mainBook, prefs);
    }

    public void closeMenu() {
        drawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewer, menu);
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
}
