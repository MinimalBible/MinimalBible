package org.bspeice.minimalbible.activity.viewer.bookutil;

import org.bspeice.minimalbible.util.IteratorUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by bspeice on 7/11/14.
 */
public class VersificationUtil {

    private static final List<BibleBook> INTROS = new ArrayList<BibleBook>() {{
        add(BibleBook.INTRO_BIBLE);
        add(BibleBook.INTRO_OT);
        add(BibleBook.INTRO_NT);
    }};

    public Versification getVersification(Book b) {
        return Versifications.instance().getVersification(
                (String) b.getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION)
        );
    }

    public Observable<BibleBook> getBookNames(Book b) {
        Versification v = getVersification(b);
        return Observable.from(IteratorUtil.copyIterator(v.getBookIterator()))
                .filter(new Func1<BibleBook, Boolean>() {
                    @Override
                    public Boolean call(BibleBook bibleBook) {
                        return !INTROS.contains(bibleBook);
                    }
                });
    }

    public Observable<String> getNiceBookNames(final Book b) {

        return getBookNames(b)
                .map(new Func1<BibleBook, String>() {
                    @Override
                    public String call(BibleBook bibleBook) {
                        return getVersification(b).getLongName(bibleBook);
                    }
                });
    }
}
