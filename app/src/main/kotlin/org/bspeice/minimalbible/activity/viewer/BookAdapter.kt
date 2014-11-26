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
import org.crosswire.jsword.versification.getBooks
import org.crosswire.jsword.versification.BibleBook
import org.bspeice.minimalbible.activity.viewer.BookAdapter.ChapterInfo
import android.util.Log

/**
 * Adapter used for displaying a book
 * Displays one chapter at a time,
 * as each TextView widget is it's own line break
 */
class BookAdapter(val b: Book) : RecyclerView.Adapter<PassageView>() {

    val versification = b.getVersification()
    val bookList = versification.getBooks()
    val chapterCount = bookList.map { versification.getLastChapter(it) - 1 }.sum()

    data class ChapterInfo(val book: Book, val chapter: Int, val bibleBook: BibleBook,
                           val vStart: Int, val vEnd: Int)

    // TODO: Lazy compute values needed for this list
    val chapterList: List<ChapterInfo> = bookList.flatMap {
        val currentBook = it
        (1..versification.getLastChapter(currentBook)).map { chapter ->
            Log.d("BookAdapter", "Building info for chapter ${chapter}")
            ChapterInfo(b, chapter, currentBook,
                    versification.getFirstVerse(currentBook, chapter),
                    versification.getLastVerse(currentBook, chapter))
        }
    }

    /**
     * I'm not sure what the position argument actually is,
     * but on initial load it doesn't change
     */
    override fun onCreateViewHolder(parent: ViewGroup?,
                                    position: Int): PassageView {
        val emptyView = LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.viewer_passage_view, parent, false) as TextView

        val passage = PassageView(emptyView)
        return passage
    }

    /**
     * Bind an existing view to its chapter content
     */
    override fun onBindViewHolder(view: PassageView, position: Int) =
            view bind chapterList[position]

    /**
     * Get the number of chapters in the book
     */
    override fun getItemCount(): Int = chapterCount
}

class PassageView(val v: TextView) : RecyclerView.ViewHolder(v) {

    fun bind(info: ChapterInfo) {
        val o = OsisParser()
        val string = o.getVerse(info.book, info.vEnd).content
        Log.d("PassageView", "Book: ${info.bibleBook}, Chapter: ${info.chapter}")
        v setText string
    }
}
