package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import com.orhanobut.logger.Logger
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import org.xml.sax.Attributes

class UnknownHandler(val tagName: String) : TagHandler {
    override fun end(info: VerseContent, builder: SpannableStringBuilder,
                     state: ParseState): ParseState = state

    override fun start(attrs: Attributes, info: VerseContent, builder: SpannableStringBuilder,
                       state: ParseState) = state

    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String,
                        state: ParseState): ParseState {
        Logger.v("Unknown tag '$tagName' received text: '$chars'")
        return state
    }
}