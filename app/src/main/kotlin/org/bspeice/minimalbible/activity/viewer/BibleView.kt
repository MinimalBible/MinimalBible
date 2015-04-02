package org.bspeice.minimalbible.activity.viewer

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import org.bspeice.minimalbible.R
import org.crosswire.jsword.book.Book
import rx.subjects.PublishSubject

class BibleView(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {

    val layoutManager: LinearLayoutManager = LinearLayoutManager(ctx)
    val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
    val contentView: View = inflater.inflate(R.layout.view_bible, this, true)
    val bibleContent = contentView.findViewById(R.id.bible_content) as RecyclerView

    init {
        bibleContent setLayoutManager layoutManager
    }

    fun doInitialize(b: Book, prefs: BibleViewerPreferences,
                     publisher: PublishSubject<BookScrollEvent>) {
        val adapter = BookAdapter(b, prefs)
        adapter.bindScrollHandler(publisher, layoutManager)
        bibleContent setAdapter adapter
        bibleContent scrollToPosition prefs.currentChapter()
    }
}

