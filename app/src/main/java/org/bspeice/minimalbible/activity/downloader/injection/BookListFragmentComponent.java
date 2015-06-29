package org.bspeice.minimalbible.activity.downloader.injection;

import org.bspeice.minimalbible.activity.downloader.BookListFragment;
import org.bspeice.minimalbible.common.injection.InvalidBookModule;

import dagger.Component;

@Component(modules = {ActivityContextModule.class, DownloadManagerModule.class,
        InvalidBookModule.class})
public interface BookListFragmentComponent {

    BookListFragment injectBookListFragment(BookListFragment fragment);
}
