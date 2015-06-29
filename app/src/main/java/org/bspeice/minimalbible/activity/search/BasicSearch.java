package org.bspeice.minimalbible.activity.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.bspeice.minimalbible.common.injection.MainBookModule;
import org.crosswire.jsword.passage.Verse;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class BasicSearch extends BaseActivity {

    @Inject
    SearchProvider searchProvider;

    @InjectView(R.id.content)
    SearchResultsListView resultsView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;


    public void inject() {
        // TODO: Cache the component
        BasicSearchComponent component = DaggerBasicSearchComponent.builder()
                .mainBookModule(new MainBookModule(this))
                .build();
        component.injectBasicSearch(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        inject();
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
