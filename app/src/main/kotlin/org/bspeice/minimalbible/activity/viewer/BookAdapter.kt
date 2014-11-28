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
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.text.style.SuperscriptSpan
import android.text.style.RelativeSizeSpan

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

    // Span to be applied to an individual verse - doesn't know about the sizes
    // of other verses so that's why start and end are relative
    /**
     * A holder object that knows how apply itself to a SpannableStringBuilder
     * Since we don't know ahead of time where this verse will end up relative to the
     * entire TextView (since there is one chapter per TextView) we use a start and end
     * relative to the verse text itself. That is, rStart of 0 indicates verse text start,
     * and rEnd of (text.length - 1) indicates the end of verse text
     * @param span The span object we should apply
     * @param rStart When the span should begin, relative to the verse
     * @param rEnd When the span should end, relative to the verse
     */
    data class SpanHolder(val span: Any?, val rStart: Int, val rEnd: Int) {
        // TODO: Is there a more case-class like way of doing this?
        class object {
            val EMPTY = SpanHolder(null, 0, 0)
        }
        /**
         * Apply this span object to the specified builder
         * Tries to be as close to immutable as possible. The offset is used to calculate
         * the absolute position of when this span should start and end, since
         * rStart and rEnd are relative to the verse text, and know nothing about the
         * rest of the text in the TextView
         */
        fun apply(builder: SpannableStringBuilder, offset: Int): SpannableStringBuilder {
            if (span != null)
                builder.setSpan(span, rStart + offset, rEnd + offset, 0)
            return builder
        }
    }

    // TODO: getRawVerse shouldn't know how to decode ints
    fun getRawVerse(verse: Int): String =
            lookup.getText(b.getVersification().decodeOrdinal(verse))

    // TODO: This code is nasty, not sure how to refactor, but it needs doing
    fun getProcessedVerse(verseOrdinal: Int, info: ChapterInfo): Pair<String, List<SpanHolder>> {
        val rawText = getRawVerse(verseOrdinal)
        // To be honest, I have no idea why I need to subtract one. But I do.
        val relativeVerse = verseOrdinal - info.vOffset - 1
        val processedText = when (relativeVerse) {
            0 -> ""
            1 -> "${info.chapter} $rawText"
            else -> "$relativeVerse$rawText"
        }
        val spans: List<SpanHolder> = listOf(
                when (relativeVerse) {
                    0 -> SpanHolder.EMPTY
                    1 -> SpanHolder(StyleSpan(Typeface.BOLD), 0, info.chapter.toString().length)
                    else -> SpanHolder(SuperscriptSpan(), 0, relativeVerse.toString().length)
                }
        )
        val secondSpan =
                if (relativeVerse > 1)
                // TODO: No magic numbers!
                    spans plus SpanHolder(RelativeSizeSpan(0.6f), 0, relativeVerse.toString().length)
                else
                    spans

        return Pair(processedText, secondSpan)
    }

    fun getAllVerses(verses: Progression<Int>, info: ChapterInfo) =
            verses.map { getProcessedVerse(it + info.vOffset, info) }
                    // For each verse, get the text
                    .fold(SpannableStringBuilder(), { initialBuilder, versePair ->
                        val offset = initialBuilder.length()
                        val builderWithText = initialBuilder append versePair.first

                        // And apply all spans
                        versePair.second.fold(builderWithText, { postBuilder, span ->
                            span.apply(postBuilder, offset)
                        })
                    })

    fun bind(info: ChapterInfo) {
        v setText getAllVerses(info.vStart..info.vEnd, info)
    }
}
