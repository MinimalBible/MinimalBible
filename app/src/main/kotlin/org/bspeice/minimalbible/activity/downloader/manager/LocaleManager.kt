package org.bspeice.minimalbible.activity.downloader.manager

import org.crosswire.common.util.Language

class LocaleManager(val rM: RefreshManager) {

    val currentLanguage = Language.DEFAULT_LANG.getName()
    val languageModuleMap = rM.flatModules
            .map { it.getLanguage() ?: Language(Language.UNKNOWN_LANG_CODE) }
            .groupBy { it.getName() }

    val availableLanguages = languageModuleMap.map { it.getKey() }
    val availableLanguagesList = availableLanguages.toSortedList {(left, right) ->
        // Prioritize our current language first
        if (left == currentLanguage)
            -1
        else if (right == currentLanguage)
            1
        else
            left.compareTo(right)
    }.toBlocking().first()
}