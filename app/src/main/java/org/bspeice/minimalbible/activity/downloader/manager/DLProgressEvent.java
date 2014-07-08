package org.bspeice.minimalbible.activity.downloader.manager;

import org.crosswire.jsword.book.Book;

/**
 * Used for notifying that a book's download progress is ongoing
 */
public class DLProgressEvent {
    private final int progress;
    private final Book b;

    public static final int PROGRESS_COMPLETE = 100;
    public static final int PROGRESS_BEGINNING = 0;

    public DLProgressEvent(int workDone, int totalWork, Book b) {
        if (totalWork == 0) {
            this.progress = 0;
        } else {
            this.progress = (int)((float) workDone / totalWork * 100);
        }
        this.b = b;
    }

    public DLProgressEvent(int workDone, Book b) {
        this.progress = workDone;
        this.b = b;
    }

    public int getProgress() {
        return progress;
    }

    public float toCircular() {
        return ((float)progress) * 360 / 100;
    }

    public Book getB() {
        return this.b;
    }
}
