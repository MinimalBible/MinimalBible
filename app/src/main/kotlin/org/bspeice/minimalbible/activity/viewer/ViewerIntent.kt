package org.bspeice.minimalbible.activity.viewer

import android.content.Context
import android.content.Intent
import org.crosswire.jsword.passage.Verse

/**
 * Created by bspeice on 4/2/15.
 */
class ViewerIntent() {

    companion object {
        val VERSE_RESULT_KEY = "VERSE_RESULT"

        fun fromSearchResult(ctx: Context, verse: Verse): Intent {
            val i = Intent(ctx, javaClass<BibleViewer>())
            i.putExtra(VERSE_RESULT_KEY, verse.getOrdinal())
            return i
        }

        /**
         * Attempt to get the result of a search out of the intent
         * Returns -1 if no search result was found
         */
        fun decodeSearchResult(i: Intent) = i.getIntExtra(VERSE_RESULT_KEY, -1)
    }
}
