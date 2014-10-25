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

/**
 * Created by bspeice on 10/24/14.
 */

class BibleMenu(val b: Book) : BaseExpandableListAdapter() {

    // Map BibleBooks to the number of chapters they have
    val menuMappings = b.getVersification().getBooks().map {
        Pair(it, b.getVersification().getLastChapter(it))
    }

    var groupHighlighted: Int = 0
    var childHighlighted: Int = 0

    override fun getGroupCount(): Int = menuMappings.count()

    override fun getChildrenCount(group: Int): Int = menuMappings.elementAt(group).component2()

    override fun getGroup(group: Int): String =
            b.bookName(menuMappings.elementAt(group).component1())

    override fun getChild(group: Int, child: Int): Int = child + 1 // Index offset

    override fun getGroupId(group: Int): Long = group.toLong()

    override fun getChildId(group: Int, child: Int): Long = child.toLong()

    override fun hasStableIds(): Boolean = true

    override fun isChildSelectable(group: Int, child: Int): Boolean = true

    private fun doBinding(convertView: View?, parent: ViewGroup?,
                          obj: Any, highlight: Boolean): View {
        val finalView: View = if (convertView != null) convertView
        else {
            val inflater = parent!!.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_navigation_drawer, parent, false)
        }

        val holder: NavItemHolder = if (finalView.getTag() != null) finalView.getTag() as NavItemHolder
        else NavItemHolder(finalView, R.id.navlist_content)

        holder.bind(obj, highlight)
        finalView.setTag(holder)
        return finalView
    }

    override fun getGroupView(position: Int, expanded: Boolean,
                              convertView: View?, parent: ViewGroup?): View =
            doBinding(convertView, parent, getGroup(position), position == groupHighlighted)

    override fun getChildView(group: Int, child: Int, isLast: Boolean,
                              convertView: View?, parent: ViewGroup?): View =
            doBinding(convertView, parent, getChild(group, child), child == childHighlighted)

    // Resource should be IdRes
    class NavItemHolder(val bindTo: View, resource: Int) {
        val content: TextView = bindTo.findViewById(resource) as TextView

        fun bind(obj: Any, highlighted: Boolean) {
            content.setText(obj.toString())
            if (highlighted)
                content.setTextColor(bindTo.getResources().getColor(R.color.navbar_highlight))
            else
                content.setTextColor(bindTo.getResources().getColor(R.color.navbar_unhighlighted))
        }
    }

}