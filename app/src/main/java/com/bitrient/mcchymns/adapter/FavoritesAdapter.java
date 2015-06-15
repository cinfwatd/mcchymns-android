package com.bitrient.mcchymns.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitrient.mcchymns.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class FavoritesAdapter extends SelectableAdapter<FavoritesAdapter.ViewHolder>  {

    private List<String> mTitles;
    private int mIcon;

    private List<String> visibleObjects;

    private ViewHolder.ClickListener clickListener;

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

        if (visibleObjects.isEmpty()) {
            notifyItemRangeChanged(0,0);
        }

        notifyDataSetChanged();
    }

    public void flushFilter() {
        visibleObjects = new ArrayList<>();
        visibleObjects.addAll(mTitles);

        notifyDataSetChanged();
    }

    public FavoritesAdapter(List<String> titles, int icon, ViewHolder.ClickListener clickListener) {
        mIcon = icon;
        mTitles = titles;
        visibleObjects = titles;

        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, viewGroup, false);

        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.textView.setText(visibleObjects.get(position));
        viewHolder.imageView.setImageResource(mIcon);

//            highlight the item if it's selected
        viewHolder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return visibleObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @SuppressWarnings("unused")
        private static final String TAG = ViewHolder.class.getSimpleName();

        private TextView textView;
        private ImageView imageView;
        private View selectedOverlay;

        private ClickListener listener;

        public ViewHolder(View view, ClickListener listener) {
            super(view);

            textView = (TextView) itemView.findViewById(R.id.rowText);
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            this.listener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getLayoutPosition());
            }
        }

        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public boolean onLongClick(View v) {
            return listener != null && listener.onItemLongClicked(getLayoutPosition());

        }

        public interface ClickListener {
            void onItemClicked(int position);
            boolean onItemLongClicked(int position);
        }
    }

    public void removeItem(int position) {
        visibleObjects.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
//        Reveerse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

//        split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    public void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            visibleObjects.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}
