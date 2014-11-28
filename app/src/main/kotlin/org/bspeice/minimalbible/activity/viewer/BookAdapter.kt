package org.bspeice.minimalbible.activity.viewer

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.crosswire.jsword.book.Book
import android.view.LayoutInflater
import org.bspeice.minimalbible.R
import android.widget.TextView
import org.crosswire.jsword.book.getVersification
import org.crosswire.jsword.versification.getBooks
import org.crosswire.jsword.versification.BibleBook
import org.bspeice.minimalbible.activity.viewer.BookAdapter.ChapterInfo
import rx.subjects.PublishSubject
import org.bspeice.minimalbible.service.lookup.VerseLookup

/**
 * Adapter used for displaying a book
 * Displays one chapter at a time,
 * as each TextView widget is it's own line break
 */
class BookAdapter(val b: Book, val lookup: VerseLookup)
: RecyclerView.Adapter<PassageView>() {

    val versification = b.getVersification()
    val bookList = versification.getBooks()
    val chapterCount = bookList.map { versification.getLastChapter(it) - 1 }.sum()

    data class ChapterInfo(val book: Book, val chapter: Int, val bibleBook: BibleBook,
                           val vStart: Int, val vEnd: Int)

    /**
     * A list of all ChapterInfo objects needed for displaying a book
     * The for expression probably looks a bit nicer:
     *  for {
     *      book <- bookList
     *      chapter <- 1..versification.getLastChapter(currentBook)
     *  } yield ChapterInfo(...)
     *
     *  Also note that getLastVerse() returns the number of verses in a chapter,
     *  so we build the actual last verse by adding getFirstVerse and getLastVerse
     */
    // TODO: Lazy compute values needed for this list
    val chapterList: List<ChapterInfo> = bookList.flatMap {
        val currentBook = it
        (1..versification.getLastChapter(currentBook)).map {
            val firstVerse = versification.getFirstVerse(currentBook, it)
            val verseCount = versification.getLastVerse(currentBook, it)
            ChapterInfo(b, it, currentBook, firstVerse, firstVerse + verseCount)
        }
    };

    /**
     * I'm not sure what the position argument actually represents,
     * but on initial load it doesn't change
     */
    override fun onCreateViewHolder(parent: ViewGroup?,
                                    position: Int): PassageView {
        val emptyView = LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.viewer_passage_view, parent, false) as TextView

        val passage = PassageView(emptyView, b, lookup)
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

    fun bindScrollHandler(provider: PublishSubject<BookScrollEvent>,
                          lM: RecyclerView.LayoutManager) {
        provider subscribe {
            val event = it
            lM scrollToPosition
                    // Get all objects in the form (index, object)
                    chapterList.withIndices()
                            // Get one that matches our book and chapter
                            .first {
                                event.b == it.second.bibleBook &&
                                        event.chapter == it.second.chapter
                            }
                            // And get that index value to scroll to
                            .first
        }
    }
}

class PassageView(val v: TextView, val b: Book, val lookup: VerseLookup)
: RecyclerView.ViewHolder(v) {

    fun getVerseText(verseRange: Progression<Int>) =
            verseRange.map { lookup.getText(b.getVersification().decodeOrdinal(it)) }

    fun reduceText(verses: List<String>) = verses.join(" ")

    // Uses functional style, but those parentheses man... you'd think I was writing LISP
    fun bind(info: ChapterInfo) {
        v.setText(reduceText(getVerseText(info.vStart..info.vEnd)))
    }
}
