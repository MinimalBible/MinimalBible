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
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseFragment;
import org.crosswire.jsword.book.Book;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookFragment extends BaseFragment {

    @Inject BookManager bookManager;

    @InjectView(R.id.book_content)
    WebView mainContent;

    private static final String ARG_BOOK_NAME = "book_name";

    private Book mBook;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static BookFragment newInstance(String bookName, Injector injector) {
        BookFragment fragment = new BookFragment();
        injector.inject(fragment);
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
        ((Injector)getActivity()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewer_main, container,
                false);
        ButterKnife.inject(this, rootView);
        mainContent.getSettings().setJavaScriptEnabled(true);

        // TODO: Load initial text from SharedPreferences

        // And due to Observable async, we can kick off fetching the actual book asynchronously!
        bookManager.getInstalledBooks()
                .first(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        String mBookName = getArguments().getString(ARG_BOOK_NAME);
                        return book.getName().equals(mBookName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        BookFragment.this.mBook = book;
                        displayBook(book);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("BookFragment", "No books installed?");
                    }
                });

        return rootView;
    }

    // TODO: Remove?
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void displayBook(Book b) {
        Log.d("BookFragment", b.getName());
        ((BibleViewer)getActivity()).setActionBarTitle(b.getInitials());
        mainContent.loadUrl(getString(R.string.content_page));
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
