package org.bspeice.minimalbible.service.format.osisparser.handler

import android.text.SpannableStringBuilder
import com.orhanobut.logger.Logger
import org.bspeice.minimalbible.service.format.osisparser.VerseContent

/**
 * Created by bspeice on 12/1/14.
 */
class UnknownHandler(val tagName: String) : TagHandler {
    override fun render(builder: SpannableStringBuilder, info: VerseContent, chars: String) {
        Logger.w("Unknown tag '$tagName' received text: '$chars'")
    }
}