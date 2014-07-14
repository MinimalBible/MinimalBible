package org.bspeice.minimalbible.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.bspeice.minimalbible.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Helper for setting up a highlighting navbar
 * This class (and its usage) needs some work refactoring,
 * but the PoC is looking good!
 */
public class NavDrawerAdapter<T> extends BaseAdapter {
    Context context;
    List<T> objects;
    int currentlyHighlighted;

    public NavDrawerAdapter(Context context, List<T> objects) {
        this.context = context;
        this.objects = objects;
    }

    public void setCurrentlyHighlighted(int currentlyHighlighted) {
        this.currentlyHighlighted = currentlyHighlighted;
    }

    public int getCurrentlyHighlighted() {
        return this.currentlyHighlighted;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NavItemHolder holder;
        if (view == null || view.getTag() == null) {
            view = inflater.inflate(R.layout.list_navigation_drawer, viewGroup, false);
            holder = new NavItemHolder(view, i == currentlyHighlighted, (T) getItem(i));
        } else {
            holder = (NavItemHolder) view.getTag();
        }

        holder.bind();
        return view;
    }

    /**
     * Holder object for items in the Nav Drawer
     */

    protected class NavItemHolder {
        @InjectView(R.id.navlist_selected_highlight)
        ImageView highlight;

        @InjectView(R.id.navlist_content)
        TextView content;

        boolean highlighted;
        View v;
        T object;

        public NavItemHolder(View v, boolean highlighted, T object) {
            this.v = v; // Needed for resolving colors below
            ButterKnife.inject(this, v);
            this.highlighted = highlighted;
            this.object = object;
        }

        public void bind() {
            content.setText(object.toString());
            if (highlighted) {
                highlight.setImageDrawable(new ColorDrawable(v.getResources()
                    .getColor(R.color.navbar_highlight)));
            } else {
                highlight.setImageDrawable(new ColorDrawable(v.getResources()
                    .getColor(R.color.navbar_unhighlighted)));
            }
        }
    }
}
