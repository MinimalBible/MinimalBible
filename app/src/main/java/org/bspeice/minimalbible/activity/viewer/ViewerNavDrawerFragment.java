package org.bspeice.minimalbible.activity.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseNavigationDrawerFragment;
import org.bspeice.minimalbible.activity.viewer.bookutil.VersificationUtil;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * ExpandableListView for managing books of the Bible.
 * We extend from @link{BaseNavigationDrawerFragment} so we can inherit some of the lifecycle
 * pieces, but the actual view inflation is done by us.
 */
public class ViewerNavDrawerFragment extends BaseNavigationDrawerFragment {

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

        mActualListView.setAdapter(new BibleNavAdapter(mainBook, i));

        mActualListView.setItemChecked(mCurrentSelectedPosition, true);
        return mActualListView;
    }
}
