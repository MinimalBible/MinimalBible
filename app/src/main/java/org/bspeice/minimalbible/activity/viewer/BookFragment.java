package org.bspeice.minimalbible.activity.viewer;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseFragment;
import org.bspeice.minimalbible.service.book.VerseLookupService;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Lazy;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookFragment extends BaseFragment {

    private static final String ARG_BOOK_NAME = "book_name";

    Injector i;
    @Inject
    @Named("MainBook")
    Lazy<Book> mBook;

    @InjectView(R.id.book_content)
    WebView mainContent;

    PublishSubject<String> titleReceiver = PublishSubject.create();

    public BookFragment() {
        // We can't initialize the lookupService here since the fragment hasn't been tied
        // to the parent activity yet.
    }

    /**
     * Returns a new instance of this fragment for the given book.
     */
    public static BookFragment newInstance(String bookName) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_NAME, bookName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewer_main, container,
                false);
        this.i = (Injector) getActivity();
        i.inject(this);
        ButterKnife.inject(this, rootView);
        mainContent.getSettings().setJavaScriptEnabled(true);

        // TODO: Load initial text from SharedPreferences, rather than getting the actual book.

        displayBook(mBook.get());

        return rootView;
    }

    // TODO: Remove?
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /*----------------------------------------
        Here be all the methods you want to spend time with
      ----------------------------------------
     */

    /**
     * Do the initial work of displaying a book. Requires setting up WebView, etc.
     * TODO: Get initial content from cache?
     *
     * @param b The book we want to display
     */
    private void displayBook(Book b) {
        Log.d("BookFragment", b.getName());
        ((BibleViewer)getActivity()).setActionBarTitle(b.getInitials());
        mainContent.loadUrl(getString(R.string.book_html));

        VerseLookupService lookupService = new VerseLookupService(i, mBook.get());
        BibleViewClient client = new BibleViewClient(b, lookupService, titleReceiver);
        titleReceiver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ((BibleViewer) getActivity()).setActionBarTitle(s);
                        Log.d("BibleViewClient", s);
                    }
                });
        mainContent.setWebViewClient(client);
        mainContent.addJavascriptInterface(client, "Android");

        // TODO: Remove remote debugging when ready - or should this be removed?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }
}
