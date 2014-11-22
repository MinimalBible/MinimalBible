package org.bspeice.minimalbible.activity.downloader

import org.jetbrains.spek.api.Spek
import kotlin.test.assertTrue

/**
 * Created by bspeice on 11/22/14.
 */

class BookListFragmentSpek : Spek() {{

    given("A BookListFragment with showDialog() mocked out") {
        class TestableFragment : BookListFragment() {
            var condition = false

            override fun showDialog() {
                condition = true
            }
        }

        val fragment = TestableFragment()

        on("attempting to display modules with the dialog not shown already") {
            fragment.displayModules(false)

            it("should show the download dialog") {
                assertTrue(fragment.condition)
            }
        }
    }

    given("a BookListFragment with displayLanguageSpinner() mocked out") {
        class TestableFragment : BookListFragment() {
            var condition = false

            override fun displayLanguageSpinner() {
                condition = true
            }
        }

        val fragment = TestableFragment()

        on("attempting to display modules with the dialog already shown") {
            fragment.displayModules(true)

            it("should show the available languages spinner") {
                assertTrue(fragment.condition)
            }
        }
    }
}
}