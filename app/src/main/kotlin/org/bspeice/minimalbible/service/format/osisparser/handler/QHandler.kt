package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import org.xml.sax.Attributes

class QHandler(val color: Int) : TagHandler {

    override fun start(attrs: Attributes, info: VerseContent, builder: SpannableStringBuilder) {
    }

    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String) {
        AppendArgs(chars, ForegroundColorSpan(color)) apply builder
    }

    override fun end(info: VerseContent, builder: SpannableStringBuilder) {
        AppendArgs(" ", null) apply builder
    }
}
