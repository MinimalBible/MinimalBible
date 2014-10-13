package org.bspeice.minimalbible.activity.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.navigation.ExpListNavAdapter;
import org.bspeice.minimalbible.activity.navigation.NavDrawerFragment;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.VersificationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import rx.functions.Action1;

/**
 * ExpandableListView for managing books of the Bible.
 * We extend from @link{BaseNavigationDrawerFragment} so we can inherit some of the lifecycle
 * pieces, but the actual view inflation is done by us.
 * I tried to refactor this into Kotlin, but I need to inject the vUtil and mainBook,
 * and trying to getActivity() as BibleViewer yielded TypeCastException
 * TODO: Extend BaseExpNavigationDrawerFragment?
 */
public class ExpListNavDrawerFragment extends NavDrawerFragment {

    @Inject VersificationUtil vUtil;
    @Inject @Named("MainBook")
    Book mainBook;

    ExpandableListView mActualListView;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Injector i = (Injector) getActivity();
        i.inject(this);

        mActualListView = (ExpandableListView) inflater.inflate(
                R.layout.fragment_expandable_navigation_drawer, container, false);
        /*
        mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectItem(position);
                    }
                });
        */

        List<String> bibleBooks;
        if (mainBook != null) {
            bibleBooks = vUtil.getBookNames(mainBook)
                    .toList().toBlocking().first();
        } else {
            bibleBooks = new ArrayList<String>();
        }


        // I really don't like how we build the chapters, but I'm not adding Guava just for Range.
        // This isn't a totally functional style map-reduce, but the reduce step is
        // unnecessarily verbose. Like this comment.
        final Map<String, List<Integer>> chapterMap = new HashMap<String, List<Integer>>();
        if (mainBook != null) {
            vUtil.getBooks(mainBook).forEach(new Action1<BibleBook>() {
                @Override
                public void call(BibleBook bibleBook) {
                    int bookCount = vUtil.getChapterCount(mainBook, bibleBook);
                    List<Integer> chapterList = new ArrayList<Integer>(bookCount);
                    for (int i = 0; i < bookCount; i++) {
                        chapterList.add(i + 1); // Index to chapter number
                    }
                    chapterMap.put(vUtil.getBookName(mainBook, bibleBook), chapterList);
                }
            });

            ExpListNavAdapter<String, Integer> adapter =
                    new ExpListNavAdapter<String, Integer>(bibleBooks, chapterMap);

            mActualListView.setAdapter(adapter);
        }

        mActualListView.setItemChecked(mCurrentSelectedPosition, true);
        return mActualListView;
    }
}
