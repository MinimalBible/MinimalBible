package org.bspeice.minimalbible.activity.downloader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseNavigationDrawerFragment;
import org.crosswire.jsword.book.BookCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class DownloadNavDrawerFragment extends BaseNavigationDrawerFragment {

    @Inject @Named("ValidCategories")
    List<BookCategory> validCategories;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        ((DownloadActivity)getActivity()).inject(this);

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

		mDrawerListView.setAdapter(new ArrayAdapter<BookCategory>(getActionBar()
				.getThemedContext(), android.R.layout.simple_list_item_1,
				android.R.id.text1, validCategories));
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		return mDrawerListView;
	}

}
