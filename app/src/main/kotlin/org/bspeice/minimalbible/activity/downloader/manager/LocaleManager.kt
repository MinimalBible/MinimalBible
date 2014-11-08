package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.common.util.Language
import rx.Observable

class LocaleManager(val rM: RefreshManager) {

    val currentLanguage = Language.DEFAULT_LANG

    val languageModuleMap = rM.flatModules
            // Language doesn't have hashCode(), so we actually group by its String
            .groupBy { FixedLanguage(it.getLanguage()) }

    // Cast back to the original Language implementation
    val availableLanguages: Observable<Language> = languageModuleMap.map { it.getKey() }
    val sortedLanguagesList =
            availableLanguages.toSortedList {(left, right) ->
                // Prioritize our current language first
                if (left.getName() == currentLanguage.getName())
                    -1
                else if (right.getName() == currentLanguage.getName())
                    1
                else
                    left.getName().compareTo(right.getName())
            }.toBlocking().first()
}

class FixedLanguage(language: Language?) :
        Language(language?.getCode() ?: Language.UNKNOWN_LANG_CODE) {
    override fun hashCode() = this.getName().hashCode()
}