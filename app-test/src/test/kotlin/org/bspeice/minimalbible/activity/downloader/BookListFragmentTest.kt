package org.bspeice.minimalbible.activity.downloader

import org.jetbrains.spek.api.Spek
import kotlin.test.assertTrue
import android.content.DialogInterface

/**
 * Created by bspeice on 11/22/14.
 */

class BookListFragmentSpek : Spek() {{

    given("A BookListFragment with showDialog() mocked out") {
        val fragment = object : BookListFragment() {
            var condition = false
            override fun showDialog() {
                condition = true
            }
        }

        on("attempting to display modules with the dialog not shown already") {
            fragment.displayModules(false)

            it("should show the download dialog") {
                assertTrue(fragment.condition)
            }
        }
    }

    given("a BookListFragment with displayLanguageSpinner() mocked out") {
        val fragment = object : BookListFragment() {
            var condition = false

            override fun displayLanguageSpinner() {
                condition = true
            }
        }

        on("attempting to display modules with the dialog already shown") {
            fragment.displayModules(true)

            it("should show the available languages spinner") {
                assertTrue(fragment.condition)
            }
        }
    }

    given("a DownloadDialogListener with with buttonPositive() mocked out") {
        val listener = object : BookListFragment.DownloadDialogListener(null, null) {
            var condition = false
            override fun buttonPositive() {
                condition = true
            }
        }

        on("handling a positive button press") {
            listener.handleButton(DialogInterface.BUTTON_POSITIVE)

            it("should call the proper handler") {
                assertTrue(listener.condition)
            }
        }
    }

    given("A DownloadDialogListener with buttonNegative() mocked out") {
        val listener = object : BookListFragment.DownloadDialogListener(null, null) {
            var condition = false
            override fun buttonNegative() {
                condition = true
            }
        }

        on("handling a negative button press") {
            listener.handleButton(DialogInterface.BUTTON_NEGATIVE)

            it("should call the proper handler") {
                assertTrue(listener.condition)
            }
        }
    }
}
}