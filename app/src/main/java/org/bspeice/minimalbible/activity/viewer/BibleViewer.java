package org.bspeice.minimalbible.activity.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.OGHolder;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseActivity;
import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
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
    BibleMenu bibleMenu;

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
        bibleMenu.setBible(mainBook);
        setInsets(this, bibleMenu);
    }
}
