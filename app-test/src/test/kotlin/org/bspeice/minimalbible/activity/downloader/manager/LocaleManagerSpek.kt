package org.bspeice.minimalbible.activity.downloader.manager

import org.jetbrains.spek.api.Spek
import org.crosswire.common.util.Language

/**
 * Created by bspeice on 12/14/14.
 */

class LocaleManagerSpek() : Spek() {{

    given("some example language objects") {
        val english = Language("en")
        val russian = Language("ru")
        val french = Language("fr");

        on("sorting between english and russian with current as english") {
            val result = LocaleManager.compareLanguages(english, russian, english)

            it("should prioritize english") {
                assert(result < 0)
            }
        }

        on("sorting between russian and english with current as english") {
            val result = LocaleManager.compareLanguages(russian, english, english)

            it("should prioritize english") {
                assert(result > 0)
            }
        }

        on("sorting between russian and english with current as french") {
            val result = LocaleManager.compareLanguages(russian, english, french)

            it("should inform us that russian is greater") {
                assert(result > 0)
            }
        }

        on("sorting between english and russian with current as french") {
            val result = LocaleManager.compareLanguages(english, russian, french)

            it("should inform us that english is lesser") {
                assert(result < 0)
            }
        }

        on("comparing the same languages with current language as the language being compared") {
            val result = LocaleManager.compareLanguages(english, english, english)

            it("should report that the languages are duplicate") {
                assert(result == 0)
            }
        }

        on("comparing the same languages with current language as something different") {
            val result = LocaleManager.compareLanguages(english, english, russian)

            it("should report that the languages are duplicate") {
                assert(result == 0)
            }
        }
    }
}
}