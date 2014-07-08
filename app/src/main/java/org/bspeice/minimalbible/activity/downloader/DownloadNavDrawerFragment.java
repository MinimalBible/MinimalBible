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
import org.bspeice.minimalbible.activity.downloader.manager.DownloadManager;

public class DownloadNavDrawerFragment extends BaseNavigationDrawerFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		
		String[] sCategories = new String[DownloadManager.VALID_CATEGORIES.length];
		for (int i = 0; i < DownloadManager.VALID_CATEGORIES.length; i++) {
			sCategories[i] = DownloadManager.VALID_CATEGORIES[i].toString();
		}
		
		mDrawerListView.setAdapter(new ArrayAdapter<String>(getActionBar()
				.getThemedContext(), android.R.layout.simple_list_item_1,
				android.R.id.text1, sCategories));
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		return mDrawerListView;
	}

}
