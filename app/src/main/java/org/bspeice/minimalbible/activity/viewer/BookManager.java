package org.bspeice.minimalbible.activity.viewer;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by bspeice on 6/18/14.
 */
@Singleton
public class BookManager {

    private Observable<Book> installedBooks;
    private Boolean refreshComplete;

    @Inject
    BookManager() {
        installedBooks = Observable.from(Books.installed().getBooks())
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
