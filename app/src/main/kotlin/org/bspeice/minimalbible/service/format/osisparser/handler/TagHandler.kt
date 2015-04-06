package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import org.xml.sax.Attributes

trait TagHandler {
    fun start(attrs: Attributes, info: VerseContent, builder: SpannableStringBuilder)
    fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String)
    fun end(info: VerseContent, builder: SpannableStringBuilder)
}

data class AppendArgs(val text: String, val span: Any?) {
    fun apply(builder: SpannableStringBuilder) {
        val offset = builder.length()
        builder.append(text)
        when (span) {
            is List<*> -> span.forEach { builder.setSpan(it, offset, offset + text.length(), 0) }
            is CharacterStyle -> builder.setSpan(span, offset, offset + text.length(), 0)
        }
        builder.setSpan(span, offset, offset + text.length(), 0)
    }
}