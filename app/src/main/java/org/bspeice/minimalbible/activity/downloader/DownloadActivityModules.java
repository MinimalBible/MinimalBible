package org.bspeice.minimalbible.activity.downloader;

import android.content.Context;
import android.net.ConnectivityManager;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.MinimalBibleModules;
import org.bspeice.minimalbible.activity.downloader.manager.BookManager;
import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activity.downloader.manager.LocaleManager;
import org.bspeice.minimalbible.activity.downloader.manager.MBIndexManager;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexPolicyAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import rx.subjects.PublishSubject;

/**
 * Module mappings for the classes under the Download Activity
 */
@Module(
        injects = {
                BookListFragment.class,
                BookItemHolder.class,
                BookManager.class,
                RefreshManager.class,
                DownloadActivity.class
        },
        addsTo = MinimalBibleModules.class,
        library = true
)
@SuppressWarnings("unused")
public class DownloadActivityModules {
    DownloadActivity activity;

    DownloadActivityModules(DownloadActivity activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    DownloadPrefs provideDownloadPrefs() {
        return Esperandro.getPreferences(DownloadPrefs.class, activity);
    }

    @Provides
    @Singleton
    DownloadActivity provideDownloadActivity() {
        return activity;
    }

    @Provides
    @Singleton
    Injector provideActivityInjector() {
        return activity;
    }

    /**
     * Provide the context for the DownloadActivity. We name it so that we don't have to
     * \@Provides a specific class, but can keep track of what exactly we mean by "Context"
     *
     * @return The DownloadActivity Context
     */
    @Provides
    @Singleton
    @Named("DownloadActivityContext")
    Context provideActivityContext() {
        return activity;
    }

    @Provides
    @Singleton
    PublishSubject<DLProgressEvent> dlProgressEventPublisher() {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    BookManager provideBookDownloadManager(Books installedBooks, RefreshManager rm,
                                           PublishSubject<DLProgressEvent> progressEvents,
                                           MBIndexManager mbIndexManager) {
        return new BookManager(installedBooks, rm, progressEvents, mbIndexManager);
    }

    @Provides
    @Singleton
    @Named("ValidCategories")
    List<BookCategory> provideValidCategories() {
        return new ArrayList<BookCategory>() {{
            add(BookCategory.BIBLE);
            add(BookCategory.COMMENTARY);
            add(BookCategory.DICTIONARY);
            add(BookCategory.MAPS);
        }};
    }

    @Provides
    @Singleton
    Collection<Installer> provideInstallers() {
        return new InstallManager().getInstallers().values();
    }

    @Provides
    @Singleton
    RefreshManager provideRefreshManager(Collection<Installer> installers, List<String> exclude,
                                         DownloadPrefs prefs,
                                         @Named("DownloadActivityContext") Context context) {
        return new RefreshManager(installers, exclude, prefs,
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    @Provides
    LocaleManager provideLocaleManager(RefreshManager refreshManager) {
        return new LocaleManager(refreshManager);
    }

    @Provides
    IndexManager indexManager() {
        IndexManager manager = IndexManagerFactory.getIndexManager();
        manager.setIndexPolicy(new IndexPolicyAdapter());
        return manager;
    }

    @Provides
    MBIndexManager mbIndexManager(PublishSubject<DLProgressEvent> downloadEvents,
                                  IndexManager indexManager) {
        return new MBIndexManager(downloadEvents, indexManager);
    }
}
