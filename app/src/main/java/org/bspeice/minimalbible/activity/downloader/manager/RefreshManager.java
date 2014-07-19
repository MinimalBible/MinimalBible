package org.bspeice.minimalbible.activity.downloader.manager;

import android.content.Context;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBible;
import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Handle refreshing the list of books available as needed
 * Note that we don't refactor this class out since we need somewhere
 * to track whether the refresh is done.
 */
@Singleton
public class RefreshManager {

    /**
     * Cached copy of modules that are available so we don't refresh for everyone who requests it.
     */
    private Observable<Map<Installer, List<Book>>> availableModules;
    private final AtomicBoolean refreshComplete = new AtomicBoolean();

    @Inject
    Collection<Installer> installers;

    @Inject
    public RefreshManager(Injector injector) {
        injector.inject(this);
        refreshModules();
    }

    /**
     * Do the work of kicking off the AsyncTask to refresh books, and make sure we know
     * when it's done.
     * NOTE: This code assigns its own thread. This is because we are called privately, and
     * don't want to expose this method. I don't like hiding the side effects like this, but
     * in this case I'm making an exception.
     */
    private Observable<Map<Installer, List<Book>>> refreshModules() {
        if (availableModules == null) {
            availableModules = Observable.from(installers)
                    .map(new Func1<Installer, Map<Installer, List<Book>>>() {
                        @Override
                        public Map<Installer, List<Book>> call(Installer installer) {
                            Map<Installer, List<Book>> map = new HashMap<Installer, List<Book>>();
                            map.put(installer, installer.getBooks());
                            return map;
                        }
                    }).subscribeOn(Schedulers.io())
                    .cache();

            // Set refresh complete when it is.
            availableModules.observeOn(Schedulers.io())
                    .subscribe(new Action1<Map<Installer, List<Book>>>() {
                        @Override
                        public void call(Map<Installer, List<Book>> onNext) {}
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable onError) {}
                    }, new Action0() {
                        @Override
                        public void call() {
                            refreshComplete.set(true);
                        }
                    });
        }
        return availableModules;
    }

    public Observable<Book> getAvailableModulesFlattened() {
        return availableModules
                // First flatten the Map to its lists
                .flatMap(new Func1<Map<Installer, List<Book>>, Observable<? extends List<Book>>>() {
                    @Override
                    public Observable<? extends List<Book>> call(Map<Installer, List<Book>> books) {
                        return Observable.from(books.values());
                    }
                })
                // Then flatten the lists
                .flatMap(new Func1<List<Book>, Observable<? extends Book>>() {
                    @Override
                    public Observable<? extends Book> call(List<Book> t1) {
                        return Observable.from(t1);
                    }
                });
    }


    /**
     * Find the installer that a Book comes from.
     * @param b The book to search for
     * @return The Installer that should be used for this book.
     */
    public Installer installerFromBook(final Book b) {
        Map<Installer, List<Book>> element = availableModules
            .filter(new Func1<Map<Installer, List<Book>>, Boolean>() {
                @Override
                public Boolean call(Map<Installer, List<Book>> installerListMap) {
                for (List<Book> element : installerListMap.values()) {
                    if (element.contains(b)) {
                        return true;
                    }
                }
                return false;
                }
            })
            .toBlocking()
            .first();
        return element.entrySet().iterator().next().getKey();
    }

    public boolean isRefreshComplete() {
        return refreshComplete.get();
    }
}
