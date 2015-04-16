package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import org.xml.sax.Attributes

class QHandler(val color: Int) : TagHandler {

    fun retrieveMarker(attrs: Attributes) =
            AppendArgs(attrs getValue "marker" ?: "")

    override fun start(attrs: Attributes, info: VerseContent, builder: SpannableStringBuilder,
                       state: ParseState) = state append retrieveMarker(attrs)

    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String,
                        state: ParseState) =
            state append AppendArgs(chars.trim() + " ", ForegroundColorSpan(color))

    override fun end(info: VerseContent, builder: SpannableStringBuilder,
                     state: ParseState) = state
}
