package org.bspeice.minimalbible.activity.viewer

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import org.bspeice.minimalbible.R
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.bookName
import org.crosswire.jsword.book.getVersification
import org.crosswire.jsword.versification.BibleBook
import org.crosswire.jsword.versification.getBooks
import rx.subjects.PublishSubject

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
class BibleMenuAdapter(val b: Book, val scrollPublisher: PublishSubject<BookScrollEvent>)
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
            BibleMenuGroup.init(
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
        val view = BibleMenuChild.init(
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