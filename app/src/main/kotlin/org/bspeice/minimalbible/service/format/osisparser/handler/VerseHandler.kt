package org.bspeice.minimalbible.service.format.osisparser.handler

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SuperscriptSpan
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import org.xml.sax.Attributes

class VerseHandler() : TagHandler {

    override fun end(info: VerseContent, builder: SpannableStringBuilder) {
    }

    override fun start(attrs: Attributes, info: VerseContent,
                       builder: SpannableStringBuilder) {
        when {
            info.verseNum == 1 -> AppendArgs("${info.chapter} ", StyleSpan(Typeface.BOLD))
            else -> AppendArgs("${info.verseNum}",
                    listOf(SuperscriptSpan(), RelativeSizeSpan(.75f)))
        } apply builder
    }

    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String) {
        builder append chars
    }
}