package org.bspeice.minimalbible.activity.downloader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.crosswire.jsword.book.Book;

import java.util.List;

/**
 * Adapter to inflate list_download_items.xml
 */
public class BookListAdapter extends BaseAdapter implements AbsListView.RecyclerListener {
    private final List<Book> bookList;
    private final LayoutInflater inflater;
    private final Injector injector;

    public BookListAdapter(LayoutInflater inflater, List<Book> bookList, Injector injector) {
        this.bookList = bookList;
        this.inflater = inflater;
        this.injector = injector;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Book getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookItemHolder viewHolder;
        // Nasty Android issue - if you don't check the getTag(), Android will start recycling,
        // and you'll get some really strange issues
        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.list_download_items, parent, false);
            viewHolder = new BookItemHolder(convertView, getItem(position), injector);
        } else {
            viewHolder = (BookItemHolder) convertView.getTag();
        }

        viewHolder.bindHolder();
        return convertView;
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        BookItemHolder holder = (BookItemHolder) view.getTag();
        holder.onScrollOffscreen();
    }

}
