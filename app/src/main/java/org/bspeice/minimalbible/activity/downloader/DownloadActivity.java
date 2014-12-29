package org.bspeice.minimalbible.activity.downloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.OGHolder;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.bspeice.minimalbible.activity.settings.MinimalBibleSettings;
import org.crosswire.jsword.book.BookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;

public class DownloadActivity extends BaseActivity implements
        Injector,
        AdapterView.OnItemClickListener {

    @Inject
    @Named("ValidCategories")
    List<BookCategory> validCategories;

    @InjectView(R.id.navigation_drawer)
    LinearLayout navigationDrawer;

    @InjectView(R.id.navigation_content)
    ListView navigationContent;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ObjectGraph daObjectGraph;

    /**
     * Build a scoped object graph for anything used by the DownloadActivity
     */
    private void buildObjGraph() {
        if (daObjectGraph == null) {
            OGHolder holder = OGHolder.get(this);
            ObjectGraph holderGraph = holder.fetchGraph();
            if (holderGraph == null) {
                daObjectGraph = MinimalBible.get(this)
                        .plus(new DownloadActivityModules(this));
                holder.persistGraph(daObjectGraph);
            } else {
                daObjectGraph = holderGraph;
            }
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
        inject(this);

        setContentView(R.layout.activity_download);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        // Set up the hamburger menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationContent.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        validCategories));
        navigationContent.setOnItemClickListener(this);
        setInsets(navigationDrawer);
        handleSelect(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MinimalBibleSettings.class);
            startActivityForResult(i, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(@NotNull AdapterView<?> parent,
                            @NotNull View view, int position, long id) {
        handleSelect(position);
    }

    public void handleSelect(int position) {
        // update the main content by replacing fragments
        //TODO: Switch to AutoFactory pattern, rather than newInstance()
        BookCategory category = validCategories.get(position);
        setTitle(category.toString());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.content,
                        BookListFragment.newInstance(validCategories.get(position)))
                .commit();

        drawerLayout.closeDrawers();
    }

    private void setTitle(String title) {
        toolbar.setTitle(title);
    }
}
