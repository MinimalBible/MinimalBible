package org.bspeice.minimalbible.activity.viewer

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.passage.Verse
import android.view.View
import android.view.LayoutInflater
import org.bspeice.minimalbible.R
import android.widget.TextView

/**
 * Adapter used for displaying a book
 * Displays one chapter at a time,
 * as each TextView widget is it's own line break
 */
class BookAdapter(val b: Book) : RecyclerView.Adapter<PassageView>() {

    /**
     * Create a new view
     */
    override fun onCreateViewHolder(parent: ViewGroup?,
                                    position: Int): PassageView? {
        val emptyView = LayoutInflater.from(parent?.getContext())
            .inflate(R.layout.viewer_passage_view, parent, false)

        val passage = PassageView(emptyView)
        passage.v setText "1"
        return passage
    }

    /**
     * Bind an existing view
     */
    override fun onBindViewHolder(view: PassageView, position: Int) {
        view.v setText "${Integer.parseInt(view.v.getText() as String) + 1}"
    }

    /**
     * Get the number of chapters in the book
     */
    override fun getItemCount(): Int = 800
}

class PassageView(val _v: View) : RecyclerView.ViewHolder(_v) {

    val v = _v as TextView

}
