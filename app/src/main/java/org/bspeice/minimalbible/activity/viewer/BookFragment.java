package org.bspeice.minimalbible.activity.viewer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseFragment;
import org.bspeice.minimalbible.activity.viewer.bookutil.VersificationUtil;
import org.bspeice.minimalbible.service.book.VerseLookupService;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleBook;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Lazy;

import static org.bspeice.minimalbible.util.StringUtil.joinString;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookFragment extends BaseFragment {

    private static final String ARG_BOOK_NAME = "book_name";
    @Inject
    @Named("MainBook")
    Lazy<Book> mBook;
    @Inject
    VersificationUtil vUtil;
    // TODO: Factory?
    VerseLookupService lookupService;
    @InjectView(R.id.book_content)
    WebView mainContent;

    public BookFragment() {
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
        Injector i = (Injector) getActivity();
        i.inject(this);
        // TODO: Defer lookup until after webview created? When exactly is WebView created?
        this.lookupService = new VerseLookupService(i, mBook.get());
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
        mainContent.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO: Restore this verse from a SharedPref
                Verse initial = new Verse(vUtil.getVersification(mBook.get()),
                        BibleBook.GEN, 1, 1);
                super.onPageFinished(view, url);
                invokeJavascript("set_content", lookupService.getHTMLVerse(initial));
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(this.getClass().getSimpleName(), "Code: " + errorCode + " " +
                    description);
            }
        });
    }

    /**
     * Do the heavy lifting of getting the actual text for a verse
     *
     * @param v The verse to display
     */
    @SuppressWarnings("unused")
    public void displayVerse(Verse v) {
        Book b = mBook.get();
        lookupService.getHTMLVerse(v);
    }

    /*-----------------------------------------
        Here be the methods you wish didn't have to exist.
      -----------------------------------------
     */

    private void invokeJavascript(String function, Object arg) {
        mainContent.loadUrl("javascript:" + function + "('" + arg.toString() + "')");
    }

    @SuppressWarnings("unused")
    private void invokeJavascript(String function, List<Object> args) {
        mainContent.loadUrl("javascript:" + function + "(" + joinString(",", args.toArray()) + ")");
    }
}
