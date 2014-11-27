package org.bspeice.minimalbible.activity.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.navigation.NavDrawerFragment;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;
import javax.inject.Named;

import rx.subjects.PublishSubject;

/**
 * ExpandableListView for managing books of the Bible.
 * We extend from @link{BaseNavigationDrawerFragment} so we can inherit some of the lifecycle
 * pieces, but the actual view inflation is done by us.
 * I tried to refactor this into Kotlin, but I need to inject the vUtil and mainBook,
 * and trying to getActivity() as BibleViewer yielded TypeCastException
 * TODO: Extend BaseExpNavigationDrawerFragment?
 */
public class BookChapterNavFragment extends NavDrawerFragment {

    @Inject @Named("MainBook")
    Book mainBook;

    @Inject
    PublishSubject<BookScrollEvent> scrollListener;

    ExpandableListView mActualListView;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Injector i = (Injector) getActivity();
        i.inject(this);

        BibleMenu menu = new BibleMenu(mainBook);
        mActualListView = (ExpandableListView) inflater.inflate(
                R.layout.fragment_expandable_navigation_drawer, container, false);
        mActualListView.setAdapter(menu);
        mActualListView.setOnChildClickListener(menu.getMenuClickListener(scrollListener));

        mActualListView.setItemChecked(mCurrentSelectedPosition, true);
        return mActualListView;
    }
}
