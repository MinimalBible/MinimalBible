package org.bspeice.minimalbible.activity.viewer;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.OGHolder;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;

public class BibleViewer extends BaseActivity implements Injector {

    @Inject
    @Named("MainBook")
    Book mainBook;

    @InjectView(R.id.navigation_drawer)
    ListView drawerContent;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private ObjectGraph bvObjectGraph;

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
     * @param savedInstanceState Android's savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.inject(this);

        setContentView(R.layout.activity_bible_viewer);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        String[] drawerStrings = new String[]{"Content 1", "Content 2"};
        drawerContent.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                drawerStrings));

        setInsets(this, drawerContent);
    }
}
