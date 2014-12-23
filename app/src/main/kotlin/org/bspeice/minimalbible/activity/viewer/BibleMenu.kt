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
import android.widget.LinearLayout
import android.app.Activity
import android.util.AttributeSet
import kotlin.properties.Delegates
import org.bspeice.minimalbible.activity.setInset
import android.support.annotation.LayoutRes

class BibleMenu(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {
    var menuContent: ExpandableListView by Delegates.notNull();

    {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_bible_menu, this, true)

        menuContent = findViewById(R.id.menu) as ExpandableListView
    }

    fun setBible(b: Book) = menuContent.setAdapter(BibleAdapter(b))

    fun placeInset(a: Activity) = setInset(a)
}

class BibleAdapter(val b: Book) : BaseExpandableListAdapter() {

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
                    // childPosition is index-based
                    // TODO: Figure out why trying chapter 0 triggers a NotImplementedException...
                    listener onNext BookScrollEvent(map.first, childPosition + 1)

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
                          obj: Any, highlight: Boolean,
                          LayoutRes layout: Int): View {
        val finalView: View = convertView ?:
                (parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                        .inflate(layout, parent, false)

        val holder: NavItemHolder =
                if (finalView.getTag() != null) finalView.getTag() as NavItemHolder
                else NavItemHolder(finalView, R.id.content)

        holder.bind(obj, highlight)
        finalView setTag holder
        return finalView
    }

    override fun getGroupView(position: Int, expanded: Boolean,
                              convertView: View?, parent: ViewGroup): View =
            doBinding(convertView, parent, getGroup(position),
                    position == groupHighlighted, R.layout.list_bible_menu_group)

    override fun getChildView(group: Int, child: Int, isLast: Boolean,
                              convertView: View?, parent: ViewGroup): View =
            doBinding(convertView, parent, getChild(group, child),
                    group == groupHighlighted && child == childHighlighted,
                    R.layout.list_bible_menu_child)

    class NavItemHolder(val bindTo: View, IdRes resource: Int) {
        val content = bindTo.findViewById(resource) as TextView
        val resources = bindTo.getResources(): Resources

        fun getHighlightedColor(highlighted: Boolean) =
                if (highlighted) resources getColor R.color.colorAccent
                else resources getColor R.color.textColor

        fun bind(obj: Any, highlighted: Boolean) {
            content setText obj.toString()
            content setTextColor getHighlightedColor(highlighted)
        }
    }
}
