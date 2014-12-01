package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import org.bspeice.minimalbible.service.format.osisparser.VerseContent
import android.text.style.RelativeSizeSpan

/**
 * Created by bspeice on 12/1/14.
 */
class DivineHandler() : TagHandler {
    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String) {
        this buildDivineName chars forEach { it apply builder }
    }

    fun buildDivineName(chars: String) =
            listOf(AppendArgs(chars take 1, null),
                    AppendArgs(chars drop 1, RelativeSizeSpan(.9f))
            )
}