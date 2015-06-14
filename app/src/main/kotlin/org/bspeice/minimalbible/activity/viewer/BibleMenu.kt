package org.bspeice.minimalbible.activity.viewer

import org.bspeice.minimalbible.R
import rx.subjects.PublishSubject

class BibleMenu(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {
    val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val contentView = inflater.inflate(R.layout.view_bible_menu, this, true)
    val menuContent = contentView.findViewById(R.id._bible_menu) as ExpandableListView

    fun doInitialize(b: Book, publisher: PublishSubject<BookScrollEvent>) {
        val adapter = BibleMenuAdapter(b, publisher)
        menuContent setAdapter adapter
        publisher subscribe {
            menuContent.collapseGroup(adapter.getGroupIdForBook(it.b))
        }
    }
}

class BibleMenuGroup(val bindTo: View) {
    val content = bindTo.findViewById(R.id.content) as TextView
    val resources = bindTo.getResources()

    companion object {
        fun init(v: View, obj: Any, highlighted: Boolean): View {
            val holder =
                    if (v.getTag() != null) v.getTag() as BibleMenuGroup
                    else BibleMenuGroup(v)
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
class BibleMenuChild(val bindTo: View, val book: BibleBook,
                      val scrollPublisher: PublishSubject<BookScrollEvent>) {
    val content1 = bindTo.findViewById(R.id.content1) as TextView
    val content2 = bindTo.findViewById(R.id.content2) as TextView
    val content3 = bindTo.findViewById(R.id.content3) as TextView

    companion object {
        fun init(v: View, obj: IntRange, book: BibleBook,
                 scrollPublisher: PublishSubject<BookScrollEvent>): View {
            val holder =
                    if (v.getTag() != null) v.getTag() as BibleMenuChild
                    else BibleMenuChild(v, book, scrollPublisher)

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
