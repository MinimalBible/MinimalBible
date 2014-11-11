package org.bspeice.minimalbible.test.activity.downloader.manager;

import org.bspeice.minimalbible.MBTestCase;
import org.bspeice.minimalbible.activity.downloader.manager.LocaleManager;
import org.crosswire.common.util.Language;

import java.util.List;

import rx.Observable;

/**
 * Test cases for the Locale Manager
 */
public class LocaleManagerTest extends MBTestCase {

    public void testSortedLanguagesList() {
        Language english = new Language("en");
        Language russian = new Language("ru");
        Language french = new Language("fr");
        Language german = new Language("de");
        Language hebrew = new Language("he");
        Language afrikaans = new Language("af");

        Observable<Language> languages = Observable.just(english, russian, french,
                german, hebrew, afrikaans);

        LocaleManager.Core core = LocaleManager.Core.INSTANCE$;

        //noinspection ConstantConditions
        List<Language> sortedLanguages = core.sortedLanguagesList(languages, english)
                .toBlocking().first();

        // First language should be the 'current' (note this is an identity compare)
        assertTrue(sortedLanguages.get(0) == english);
        // Second language should be 'less than' third
        assertTrue(sortedLanguages.toString(),
                sortedLanguages.get(1).toString().compareTo(
                        sortedLanguages.get(2).toString()) < 0);
        // Fifth language should be greater than the fourth
        assertTrue(sortedLanguages.toString(), sortedLanguages.get(4).toString().compareTo(
                sortedLanguages.get(3).toString()) > 0);
    }
}
