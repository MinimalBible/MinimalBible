package org.bspeice.minimalbible.activity.downloader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.navigation.ListNavAdapter;
import org.bspeice.minimalbible.activity.navigation.NavDrawerFragment;
import org.crosswire.jsword.book.BookCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class DownloadNavDrawerFragment extends NavDrawerFragment {

    @Inject
    @Named("ValidCategories")
    List<BookCategory> validCategories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((Injector) getActivity()).inject(this);

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

        mDrawerListView.setAdapter(new ListNavAdapter<BookCategory>(getActivity(),
                validCategories));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

}
