package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.common.util.Language
import rx.Observable
import rx.observables.GroupedObservable

class LocaleManager(val rM: RefreshManager) {

    val currentLanguage = Language.DEFAULT_LANG

    private val languageModuleMap = rM.flatModules
            // Language doesn't have hashCode(), so we actually group by its String
            .groupBy { FixedLanguage(it.getLanguage()) }

    // I would suppress the warning here if I could figure out how...
    val modulesByLanguage = languageModuleMap
            .map { GroupedObservable.from(it.getKey(): Language, it) }

    // Cast back to the original Language implementation
    val availableLanguages: Observable<Language> = languageModuleMap.map { it.getKey() }
    val sortedLanguagesList =
            Core.sortedLanguagesList(availableLanguages, currentLanguage).toBlocking().first()

    object Core {
        fun sortedLanguagesList(availableLanguages: Observable<Language>,
                                currentLanguage: Language) =
            availableLanguages.toSortedList {(left, right) ->
                // Prioritize our current language first
                if (left.getName() == currentLanguage.getName())
                    -1
                else if (right.getName() == currentLanguage.getName())
                    1
                else
                    left.getName() compareTo right.getName()
            }
    }

    // TODO: Fix the actual Language implementation - Pull Request?
    // Can't use a data class because we need to get the name of the language
    private class FixedLanguage(language: Language?) :
            Language(language?.getCode() ?: Language.UNKNOWN_LANG_CODE) {
        override fun hashCode() = this.getName().hashCode()
    }
}