package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import com.orhanobut.logger.Logger
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import org.xml.sax.Attributes

class UnknownHandler(val tagName: String) : TagHandler {
    override fun end(info: VerseContent, builder: SpannableStringBuilder) {
    }

    override fun start(attrs: Attributes, info: VerseContent,
                       builder: SpannableStringBuilder) {
    }

    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String) {
        Logger.w("Unknown tag '$tagName' received text: '$chars'")
    }
}