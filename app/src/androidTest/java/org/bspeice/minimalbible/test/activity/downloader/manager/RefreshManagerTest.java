package org.bspeice.minimalbible.test.activity.downloader.manager;

import com.jayway.awaitility.Awaitility;

import junit.framework.TestCase;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.Installer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import rx.functions.Action1;

import static com.jayway.awaitility.Awaitility.await;
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
    class RMTModules {
        Injector i;
        Collection<Installer> installers;

        RMTModules(Injector i, Collection<Installer> installers) {
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

        RMTModules modules = new RMTModules(this, mockInstallers);
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

    public void testInstallerFromBook() throws Exception {
        // Environment setup
        Book mockBook = mock(Book.class);

        Installer mockInstaller = mock(Installer.class);
        List<Book> bookList = new ArrayList<Book>();
        bookList.add(mockBook);
        when(mockInstaller.getBooks()).thenReturn(bookList);

        Collection<Installer> mockInstallers = new ArrayList<Installer>();
        mockInstallers.add(mockInstaller);

        RMTModules modules = new RMTModules(this, mockInstallers);
        mObjectGraph = ObjectGraph.create(modules);

        // And the actual test
        mObjectGraph.inject(this);
        Installer i = rM.installerFromBook(mockBook);

        assertSame(mockInstaller, i);
        verify(mockInstaller).getBooks();
    }

    public void testRefreshSeparateThread() {
        Installer mockInstaller = mock(Installer.class);
        final List<Book> bookList = new ArrayList<Book>();
        when(mockInstaller.getBooks()).thenAnswer(new Answer<List<Book>>() {
            @Override
            public List<Book> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(1000); // Just long enough to give us a gap between
                                    // refresh start and complete
                return bookList;
            }
        });

        Collection<Installer> mockInstallers = new ArrayList<Installer>();
        mockInstallers.add(mockInstaller);

        RMTModules modules = new RMTModules(this, mockInstallers);
        mObjectGraph = ObjectGraph.create(modules);

        // And the actual test
        mObjectGraph.inject(this);

        // So the refresh should be kicked off at the constructor, meaning that it's not "complete"
        assertFalse(rM.isRefreshComplete());

        // But, if it's on another thread, it should finish up eventually, right?
        await().atMost(5, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return rM.isRefreshComplete();
            }
        });
    }
}