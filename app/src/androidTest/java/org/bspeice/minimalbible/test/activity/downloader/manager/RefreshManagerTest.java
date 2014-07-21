package org.bspeice.minimalbible.test.activity.downloader.manager;

import junit.framework.TestCase;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.Installer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import rx.functions.Action1;

import static org.mockito.Mockito.*;

public class RefreshManagerTest extends TestCase implements Injector {

    /**
     * The object graph that should be given to classes under test. Each test is responsible
     * for setting their own ObjectGraph.
     */
    ObjectGraph mObjectGraph;

    @Override
    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    @Inject RefreshManager rM;

    @Module (injects = {RefreshManagerTest.class, RefreshManager.class})
    class TGAMFModules {
        Injector i;
        Collection<Installer> installers;

        TGAMFModules(Injector i, Collection<Installer> installers) {
            this.i = i;
            this.installers = installers;
        }

        @Provides @Singleton
        Injector provideInjector() {
            return i;
        }

        @Provides @Singleton
        Collection<Installer> provideInstallers() {
            return this.installers;
        }
    }
    public void testGetAvailableModulesFlattened() throws Exception {
        // Environment setup
        final String mockBookName = "MockBook";

        Book mockBook = mock(Book.class);
        when(mockBook.getName()).thenReturn(mockBookName);

        Installer mockInstaller = mock(Installer.class);
        List<Book> bookList = new ArrayList<Book>();
        bookList.add(mockBook);
        when(mockInstaller.getBooks()).thenReturn(bookList);

        Collection<Installer> mockInstallers = new ArrayList<Installer>();
        mockInstallers.add(mockInstaller);

        TGAMFModules modules = new TGAMFModules(this, mockInstallers);
        mObjectGraph = ObjectGraph.create(modules);

        // Now the actual test
        mObjectGraph.inject(this); // Get the RefreshManager

        rM.getAvailableModulesFlattened()
                .toBlocking()
                .forEach(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        assertEquals(mockBookName, book.getName());
                    }
                });

        verify(mockInstaller).getBooks();
        verify(mockBook).getName();
    }

    /*
    public void testInstallerFromBook() throws Exception {

    }

    public void testIsRefreshComplete() throws Exception {

    }
    */
}