package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import org.xml.sax.Attributes

class DivineHandler() : TagHandler {
    override fun end(info: VerseContent, builder: SpannableStringBuilder,
                     state: ParseState) = state

    override fun start(attrs: Attributes, info: VerseContent, builder: SpannableStringBuilder,
                       state: ParseState) = state

    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String,
                        state: ParseState) =
            state append(buildDivineName(chars))

    fun buildDivineName(chars: String) =
            listOf(AppendArgs(chars take 1),
                    AppendArgs((chars drop 1).toUpperCase(), RelativeSizeSpan(.8f))
            )
}