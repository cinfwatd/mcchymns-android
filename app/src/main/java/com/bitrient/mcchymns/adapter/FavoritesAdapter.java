package com.bitrient.mcchymns.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitrient.mcchymns.R;

import java.util.List;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>  {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private List<String> mTitles;
    private int mIcon;

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
            }
        }
    }

    public FavoritesAdapter(List<String> titles, int icon) {
        mIcon = icon;
        mTitles = titles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view, viewType);

            return viewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (viewHolder.holderId == 1) { //item
            viewHolder.textView.setText(mTitles.get(position));
            viewHolder.imageView.setImageResource(mIcon);
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }
}
