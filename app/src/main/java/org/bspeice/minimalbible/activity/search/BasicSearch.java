package org.bspeice.minimalbible.activity.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.OGHolder;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.crosswire.jsword.passage.Verse;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;


public class BasicSearch extends BaseActivity
        implements Injector {

    @Inject
    SearchProvider searchProvider;

    @InjectView(R.id.content)
    SearchResultsListView resultsView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private ObjectGraph searchObjGraph;

    private void buildObjGraph() {
        if (searchObjGraph == null) {
            OGHolder holder = OGHolder.get(this);
            searchObjGraph = holder.fetchGraph();
            if (searchObjGraph == null) {
                searchObjGraph = MinimalBible.get(this)
                        .plus(new SearchModules());
                holder.persistGraph(searchObjGraph);
            }
        }
        searchObjGraph.inject(this);
    }

    @Override
    public void inject(Object o) {
        buildObjGraph();
        searchObjGraph.inject(o);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        inject(this);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        // We don't set toolbar insets assuming that fitsSystemWindows="true"
        // Also don't set "Up" enabled, back is enough.

        handleSearch(getIntent());
    }


    // Used for launchMode="singleTop"
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSearch(intent);
    }

    public void handleSearch(Intent intent) {
        if (!Intent.ACTION_SEARCH.equals(intent.getAction()))
            return;

        String query = intent.getStringExtra(SearchManager.QUERY);
        List<Verse> results = searchProvider.basicTextSearch(query);

        displayTitle(query, results.size());
        displaySearch(results);
    }

    public void displayTitle(String query, Integer resultsSize) {
        // We can't go through the actual `toolbar` object, we have to
        // getSupportActionBar() first.
        // http://stackoverflow.com/a/26506858/1454178
        getSupportActionBar().setTitle(buildTitle(query, resultsSize));
    }

    public String buildTitle(String query, Integer resultsSize) {
        return "\"" + query + "\" - " + resultsSize + " results";
    }

    // TODO: Inject the book into BasicSearch instead of pulling it out of searchProvider?
    public void displaySearch(List<Verse> searchResults) {
        resultsView.initialize(searchProvider.getBook(), searchResults);
    }
}
