package org.bspeice.minimalbible.activity.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseNavigationDrawerFragment;
import org.bspeice.minimalbible.activity.viewer.bookutil.VersificationUtil;
import org.crosswire.jsword.book.Book;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class ViewerNavDrawerFragment extends BaseNavigationDrawerFragment {

    @Inject VersificationUtil vUtil;
    @Inject @Named("MainBook")
    Book mainBook;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        ((Injector)getActivity()).inject(this);

		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});
        List<String> bookNames = vUtil.getNiceBookNames(mainBook)
                .toList().toBlocking().first();

		mDrawerListView.setAdapter(new ArrayAdapter<String>(getActionBar()
				.getThemedContext(), android.R.layout.simple_list_item_1,
				android.R.id.text1, bookNames));
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		return mDrawerListView;
	}
}
