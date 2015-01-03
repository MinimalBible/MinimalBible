package org.bspeice.minimalbible.activity.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.OGHolder;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;

import javax.inject.Inject;

import dagger.ObjectGraph;


public class BasicSearch extends BaseActivity
        implements Injector {

    @Inject
    SearchProvider searchProvider;

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
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
        searchProvider.basicTextSearch(query);
    }
}
