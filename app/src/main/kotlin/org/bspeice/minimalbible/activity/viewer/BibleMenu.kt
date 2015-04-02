package org.bspeice.minimalbible.activity.viewer

import android.content.Context
import android.content.res.Resources
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.TextView
import org.bspeice.minimalbible.R
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.bookName
import org.crosswire.jsword.book.getVersification
import org.crosswire.jsword.versification.BibleBook
import org.crosswire.jsword.versification.getBooks
import rx.subjects.PublishSubject

class BibleMenu(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {
    val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val contentView = inflater.inflate(R.layout.view_bible_menu, this, true)
    val menuContent = contentView.findViewById(R.id._bible_menu) as ExpandableListView

    fun doInitialize(b: Book, publisher: PublishSubject<BookScrollEvent>) {
        val adapter = BibleAdapter(b, publisher)
        menuContent setAdapter adapter
        publisher subscribe {
            menuContent.collapseGroup(adapter.getGroupIdForBook(it.b))
        }
    }
}

/**
 * The actual adapter for displaying a book's menu navigation system.
 * There are a couple of notes about this:
 *  Books are displayed with one row per BibleBook (Genesis, Exodus, etc.) as the group.
 *  Within each group, there are 3 chapters listed per row (to save space). In order to
 *  accommodate this, some slightly funky mathematics have to be used, and this is documented.
 *  Additionally, it doesn't make a whole lot of sense to genericize this using constants
 *  unless we go to programmatic layouts, since we still need to know the view ID's ahead of time.
 *
 * TODO: Refactor this so the math parts are separate from the actual override functions,
 * so it's easier to test.
 */
class BibleAdapter(val b: Book, val scrollPublisher: PublishSubject<BookScrollEvent>)
: BaseExpandableListAdapter() {

    // Map BibleBooks to the number of chapters they have
    val menuMappings = b.getVersification().getBooks().map {
        Pair(it, b.getVersification().getLastChapter(it))
    }

    fun getGroupIdForBook(b: BibleBook) = menuMappings.indexOf(
            menuMappings.first { it.first == b }
    )

    var groupHighlighted: Int = 0

    override fun getGroupCount(): Int = menuMappings.count()

    fun getChaptersForGroup(group: Int) = menuMappings[group].second

    /**
     * Get the number of child views for a given book.
     * What makes this complicated is that we display 3 chapters per row.
     * To make sure we include everything and account for integer division,
     * we have to add a row if the chapter count modulo 3 is not even.
     */
    override fun getChildrenCount(group: Int): Int {
        val chapterCount = getChaptersForGroup(group)
        return when (chapterCount % 3) {
            0 -> chapterCount / 3
            else -> (chapterCount / 3) + 1
        }
    }

    override fun getGroup(group: Int): String = b.bookName(menuMappings[group].first)

    /**
     * Get the starting chapter number for this child view
     * In order to account for displaying 3 chapters per line,
     * we need to multiply by three, and then add 1 for the index offset
     */
    override fun getChild(group: Int, child: Int): Int = (child * 3) + 1

    override fun getGroupId(group: Int): Long = group.toLong()

    override fun getChildId(group: Int, child: Int): Long = child.toLong()

    override fun hasStableIds(): Boolean = true

    override fun isChildSelectable(group: Int, child: Int): Boolean = true

    override fun getGroupView(position: Int, expanded: Boolean,
                              convertView: View?, parent: ViewGroup): View =
            GroupItemHolder.init(
                    getOrInflate(convertView, parent, R.layout.list_bible_menu_group),
                    getGroup(position),
                    position == groupHighlighted)

    override fun getChildView(group: Int, child: Int, isLast: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        val chapterStart = getChild(group, child)
        val chapterCount = getChaptersForGroup(group)
        val chapterEnd =
                if (chapterCount < chapterStart + 2)
                    chapterCount
                else
                    chapterStart + 2
        val view = ChildItemHolder.init(
                getOrInflate(convertView, parent, R.layout.list_bible_menu_child),
                chapterStart..chapterEnd,
                menuMappings[group].first,
                scrollPublisher
        )

        return view
    }

    private fun getOrInflate(v: View?, p: ViewGroup, LayoutRes layout: Int) =
            v ?: (p.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                    .inflate(layout, p, false)
}

class GroupItemHolder(val bindTo: View) {
    val content = bindTo.findViewById(R.id.content) as TextView
    val resources = bindTo.getResources(): Resources

    companion object {
        fun init(v: View, obj: Any, highlighted: Boolean): View {
            val holder =
                    if (v.getTag() != null) v.getTag() as GroupItemHolder
                    else GroupItemHolder(v)
            holder.bind(obj, highlighted)

            return v
        }
    }

    fun getHighlightedColor(highlighted: Boolean) =
            if (highlighted) resources getColor R.color.colorAccent
            else resources getColor R.color.textColor

    fun bind(obj: Any, highlighted: Boolean) {
        content setText obj.toString()
        content setTextColor getHighlightedColor(highlighted)
    }
}

/**
 * Bind the child items. There are some funky math things going on since
 * we display three chapters per row, check the adapter for more documentation
 */
class ChildItemHolder(val bindTo: View, val book: BibleBook,
                      val scrollPublisher: PublishSubject<BookScrollEvent>) {
    val content1 = bindTo.findViewById(R.id.content1) as TextView
    val content2 = bindTo.findViewById(R.id.content2) as TextView
    val content3 = bindTo.findViewById(R.id.content3) as TextView

    companion object {
        fun init(v: View, obj: IntRange, book: BibleBook,
                 scrollPublisher: PublishSubject<BookScrollEvent>): View {
            val holder =
                    if (v.getTag() != null) v.getTag() as ChildItemHolder
                    else ChildItemHolder(v, book, scrollPublisher)

            holder.clearViews()
            holder.bind(obj)
            return v
        }
    }

    fun buildOnClickListener(chapter: Int): View.OnClickListener =
            View.OnClickListener { scrollPublisher onNext BookScrollEvent(book, chapter) }

    // Clear the views before binding, so that we don't have stale text left
    // as a result of recycling. There should probably be a different way of doing this,
    // but get something that works first.
    fun clearViews() {
        content1 setText ""
        content2 setText ""
        content3 setText ""
    }

    /**
     * Calculate which view should hold the chapter. We remove 1 before the modulus
     * in order to use index-based addressing. If we didn't remove 1, position 1 would receive
     * content2, since 1 modulus 3 is 1.
     */
    fun getViewForPosition(position: Int) = when ((position - 1) % 3) {
        0 -> content1
        1 -> content2
        else -> content3
    }

    /**
     * Set up the view with the data we want to display
     */
    fun bind(range: IntRange) = range.forEach {
        val view = getViewForPosition(it)
        view setText it.toString()
        view setOnClickListener buildOnClickListener(it)
    }
}
