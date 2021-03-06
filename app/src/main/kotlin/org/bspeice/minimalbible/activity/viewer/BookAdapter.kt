package org.bspeice.minimalbible.activity.viewer

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import org.bspeice.minimalbible.R
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.getVersification
import org.crosswire.jsword.versification.BibleBook
import org.crosswire.jsword.versification.getBooks
import rx.subjects.PublishSubject

/**
 * Adapter used for displaying a book
 * Displays one chapter at a time,
 * as each TextView widget is it's own line break
 */
class BookAdapter(val b: Book, val prefs: BibleViewerPreferences)
: RecyclerView.Adapter<PassageView>() {

    val versification = b.getVersification()
    val bookList = versification.getBooks()
    //    val chapterCount = bookList.map { versification.getLastChapter(it) - 1 }.sum()

    /**
     * Store information needed to decode the text of a chapter
     * Book, chapter, and bibleBook should be pretty self-explanatory
     * The vStart, vEnd, and vOffset are needed to map between verses relative to their chapter,
     * and the actual verse ordinal needed for parsing the text.
     * So Genesis 1:1 would be chapter 1, bibleBook Genesis, vStart 1, vOffset 2
     * since it actually starts at ordinal 3
     */
    data class ChapterInfo(val book: Book, val chapter: Int, val bibleBook: BibleBook,
                           val vStart: Int, val vEnd: Int, val vOffset: Int)

    /**
     * A list of all ChapterInfo objects needed for displaying a book
     * The for expression probably looks a bit nicer:
     *  for {
     *      book <- bookList
     *      chapter <- 1..versification.getLastChapter(currentBook)
     *  } yield ChapterInfo(...)
     *
     *  Also note that getLastVerse() returns the number of verses in a chapter,
     *  not the actual last verse's ordinal
     */
    // TODO: Lazy compute values needed for this list
    val chapterList: List<ChapterInfo> = bookList.flatMap {
        val currentBook = it
        (1..versification.getLastChapter(currentBook)).map {
            val firstVerseOrdinal = versification.getFirstVerse(currentBook, it)
            val verseOrdinalOffset = firstVerseOrdinal - 1
            val verseCount = versification.getLastVerse(currentBook, it)
            val firstVerseRelative = 1
            val lastVerseRelative = firstVerseRelative + verseCount

            ChapterInfo(b, it, currentBook,
                    firstVerseRelative, lastVerseRelative, verseOrdinalOffset)
        }
    }

    /**
     * I'm not sure what the position argument actually represents,
     * but on initial load it doesn't change
     */
    override fun onCreateViewHolder(parent: ViewGroup?,
                                    position: Int): PassageView {
        val emptyView = LayoutInflater.from(parent!!.getContext())
                .inflate(R.layout.viewer_passage_view, parent, false) as TextView

        // TODO: Listen for changes to the text size?
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, prefs.baseTextSize().toFloat())

        val passage = PassageView(emptyView, b)
        return passage
    }

    /**
     * Bind an existing view to its chapter content
     */
    override fun onBindViewHolder(view: PassageView, position: Int) {
        prefs.currentChapter(position)
        return view bind chapterList[position]
    }

    /**
     * Get the number of chapters in the book
     */
    override fun getItemCount(): Int = chapterList.size()

    public fun bindScrollHandler(provider: PublishSubject<BookScrollEvent>,
                                 lM: RecyclerView.LayoutManager) {
        provider subscribe {
            val event = it
            lM scrollToPosition
                    // Get all objects in the form (index, object)
                    chapterList.withIndex()
                            // get one that matches our book and chapter
                            .first {
                                event.b == it.value.bibleBook &&
                                        event.chapter == it.value.chapter
                            }
                            // and get that index value to scroll to
                            .index
        }
    }
}