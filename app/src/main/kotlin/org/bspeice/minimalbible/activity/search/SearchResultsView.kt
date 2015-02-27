package org.bspeice.minimalbible.activity.search

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import org.bspeice.minimalbible.R
import org.crosswire.jsword.passage.Verse
import android.widget.TextView
import org.bspeice.minimalbible.service.format.osisparser.OsisParser
import org.crosswire.jsword.book.Book
import android.view.ViewGroup

/**
 * Created by bspeice on 2/26/15.
 */
class SearchResultsListView(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {

    val layoutManager = LinearLayoutManager(ctx)
    val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val contentView = inflater.inflate(R.layout.view_search_results_list, this, true)
    val searchResults = contentView.findViewById(R.id.search_results) as RecyclerView;

    {
        searchResults setLayoutManager layoutManager
    }

    fun initialize(b: Book, resultsList: List<Verse>) {
        searchResults.setAdapter(SearchResultsAdapter(b, resultsList))
    }
}

// TODO: Handle clicking an item and navigating on the main screen
class SearchResultsAdapter(val b: Book, val results: List<Verse>)
: RecyclerView.Adapter<ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResultViewHolder? {
        val resultView = SearchResultView(parent)
        return ResultViewHolder(resultView)
    }

    override fun onBindViewHolder(holder: ResultViewHolder?, position: Int) {
        holder?.bind(b, results[position])
    }

    override fun getItemCount(): Int = results.size()
}

/**
 * The ViewHolder object for an individual search result
 * TODO: Bold the text found in the query
 */
class ResultViewHolder(val view: SearchResultView) : RecyclerView.ViewHolder(view.contentView) {

    // TODO: Need a nicer way of displaying the book name - currently is ALL CAPS
    fun buildVerseName(v: Verse) = "${v.getBook().name()} ${v.getChapter()}:${v.getVerse()}"

    fun buildVerseContent(b: Book, v: Verse, o: OsisParser) = o.parseVerse(b, v)

    fun bind(b: Book, verse: Verse) {
        view.verseName setText buildVerseName(verse)
        view.verseContent setText buildVerseContent(b, verse, OsisParser())
    }
}

/**
 * A custom view to wrap showing a search result
 */
class SearchResultView(val group: ViewGroup?) {
    val inflater = LayoutInflater.from(group?.getContext())
    val contentView = inflater.inflate(R.layout.view_search_result, group, false)

    val verseName = contentView.findViewById(R.id.verseName) as TextView
    val verseContent = contentView.findViewById(R.id.verseContent) as TextView
}
