package org.bspeice.minimalbible.activity.viewer

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.passage.Verse
import android.view.View
import android.view.LayoutInflater
import org.bspeice.minimalbible.R
import android.widget.TextView
import org.bspeice.minimalbible.service.format.osisparser.OsisParser
import org.crosswire.jsword.book.getVersification

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
//        passage.v setText o.getVerse(b, position).content
        return passage
    }

    /**
     * Bind an existing view
     */
    override fun onBindViewHolder(view: PassageView, position: Int) {
        val o = OsisParser()
        view.v setText o.getVerse(b, position).content
    }

    /**
     * Get the number of chapters in the book
     */
    override fun getItemCount(): Int = b.getVersification()
            .getAllVerses().getEnd().getOrdinal()
}

class PassageView(val _v: View) : RecyclerView.ViewHolder(_v) {

    val v = _v as TextView

}
