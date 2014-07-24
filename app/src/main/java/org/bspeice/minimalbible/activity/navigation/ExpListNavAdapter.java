package org.bspeice.minimalbible.activity.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.bspeice.minimalbible.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * ExpandableListView Navigation Drawer
 * T1 represents Group objects, T2 is child objects
 * Not sure if I'll ever actually need to re-use this, but go ahead and make it generic.
 * TODO: Document this.
 */
public class ExpListNavAdapter<T1, T2> extends BaseExpandableListAdapter {

    // Now we could technically implement this structure using a LinkedHashMap, but
    // it's easier both to understand and program if we implement this using two maps.
    Map<Integer, T1> indexableBibleBooks;
    Map<T1, List<T2>> chaptersForBook;

    private int groupHighlighted;
    private int childHighlighted;

    public ExpListNavAdapter(List<T1> groups, Map<T1, List<T2>> children) {

        // Let the map know ahead of time how big it will be
        // int bookCount = versification.getBookCount();
        indexableBibleBooks = new HashMap<Integer, T1>(groups.size());
        chaptersForBook = new HashMap<T1, List<T2>>(groups.size());

        // Is it terrible that I don't like using an actual for loop?
        for (int index = 0; index < groups.size(); index++) {
            T1 gItem = groups.get(index);
            indexableBibleBooks.put(index, gItem);
            chaptersForBook.put(gItem, children.get(gItem));
        }
    }

    @Override
    public int getGroupCount() {
        return indexableBibleBooks.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return chaptersForBook.get(indexableBibleBooks.get(i)).size();
    }

    @Override
    public T1 getGroup(int i) {
        return indexableBibleBooks.get(i);
    }

    /**
     * @param i  The group position
     * @param i2 The child position
     * @return The child chapter value
     */
    @Override
    public T2 getChild(int i, int i2) {
        return chaptersForBook.get(indexableBibleBooks.get(i)).get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Get the views for this group
     *
     * @param position
     * @param expanded
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int position, boolean expanded,
                             View convertView, ViewGroup parent) {
        NavItemHolder<T1> bookHolder;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_navigation_drawer,
                    parent, false);
            bookHolder = new NavItemHolder<T1>(convertView);
            convertView.setTag(bookHolder);
        } else {
            bookHolder = (NavItemHolder<T1>) convertView.getTag();
        }

        bookHolder.bind(getGroup(position), position == groupHighlighted);
        return convertView;
    }

    /**
     * Get the view for a child
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        NavItemHolder<T2> chapterHolder;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_navigation_drawer,
                    parent, false);
            chapterHolder = new NavItemHolder<T2>(convertView);
            convertView.setTag(chapterHolder);
        } else {
            chapterHolder = (NavItemHolder<T2>) convertView.getTag();
        }

        chapterHolder.bind(getChild(groupPosition, childPosition),
                childPosition == childHighlighted);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public void setGroupHighlighted(int groupHighlighted) {
        this.groupHighlighted = groupHighlighted;
    }

    public void setChildHighlighted(int childHighlighted) {
        this.childHighlighted = childHighlighted;
    }

    /**
     * Class to hold elements for the navbar - doesn't matter if they're group or child.
     * T3 is either T1 or T2, doesn't matter.
     */
    class NavItemHolder<T3> {
        @InjectView(R.id.navlist_content)
        TextView content;

        View v;

        public NavItemHolder(View v) {
            this.v = v; // Needed for resolving colors below
            ButterKnife.inject(this, v);
        }

        public void bind(T3 object, boolean highlighted) {
            content.setText(object.toString());
            if (highlighted) {
                content.setTextColor(v.getResources().getColor(R.color.navbar_highlight));
            } else {
                content.setTextColor(v.getResources().getColor(R.color.navbar_unhighlighted));
            }
        }
    }
}