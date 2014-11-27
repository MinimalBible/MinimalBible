package org.bspeice.minimalbible.activity.viewer

import android.widget.BaseExpandableListAdapter
import android.view.View
import android.view.ViewGroup
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.getVersification
import org.crosswire.jsword.versification.getBooks
import org.crosswire.jsword.book.bookName
import android.widget.TextView
import org.bspeice.minimalbible.R
import android.content.Context
import android.view.LayoutInflater
import android.content.res.Resources
import android.support.annotation.IdRes
import android.widget.ExpandableListView
import rx.subjects.PublishSubject

/**
 * Created by bspeice on 10/24/14.
 */

class BibleMenu(val b: Book) : BaseExpandableListAdapter() {

    // Map BibleBooks to the number of chapters they have
    val menuMappings = b.getVersification().getBooks().map {
        Pair(it, b.getVersification().getLastChapter(it))
    }

    /**
     * The listener that should be registered to receive click events
     * It's created here because we need access to the menuMappings
     */
    fun getMenuClickListener(listener: PublishSubject<BookScrollEvent>) =
            object : ExpandableListView.OnChildClickListener {
                override fun onChildClick(listView: ExpandableListView?, childView: View?,
                                          groupPosition: Int, childPosition: Int, id: Long): Boolean {

                    val map = menuMappings[groupPosition]
                    listener onNext BookScrollEvent(map.first, map.second)

                    return true; // Event was handled
                }
            }

    var groupHighlighted: Int = 0
    var childHighlighted: Int = 0

    override fun getGroupCount(): Int = menuMappings.count()

    override fun getChildrenCount(group: Int): Int = menuMappings[group].second

    override fun getGroup(group: Int): String = b.bookName(menuMappings[group].first)

    override fun getChild(group: Int, child: Int): Int = child + 1 // Index offset

    override fun getGroupId(group: Int): Long = group.toLong()

    override fun getChildId(group: Int, child: Int): Long = child.toLong()

    override fun hasStableIds(): Boolean = true

    override fun isChildSelectable(group: Int, child: Int): Boolean = true

    private fun doBinding(convertView: View?, parent: ViewGroup,
                          obj: Any, highlight: Boolean): View {
        val finalView: View = convertView ?:
                (parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                        .inflate(R.layout.list_navigation_drawer, parent, false)

        val holder: NavItemHolder =
                if (finalView.getTag() != null) finalView.getTag() as NavItemHolder
                else NavItemHolder(finalView, R.id.navlist_content)

        holder.bind(obj, highlight)
        finalView setTag holder
        return finalView
    }

    override fun getGroupView(position: Int, expanded: Boolean,
                              convertView: View?, parent: ViewGroup): View =
            doBinding(convertView, parent, getGroup(position), position == groupHighlighted)

    override fun getChildView(group: Int, child: Int, isLast: Boolean,
                              convertView: View?, parent: ViewGroup): View =
            doBinding(convertView, parent, getChild(group, child), child == childHighlighted)

    class NavItemHolder(val bindTo: View, IdRes resource: Int) {
        val content = bindTo.findViewById(resource) as TextView
        val resources = bindTo.getResources(): Resources

        fun getHighlightedColor(highlighted: Boolean) = when(highlighted) {
            true -> resources getColor R.color.navbar_highlight
            else -> resources getColor R.color.navbar_unhighlighted // false
        }

        fun bind(obj: Any, highlighted: Boolean) {
            content setText obj.toString()
            content setTextColor getHighlightedColor(highlighted)
        }
    }
}
