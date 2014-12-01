package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import android.text.style.CharacterStyle

/**
 * Created by bspeice on 12/1/14.
 */

trait TagHandler {
    fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String)
}

data class AppendArgs(val text: String, val span: Any?) {
    fun apply(builder: SpannableStringBuilder) {
        val offset = builder.length()
        builder.append(text)
        when (span) {
            is List<*> -> span.forEach { builder.setSpan(it, offset, offset + text.length, 0) }
            is CharacterStyle -> builder.setSpan(span, offset, offset + text.length, 0)
        }
        builder.setSpan(span, offset, offset + text.length, 0)
    }
}