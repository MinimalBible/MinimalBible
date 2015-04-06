package org.bspeice.minimalbible.activity.viewer

import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.widget.TextView
import com.orhanobut.logger.Logger
import org.bspeice.minimalbible.service.format.osisparser.OsisParser
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.book.getVersification

class PassageView(val v: TextView, val b: Book)
: RecyclerView.ViewHolder(v) {

    fun buildOrdinal(verse: Int, info: BookAdapter.ChapterInfo) =
            b.getVersification().decodeOrdinal(verse + info.vOffset)

    fun getAllVerses(verses: Progression<Int>, info: BookAdapter.ChapterInfo): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        val parser = OsisParser()
        verses.forEach { parser.appendVerse(b, buildOrdinal(it, info), builder) }
        return builder
    }

    fun bind(info: BookAdapter.ChapterInfo) {
        Logger.d("PassageView", "Binding chapter ${info.chapter}")
        v setText getAllVerses(info.vStart..info.vEnd, info)
    }
}