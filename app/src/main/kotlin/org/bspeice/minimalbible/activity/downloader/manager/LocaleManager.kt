package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.common.util.Language
import rx.Observable
import org.crosswire.jsword.book.BookCategory
import kotlin.platform.platformStatic

/**
 * Took me a significant amount of time, but this is an implementation I can live with.
 * An ideal solution would be able to group by the category first, then language, with all
 * modules underneath, so something like Map<BookCategory, Map<Language, List<Book>>>.
 * That said, trying to build said map is a bit ridiculous. The way I wrote it requires
 * using functions instead of cached values, but I'll get over it.
 */
class LocaleManager(val rM: RefreshManager) {

    val currentLanguage = Language.DEFAULT_LANG

    // Get all modules grouped by language first
    val modulesByCategory = rM.flatModules.groupBy { it.getBookCategory() }

    fun languagesForCategory(cat: BookCategory): Observable<Language> = modulesByCategory
            // Then filter according to the requested language
            .filter { it.getKey() == cat }
            // Then map the GroupedObservable Book element to its actual language
            .flatMap { it.map { it.getLanguage() } }
            // Making sure to discard anything with a null language
            .filter { it != null }
            // And remove duplicates. The flatMap above means that we will have one entry
            // for each book, so we need to remove duplicate entries of
            // languages with more than one book to them
            .distinct()

    fun sortedLanguagesForCategory(cat: BookCategory): List<Language> =
            languagesForCategory(cat)
                    // Finally, sort all languages, prioritizing the current
                    .toSortedList { left, right -> compareLanguages(left, right, currentLanguage) }
                    // And flatten this into the actual List needed
                    .toBlocking().first()

    class object {
        platformStatic
        fun compareLanguages(left: Language, right: Language, current: Language) =
                if (left == right)
                    0
                else if (left.getName() == current.getName())
                    -1
                else if (right.getName() == current.getName())
                    1
                else
                    left.getName() compareTo right.getName()
    }
}