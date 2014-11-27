package org.bspeice.minimalbible.activity.viewer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.BaseFragment;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.subjects.PublishSubject;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookFragment extends BaseFragment {

    private static final String ARG_BOOK_NAME = "book_name";

    Injector i;
    @Inject
    @Named("MainBook")
    Book mBook;
    @Inject
    PublishSubject<BookScrollEvent> scrollEventProvider;

    @InjectView(R.id.book_content)
    RecyclerView bookContent;

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

        // TODO: Load initial text from SharedPreferences, rather than getting the actual book.
        displayBook(mBook);

        return rootView;
    }

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

        final RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        BookAdapter adapter = new BookAdapter(b);
        bookContent.setLayoutManager(manager);
        bookContent.setAdapter(adapter);

        adapter.bindScrollHandler(scrollEventProvider, manager);
    }
}
