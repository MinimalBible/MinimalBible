package org.bspeice.minimalbible.activity.navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.bspeice.minimalbible.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 * TODO: Refactor to allow ExpandableListView
 */
public class NavDrawerFragment extends AbstractNavDrawerFragment {

    protected ListView mDrawerListView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This could also be a ScrollView
        ListView list = (ListView) view.findViewById(R.id.list_nav_drawer);
        // This could also be set in your layout, allows the list items to
        // scroll through the bottom padded area (navigation bar)
        list.setClipToPadding(false);
        // Sets the padding to the insets (include action bar and navigation bar
        // padding for the current device and orientation)
        super.setInsets(list);
    }

    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
            ((ListNavAdapter<String>) mDrawerListView.getAdapter()).setCurrentlyHighlighted(position);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    public int getCurrentPosition() {
        return mCurrentSelectedPosition;
    }
}
