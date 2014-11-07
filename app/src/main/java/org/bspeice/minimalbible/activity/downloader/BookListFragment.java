package org.bspeice.minimalbible.activity.downloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseFragment;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookComparators;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemSelected;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * A placeholder fragment containing a simple view.
 */

public class BookListFragment extends BaseFragment {
    protected static final String ARG_BOOK_CATEGORY = "book_category";

    @Inject
    protected DownloadPrefs downloadPrefs;
    protected ProgressDialog refreshDialog;
    @Inject RefreshManager refreshManager;
    @Inject
    List<String> availableLanguages;

    @InjectView(R.id.lst_download_available)
    ListView downloadsAvailable;
    @InjectView(R.id.spn_available_languages)
    Spinner languagesSpinner;

    private LayoutInflater inflater;

    /**
     * Returns a new instance of this fragment for the given section number.
     * TODO: Switch to AutoFactory/@Provides rather than inline creation.
     */
    public static BookListFragment newInstance(BookCategory c) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_CATEGORY, c.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        ((Injector)getActivity()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_download, container,
                false);
        ButterKnife.inject(this, rootView);
        displayModules();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DownloadActivity) activity).onSectionAttached(getArguments()
                .getString(ARG_BOOK_CATEGORY));
    }

    /**
     * Trigger the functionality to display a list of modules. Prompts user if downloading
     * from the internet is allowable.
     */
 	protected void displayModules() {
		boolean dialogDisplayed = downloadPrefs.hasShownDownloadDialog();
		
		if (!dialogDisplayed) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			DownloadDialogListener dialogListener = new DownloadDialogListener();
			builder.setMessage(
					"About to contact servers to download content. Continue?")
					.setPositiveButton("Yes", dialogListener)
					.setNegativeButton("No", dialogListener)
					.setCancelable(false).show();
		} else {
			refreshModules();
		}
	}

    /**
     * Do the work of refreshing modules (download manager handles using cached copy vs. actual
     * refresh), and then displaying them when ready.
     */
	private void refreshModules() {
        // Check if the downloadManager has already refreshed everything
        if (!refreshManager.getRefreshComplete().get()) {
            // downloadManager is in progress of refreshing
            refreshDialog = new ProgressDialog(getActivity());
            refreshDialog.setMessage("Refreshing available modules...");
            refreshDialog.setCancelable(false);
            refreshDialog.show();
        }

        languagesSpinner.setAdapter(getLocaleSpinner());
        if (BookListFragment.this.getActivity() != null) {
            // On a screen rotate, getActivity() will be null. But, the activity
            // will already have been set up correctly, so we don't need to worry
            // about it.
            // If not null, we need to set it up now.
            setInsetsSpinner(BookListFragment.this.getActivity(), languagesSpinner);
        }
        if (refreshDialog != null) {
            refreshDialog.cancel();
        }
    }

    @SuppressWarnings("ConstantConditions")
        // getAvailableLanguagesList() will not return null
    SpinnerAdapter getLocaleSpinner() {
        ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this.getActivity(),
                android.R.layout.simple_spinner_item,
                availableLanguages.toArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @SuppressWarnings("unused")
    @OnItemSelected(R.id.spn_available_languages)
    public void onClick(final int position) {
        refreshManager.getFlatModules()
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return book.getBookCategory() ==
                                BookCategory.fromString(BookListFragment.this.getArguments()
                                        .getString(ARG_BOOK_CATEGORY));
                    }
                })
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return book.getLanguage().getName()
                                .equals(availableLanguages.get(position));
                    }
                })
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

    private class DownloadDialogListener implements
            DialogInterface.OnClickListener {
        @Override
        public void onClick(@NotNull DialogInterface dialog, int which) {
            downloadPrefs.hasShownDownloadDialog(true);

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    // Clicked ready to continue - allow downloading in the future
                    downloadPrefs.hasEnabledDownload(true);

                    // And warn them that it has been enabled in the future.
                    Toast.makeText(getActivity(),
                            "Downloading now enabled. Disable in settings.",
                            Toast.LENGTH_SHORT).show();
                    refreshModules();
                    break;

                // case DialogInterface.BUTTON_NEGATIVE:
                default:
                    // Clicked to not download - Permanently disable downloading
                    downloadPrefs.hasEnabledDownload(false);
                    Toast.makeText(getActivity(),
                            "Disabling downloading. Re-enable it in settings.",
                            Toast.LENGTH_SHORT).show();
                    refreshModules();
                    break;
            }
        }
    }

}