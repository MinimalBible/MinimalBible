package org.bspeice.minimalbible.activity.downloader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseFragment;
import org.bspeice.minimalbible.activity.downloader.injection.ActivityContextModule;
import org.bspeice.minimalbible.activity.downloader.injection.BookListFragmentComponent;
import org.bspeice.minimalbible.activity.downloader.injection.DaggerBookListFragmentComponent;
import org.bspeice.minimalbible.activity.downloader.manager.LocaleManager;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.common.util.Language;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookComparators;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemSelected;
import dagger.Lazy;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * A fragment to list out the books available for downloading.
 * Each fragment will be responsible for a single category,
 * another fragment will be created if a second category is needed.
 */

public class BookListFragment extends BaseFragment {
    protected static final String ARG_BOOK_CATEGORY = "book_category";
    protected BookCategory bookCategory;

    @Inject
    DownloadPrefs downloadPrefs;
    @Inject
    Lazy<RefreshManager> refreshManager;
    @Inject
    LocaleManager localeManager;

    @InjectView(R.id.lst_download_available)
    ListView downloadsAvailable;
    @InjectView(R.id.spn_available_languages)
    Spinner languagesSpinner;

    LayoutInflater inflater;

    // A cache of the languages currently available for this category
    List<Language> availableLanguages;

    /**
     * Returns a new instance of this fragment for the given section number.
     * TODO: Switch to AutoFactory/@Provides rather than inline creation.
     */
    public static BookListFragment newInstance(BookCategory c, DownloadActivity activity) {
        BookListFragment fragment = new BookListFragment();
        DaggerBookListFragmentComponent.builder()
                .activityContextModule(new ActivityContextModule(activity))
                .build()
                .injectBookListFragment(fragment);
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_CATEGORY, c.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        BookListFragmentComponent component = DaggerBookListFragmentComponent.builder()
                .activityContextModule(new ActivityContextModule((DownloadActivity) getActivity()))
                .build();
        component.injectBookListFragment(this);

        bookCategory = BookCategory.fromString(getArguments().getString(ARG_BOOK_CATEGORY));
        availableLanguages = localeManager.sortedLanguagesForCategory(bookCategory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        In order to get the spinner arrow button to have the right color, we need to modify
        android:textColorSecondary of the parent theme. For whatever reason, applying this
        modification in the XML doesn't work. However, I don't want to have a different theme for
        the entire activity, so what the below accomplishes is the same as specifying
        android:theme in the AndroidManifest for an activity.
        So if you ever change the below code: PLEASE, make sure the language selector colors
        are correct.
         */
        Context ctx = new ContextThemeWrapper(getActivity(), R.style.MinimalBible_DownloadList);
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = this.inflater.inflate(R.layout.fragment_download, container,
                false);

        ButterKnife.inject(this, rootView);
        displayModules();

        return rootView;
    }

    void displayModules() {
        displayModules(downloadPrefs.hasShownDownloadDialog());
    }

    /**
     * Trigger the functionality to display a list of modules. Prompts user if downloading
     * from the internet is allowable.
     */
    void displayModules(boolean dialogDisplayed) {
        if (!dialogDisplayed) {
            showDialog();
        } else {
            displayLanguageSpinner();
        }
	}

    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DownloadDialogListener dialogListener = new DownloadDialogListener(this, downloadPrefs);
        builder.setMessage(
                "About to contact servers to download content. Continue?")
                .setPositiveButton("Yes", dialogListener)
                .setNegativeButton("No", dialogListener)
                .setCancelable(false).show();
    }

    void displayLanguageSpinner() {
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                availableLanguages.toArray()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languagesSpinner.setAdapter(adapter);

        // Use Object to avoid the incredibly long type parameters
        refreshManager.get().getAvailableModules()
                .subscribe(new Action1<Object>() {
                               @Override
                               public void call(Object o) {
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(getActivity(),
                                        getString(R.string.error_book_refresh),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
    }

    @SuppressWarnings("unused")
    @OnItemSelected(R.id.spn_available_languages)
    public void onClick(final int position) {
        booksByLanguage(refreshManager.get().getFlatModules(),
                availableLanguages.get(position),
                bookCategory)
                // Repack all the books
                .toSortedList(new Func2<Book, Book, Integer>() {
                    @Override
                    public Integer call(Book book1, Book book2) {
                        return BookComparators.getInitialComparator().compare(book1, book2);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Book>>() {
                    @Override
                    public void call(List<Book> books) {
                        downloadsAvailable.setAdapter(
                                new BookListAdapter(inflater, books,
                                        (DownloadActivity) getActivity()));
                    }
                });
    }

    // TODO: Refactor out, this information should come from LocaleManager
    protected Observable<Book> booksByLanguage(Observable<Book> books, final Language language,
                                               final BookCategory category) {
        return books
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return book.getBookCategory() == category;
                    }
                })
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return book.getLanguage() != null;
                    }
                })
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        // Language doesn't properly implement .equals(), so use Strings
                        return book.getLanguage().getCode()
                                .equals(language.getCode());
                    }
                });
    }

    static class DownloadDialogListener implements
            DialogInterface.OnClickListener {
        BookListFragment fragment;
        DownloadPrefs downloadPrefs;

        DownloadDialogListener(BookListFragment fragment, DownloadPrefs downloadPrefs) {
            this.fragment = fragment;
            this.downloadPrefs = downloadPrefs;
        }

        @Override
        public void onClick(@NotNull DialogInterface dialog, int which) {
            downloadPrefs.hasShownDownloadDialog(true);
            handleButton(which);
        }

        void handleButton(int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    buttonPositive();
                    break;

                // case DialogInterface.BUTTON_NEGATIVE:
                default:
                    buttonNegative();
                    break;
            }
        }

        void buttonPositive() {
            // Clicked ready to continue - allow downloading in the future
            downloadPrefs.hasEnabledDownload(true);

            // And warn them that it has been enabled in the future.
            // TODO: Migrate to strings.xml
            showToast("Downloading now enabled. Disable in settings");
            fragment.displayModules();
        }

        void buttonNegative() {
            // Clicked to not download - Permanently disable downloading
            downloadPrefs.hasEnabledDownload(false);
            // TODO: Migrate to strings.xml
            showToast("Disabling downloading. Re-enable it in settings.");
            fragment.getActivity().finish();
        }

        void showToast(String text) {
            Toast.makeText(fragment.getActivity(), text, Toast.LENGTH_SHORT).show();
        }
    }
}