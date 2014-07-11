package org.bspeice.minimalbible.activity.downloader;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBibleModules;
import org.bspeice.minimalbible.activity.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activity.downloader.manager.InstalledManager;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;

/**
 * Module mappings for the classes under the Download Activity
 */
@Module(
        injects = {
                BookListFragment.class,
                BookItemHolder.class,
                BookDownloadManager.class,
                RefreshManager.class,
                DownloadNavDrawerFragment.class,
                DownloadActivity.class,
                InstalledManager.class
        },
        addsTo = MinimalBibleModules.class
)
public class DownloadActivityModules {
    DownloadActivity activity;

    DownloadActivityModules(DownloadActivity activity) {
        this.activity = activity;
    }

    @Provides @Singleton
    DownloadPrefs provideDownloadPrefs() {
        return Esperandro.getPreferences(DownloadPrefs.class, activity);
    }

    @Provides @Singleton
    DownloadActivity provideDownloadActivity() {
        return activity;
    }

    @Provides @Singleton
    Injector provideActivityInjector() {
        return activity;
    }

    @Provides @Singleton
    BookDownloadManager provideBookDownloadManager() {
        return new BookDownloadManager(activity);
    }

    @Provides @Singleton
    @Named("ValidCategories")
    List<BookCategory> provideValidCategories() {
        return new ArrayList<BookCategory>() {{
            add(BookCategory.BIBLE);
            add(BookCategory.COMMENTARY);
            add(BookCategory.DICTIONARY);
            add(BookCategory.MAPS);
        }};
    }

    //TODO: Move this to a true async
    @Provides @Singleton
    Books provideInstalledBooksManager() {
        return Books.installed();
    }

    @Provides
    List<Book> provideInstalledBooks(Books b) {
        return b.getBooks();
    }

    @Provides @Singleton
    Collection<Installer> provideInstallers() {
        return new InstallManager().getInstallers().values();
    }
}
