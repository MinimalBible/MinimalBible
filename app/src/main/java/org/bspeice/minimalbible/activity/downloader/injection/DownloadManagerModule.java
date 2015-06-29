package org.bspeice.minimalbible.activity.downloader.injection;

import android.content.Context;
import android.net.ConnectivityManager;

import org.bspeice.minimalbible.activity.downloader.DownloadActivity;
import org.bspeice.minimalbible.activity.downloader.DownloadPrefs;
import org.bspeice.minimalbible.activity.downloader.manager.BookManager;
import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activity.downloader.manager.LocaleManager;
import org.bspeice.minimalbible.activity.downloader.manager.RefreshManager;
import org.bspeice.minimalbible.activity.search.MBIndexManager;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import java.util.Collection;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import de.devland.esperandro.Esperandro;
import rx.subjects.PublishSubject;

@Module
public class DownloadManagerModule {

    @Provides
    DownloadPrefs provideDownloadPrefs(DownloadActivity activity) {
        return Esperandro.getPreferences(DownloadPrefs.class, activity);
    }

    @Provides
    PublishSubject<DLProgressEvent> dlProgressEventPublisher() {
        return PublishSubject.create();
    }

    @Provides
    BookManager provideBookDownloadManager(Books installedBooks, RefreshManager rm,
                                           PublishSubject<DLProgressEvent> progressEvents,
                                           MBIndexManager mbIndexManager) {
        return new BookManager(installedBooks, rm, progressEvents, mbIndexManager);
    }

    @Provides
    Collection<Installer> provideInstallers() {
        return new InstallManager().getInstallers().values();
    }

    @Provides
    RefreshManager provideRefreshManager(Collection<Installer> installers, List<String> exclude,
                                         DownloadPrefs prefs,
                                         DownloadActivity activity) {
        return new RefreshManager(installers, exclude, prefs,
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    @Provides
    LocaleManager provideLocaleManager(RefreshManager refreshManager) {
        return new LocaleManager(refreshManager);
    }
}
