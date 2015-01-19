package org.bspeice.minimalbible.activity.settings;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.bspeice.minimalbible.Injector;
import org.crosswire.jsword.book.Book;

import java.util.List;

import javax.inject.Inject;

/**
 * Set the active "main book"
 * Can not be implemented in Kotlin due to array needs
 */
public class AvailableBookPreference extends ListPreference {

    @Inject
    List<Book> books;

    public AvailableBookPreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        ((Injector) getContext()).inject(this);

        CharSequence[] entries = new CharSequence[books.size()];
        CharSequence[] entryValues = new CharSequence[books.size()];

        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            entries[i] = b.getName();
            entryValues[i] = b.getInitials();
        }

        setEntries(entries);
        setEntryValues(entryValues);

        return super.onCreateView(parent);
    }

    @Override
    public CharSequence getSummary() {
        return getEntry();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        setSummary(getEntry());
    }
}
