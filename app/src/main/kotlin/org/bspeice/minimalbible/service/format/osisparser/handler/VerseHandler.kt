package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.text.style.SuperscriptSpan
import android.text.style.RelativeSizeSpan

/**
 * Created by bspeice on 12/1/14.
 */
class VerseHandler() : TagHandler {
    var isVerseStart = true

    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String) {
        buildVerseHeader(info.chapter, info.verseNum, isVerseStart) apply builder
        builder append chars
        isVerseStart = false
    }

    fun buildVerseHeader(chapter: Int, verseNum: Int, verseStart: Boolean): AppendArgs =
            when {
                !verseStart -> AppendArgs("", null)
                verseNum == 1 -> AppendArgs("$chapter ", StyleSpan(Typeface.BOLD))
                else -> AppendArgs("${verseNum}", listOf(SuperscriptSpan(), RelativeSizeSpan(.75f)))
            }
}