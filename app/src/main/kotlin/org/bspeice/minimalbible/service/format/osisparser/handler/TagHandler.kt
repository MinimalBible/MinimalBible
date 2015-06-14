package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import org.xml.sax.Attributes

interface TagHandler {
    fun start(attrs: Attributes, info: VerseContent, builder: SpannableStringBuilder,
              state: ParseState): ParseState

    fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String,
               state: ParseState): ParseState

    fun end(info: VerseContent, builder: SpannableStringBuilder,
            state: ParseState): ParseState
}

class ParseState(val spans: List<AppendArgs>) {

    fun build(builder: SpannableStringBuilder): ParseState {
        spans.forEach { it apply builder }
        return ParseState(listOf())
    }

    fun append(arg: AppendArgs) = ParseState(spans + arg)

    fun append(args: List<AppendArgs>) = ParseState(spans + args)
}

data class AppendArgs(val text: String, val span: Any? = null) {
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