package org.bspeice.minimalbible.test.activity.downloader;

import org.bspeice.minimalbible.MBTestCase;
import org.bspeice.minimalbible.activity.downloader.BookListFragment;
import org.crosswire.common.util.Language;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.mockito.Mockito;

import rx.Observable;
import rx.functions.Action1;

import static org.mockito.Mockito.when;

public class BookListFragmentTest extends MBTestCase {

    public void testBooksByLanguage() throws Exception {
        BookCategory bibleCategory = BookCategory.BIBLE;
        BookCategory dictionaryCategory = BookCategory.DICTIONARY;
        Language russianLanguage = new Language("ru");
        Language englishLanguage = new Language("en");

        final Book russianBible = Mockito.mock(Book.class);
        when(russianBible.getBookCategory()).thenReturn(bibleCategory);
        when(russianBible.getLanguage()).thenReturn(russianLanguage);

        final Book englishBible = Mockito.mock(Book.class);
        when(englishBible.getBookCategory()).thenReturn(bibleCategory);
        when(englishBible.getLanguage()).thenReturn(englishLanguage);

        final Book englishDictionary = Mockito.mock(Book.class);
        when(englishDictionary.getBookCategory()).thenReturn(dictionaryCategory);
        when(englishDictionary.getLanguage()).thenReturn(englishLanguage);

        Observable<Book> mockBooks = Observable.just(russianBible, englishBible,
                englishDictionary);

        // Since we're not testing lifecycle here, don't worry about newInstance()
        TestableBookListFragment fragment = new TestableBookListFragment();

        fragment.booksByLanguage(mockBooks, englishLanguage, bibleCategory)
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        assertEquals(englishBible, book);
                    }
                });
        fragment.booksByLanguage(mockBooks, russianLanguage, bibleCategory)
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        assertEquals(russianBible, book);
                    }
                });
        fragment.booksByLanguage(mockBooks, englishLanguage, dictionaryCategory)
                .subscribe(new Action1<Book>() {
                    @Override
                    public void call(Book book) {
                        assertEquals(englishDictionary, book);
                    }
                });
    }

    public static class TestableBookListFragment extends BookListFragment {
        @Override
        public Observable<Book> booksByLanguage(Observable<Book> books, Language language, BookCategory category) {
            return super.booksByLanguage(books, language, category);
        }
    }
}