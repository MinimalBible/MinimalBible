package org.bspeice.minimalbible.activity.downloader.manager;

import org.crosswire.jsword.book.BookCategory;

// TODO: Listen to BookInstall events?
public class DownloadManager {

	private final String TAG = "DownloadManager";

    // TODO: Inject this, don't have any static references
	public static final BookCategory[] VALID_CATEGORIES = { BookCategory.BIBLE,
			BookCategory.COMMENTARY, BookCategory.DICTIONARY,
			BookCategory.MAPS };
}
