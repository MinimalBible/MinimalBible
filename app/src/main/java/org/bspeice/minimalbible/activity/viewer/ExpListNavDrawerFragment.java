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

import rx.functions.Func1;
import rx.functions.Func2;

/**
 * ExpandableListView for managing books of the Bible.
 * We extend from @link{BaseNavigationDrawerFragment} so we can inherit some of the lifecycle
 * pieces, but the actual view inflation is done by us.
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
        // RXJava does get ridiculous with the angle brackets, you have me there. But Intellij
        // folds nicely.
        Map<String, List<Integer>> chapterMap = new HashMap<String, List<Integer>>();
        if (mainBook != null) {
            vUtil.getBooks(mainBook).map(new Func1<BibleBook, Map<String, List<Integer>>>() {
                @Override
                public Map<String, List<Integer>> call(BibleBook bibleBook) {
                    // These lines are important
                    int bookCount = vUtil.getChapterCount(mainBook, bibleBook);
                    List<Integer> chapterList = new ArrayList<Integer>(bookCount);
                    for (int i = 0; i < bookCount; i++) {
                        chapterList.add(i + 1); // Index to chapter number
                    }
                    // </important>
                    Map<String, List<Integer>> bookListMap =
                            new HashMap<String, List<Integer>>(1);
                    bookListMap.put(vUtil.getBookName(mainBook, bibleBook), chapterList);
                    return bookListMap;
                }
            })
                    .reduce(new Func2<Map<String, List<Integer>>,
                            Map<String, List<Integer>>,
                            Map<String, List<Integer>>>() {
                        @Override
                        public Map<String, List<Integer>>
                        call(Map<String, List<Integer>> acc,
                             Map<String, List<Integer>> value) {
                            // These lines are important
                            acc.putAll(value);
                            return acc;
                            // </important>
                        }
                    })
                    .toBlocking()
                    .first();
        }

        ExpListNavAdapter<String, Integer> adapter =
                new ExpListNavAdapter<String, Integer>(bibleBooks, chapterMap);

        mActualListView.setAdapter(adapter);

        mActualListView.setItemChecked(mCurrentSelectedPosition, true);
        return mActualListView;
    }
}
