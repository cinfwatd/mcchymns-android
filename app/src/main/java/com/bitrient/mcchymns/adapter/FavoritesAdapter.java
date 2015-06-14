package com.bitrient.mcchymns.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitrient.mcchymns.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>  {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_EMPTY = 1;

    private List<String> mTitles;
    private int mIcon;
    private Context mContext;

    private List<String> visibleObjects;
    private boolean isSearch = false;

    /**
     * Filters the dataset
     * This is used with the SearchView to filter listed data.
     *
     * @param filterText query string
     */
    public void setFilter(String filterText) {

        visibleObjects = new ArrayList<>();
//        constraints = constraint.toString().toLowerCase();
        filterText = filterText.toLowerCase();

        for (String item: mTitles) {
            if (item.toLowerCase().contains(filterText))
                visibleObjects.add(item);
        }

        isSearch = true;
        notifyDataSetChanged();
    }

    public void flushFilter() {
        visibleObjects = new ArrayList<>();
        visibleObjects.addAll(mTitles);

        isSearch = false;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;

        private TextView textView;
        private ImageView imageView;

        public ViewHolder(View view, int viewType) {
            super(view);

            if (viewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderId = 1;
            } else if (viewType == TYPE_EMPTY) {
                textView = (TextView) itemView.findViewById(R.id.empty_favorites_message);
                imageView = (ImageView) itemView.findViewById(R.id.empty_favorites_icon);
                holderId = 2;
            }
        }
    }

    public FavoritesAdapter(List<String> titles, int icon, Context context) {
        mIcon = icon;
        mTitles = titles;
        mContext = context;
        visibleObjects = titles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view, viewType);

            return viewHolder;
        } else if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_favorites, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view, viewType);

            return viewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (viewHolder.holderId == 1) { //item
            viewHolder.textView.setText(visibleObjects.get(position));
            viewHolder.imageView.setImageResource(mIcon);
        } else if (viewHolder.holderId == 2) { //empty
            if (isSearch) {
                viewHolder.textView.setText(mContext.getResources().getText(R.string.no_hymns_found));
                viewHolder.imageView.setImageResource(R.mipmap.ic_search);
            } // else use defaults
        }
    }

    @Override
    public int getItemCount() {
        return visibleObjects.size() > 0 ? visibleObjects.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (visibleObjects.size() == 0) {
            return TYPE_EMPTY;
        }

        return super.getItemViewType(position);
    }
}
