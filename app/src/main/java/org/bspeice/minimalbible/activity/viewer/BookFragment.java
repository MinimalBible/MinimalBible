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
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.versification.Versification;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static org.crosswire.jsword.versification.system.Versifications.instance;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookFragment extends BaseFragment {
    @Inject @Named("MainBook") Book mBook;

    @InjectView(R.id.book_content)
    WebView mainContent;

    private static final String ARG_BOOK_NAME = "book_name";

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static BookFragment newInstance(String bookName) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_NAME, bookName);
        fragment.setArguments(args);
        return fragment;
    }

    public BookFragment() {
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
        ((Injector)getActivity()).inject(this);
        ButterKnife.inject(this, rootView);
        mainContent.getSettings().setJavaScriptEnabled(true);

        // TODO: Load initial text from SharedPreferences

        displayBook(mBook);

        Log.d("BookFragment", getVersification(mBook).toString());

        return rootView;
    }

    private Versification getVersification(Book b) {
        return instance().getVersification((String) b.getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION));
    }

    // TODO: Remove?
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void displayBook(Book b) {
        Log.d("BookFragment", b.getName());
        ((BibleViewer)getActivity()).setActionBarTitle(b.getInitials());
        mainContent.loadUrl(getString(R.string.book_html));
        mainContent.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                invokeJavascript("set_content", BookFragment.this.mBook.getName());
            }
        });

    }

    private void invokeJavascript(String function, Object arg) {
        mainContent.loadUrl("javascript:" + function + "('" + arg.toString() + "')");
    }

    private void invokeJavascript(String function, List<Object> args) {
        mainContent.loadUrl("javascript:" + function + "(" + joinString(",", args.toArray()) + ")");
    }

    // Convenience from http://stackoverflow.com/a/17795110/1454178
    public static String joinString(String join, Object... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        } else if (strings.length == 1) {
            return strings[0].toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strings[0]);
            for (int i = 1; i < strings.length; i++) {
                sb.append(join).append(strings[i].toString());
            }
            return sb.toString();
        }
    }
}
