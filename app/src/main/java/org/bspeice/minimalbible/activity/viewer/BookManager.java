package org.bspeice.minimalbible.activity.viewer;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bspeice on 6/18/14.
 */
@Singleton
public class BookManager {

    private Observable<Book> installedBooks;
    private Boolean refreshComplete;

    // Some of these books seem to think they're installed...
    private List<String> excludeBooks = new ArrayList<String>() {{
        add("ERen_no");
        add("ot1nt2");
    }};

    @Inject
    BookManager() {
        // TODO:  Any way this can be sped up goes straight to the initialization time.
        installedBooks = Observable.from(Books.installed().getBooks())
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        return !excludeBooks.contains(book.getInitials());
                    }
                })
            .cache();
        installedBooks.subscribeOn(Schedulers.io())
                .subscribe(new Action1<Book>() {
            @Override
            public void call(Book book) {}
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {}
        }, new Action0() {
            @Override
            public void call() {
                BookManager.this.refreshComplete = true;
            }
        });
    }

    public Observable<Book> getInstalledBooks() {
        return installedBooks;
    }

    public Boolean isRefreshComplete() {
        return refreshComplete;
    }
}
