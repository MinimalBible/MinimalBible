package org.bspeice.minimalbible.test.activity.downloader.manager;

import junit.framework.TestCase;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.activity.downloader.manager.InstalledManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class InstalledManagerTest extends TestCase implements Injector {
    ObjectGraph mObjectGraph;

    @Module(injects = {InstalledManager.class,
            InstalledManagerTest.class})
    static class IMTestModules {
        Injector i;
        public IMTestModules(Injector i) {
            this.i = i;
        }

        @Provides @Singleton
        Injector provideInjector() {
            return this.i;
        }

        @Provides @Singleton
        Books provideInstalledBooks() {
            return Books.installed();
        }

        @Provides
        List<Book> provideInstalledBooksList(Books b) {
            return b.getBooks();
        }
    }

    @Inject Books installedBooks;
    @Inject InstalledManager iM;

    @Override
    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public void setUp() throws Exception {
        super.setUp();
        mObjectGraph = ObjectGraph.create(new IMTestModules(this));
        mObjectGraph.inject(this);

        //TODO: Guarantee that a book is installed.
    }

    Observable<Book> getInstalledBooks() {
        return Observable.from(installedBooks.getBooks())
                .filter(new Func1<Book, Boolean>() {
                    @Override
                    public Boolean call(Book book) {
                        // Double check that the book is actually installed
                        return book.getDriver().isDeletable(book);
                    }
                });
    }

    public void testIsInstalled() throws Exception {
        final AtomicBoolean foundMismatch = new AtomicBoolean(false);
        getInstalledBooks()
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        if (!iM.isInstalled(book)) {
                            foundMismatch.set(true);
                        }
                    }
                });
        assertFalse(foundMismatch.get());
    }

    /*
    public void testRemoveBook() throws Exception {
    }
    */
}