package org.bspeice.minimalbible.activity.viewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.viewer.bookutil.VersificationUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;

/**
 * ExpandableListView Navigation Drawer
 * TODO: Refactor out to ExpandableNavDrawerAdapter?
 */
public class BibleNavAdapter extends BaseExpandableListAdapter {

    @Inject
    VersificationUtil vUtil;
    Versification versification;
    Book book;

    // Now we could technically implement this structure using a LinkedHashMap, but
    // it's easier both to understand and program if we implement this using two maps.
    Map<Integer, BibleBook> indexableBibleBooks;
    Map<BibleBook, Integer> chaptersForBook;

    private int groupHighlighted;
    private int childHighlighted;

    public BibleNavAdapter(final Book b, Injector injector) {
        injector.inject(this);
        this.book = b;
        versification = vUtil.getVersification(book);

        // Let the map know ahead of time how big it will be
        // int bookCount = versification.getBookCount();
        indexableBibleBooks = new HashMap<Integer, BibleBook>();
        chaptersForBook = new HashMap<BibleBook, Integer>();

        final AtomicInteger counter = new AtomicInteger(0);
        vUtil.getBooks(book)
                .forEach(new Action1<BibleBook>() {
                    @Override
                    public void call(BibleBook bibleBook) {
                        indexableBibleBooks.put(counter.getAndIncrement(), bibleBook);
                        chaptersForBook.put(bibleBook, vUtil.getChapterCount(b, bibleBook));
                    }
                });
    }

    @Override
    public int getGroupCount() {
        return indexableBibleBooks.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return chaptersForBook.get(indexableBibleBooks.get(i));
    }

    @Override
    public String getGroup(int i) {
        return vUtil.getBookName(book, indexableBibleBooks.get(i));
    }

    /**
     * Take a shortcut - since the second item is the (indexed) chapter number,
     * we just need to add one to remove the off-by-one
     *
     * @param i  The group position
     * @param i2 The child position
     * @return The child chapter value
     */
    @Override
    public Integer getChild(int i, int i2) {
        return i2 + 1;
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
        return false;
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
        NavItemHolder<String> bookHolder;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_navigation_drawer,
                    parent, false);
            bookHolder = new NavItemHolder<String>(convertView);
            convertView.setTag(bookHolder);
        } else {
            bookHolder = (NavItemHolder<String>) convertView.getTag();
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
        NavItemHolder<Integer> chapterHolder;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_navigation_drawer,
                    parent, false);
            chapterHolder = new NavItemHolder<Integer>(convertView);
            convertView.setTag(chapterHolder);
        } else {
            chapterHolder = (NavItemHolder<Integer>) convertView.getTag();
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
     */
    class NavItemHolder<T> {
        @InjectView(R.id.navlist_content)
        TextView content;

        View v;

        public NavItemHolder(View v) {
            this.v = v; // Needed for resolving colors below
            ButterKnife.inject(this, v);
        }

        public void bind(T object, boolean highlighted) {
            content.setText(object.toString());
            if (highlighted) {
                content.setTextColor(v.getResources().getColor(R.color.navbar_highlight));
            } else {
                content.setTextColor(v.getResources().getColor(R.color.navbar_unhighlighted));
            }
        }
    }
}