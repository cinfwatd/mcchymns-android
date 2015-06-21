package com.bitrient.mcchymns.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.database.HymnContract;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class FavoritesAdapter extends SelectableAdapter<FavoritesAdapter.ViewHolder>  {

    private int mIcon;
    private Cursor mCursor;
    private int mRowIdColumn;

    private ViewHolder.ClickListener clickListener;

    public FavoritesAdapter(Cursor cursor, int icon, ViewHolder.ClickListener clickListener) {
        mIcon = icon;
        mCursor = cursor;

        mRowIdColumn = cursor != null ? cursor.getColumnIndex(HymnContract.HymnEntry._ID) : -1;

        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorites_row, viewGroup, false);

        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (mCursor == null) throw new IllegalStateException("This should only be called when the cursor is valid.");
        if (!mCursor.moveToPosition(position)) throw new IllegalStateException("Couldn't move the cursor to the position " + position);

        String firstLine = mCursor.getString(1);
        String hymnNumber = mCursor.getString(2);

        viewHolder.hymnIcon.setImageResource(mIcon);
        viewHolder.firstLineTextView.setText(firstLine);
        viewHolder.hymnNumberTextView.setText("#" + hymnNumber);

//            highlight the item if it's selected
        viewHolder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

    }

    /**
     * Return the stable ID for the item at <code>position</code>. If {@link #hasStableIds()}
     * would return false this method should return #NO_ID. The default implementation
     * of this method returns #NO_ID.
     *
     * @param position Adapter position to query
     * @return the stable ID of the item at position
     */
    @Override
    public long getItemId(int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }

        return 0;
    }



    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor == cursor) return;

        mCursor = cursor;
        if (mCursor != null) {
            mRowIdColumn = cursor.getColumnIndexOrThrow(HymnContract.HymnEntry._ID);
        } else {
            mRowIdColumn = -1;
        }

        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @SuppressWarnings("unused")
        private static final String TAG = ViewHolder.class.getSimpleName();

        private TextView firstLineTextView;
        private TextView hymnNumberTextView;
        private ImageView hymnIcon;

        private View selectedOverlay;

        private ClickListener listener;

        public ViewHolder(View view, ClickListener listener) {
            super(view);

            firstLineTextView = (TextView) itemView.findViewById(R.id.favorites_row_title);
            hymnNumberTextView = (TextView) itemView.findViewById(R.id.favorites_row_number);
            hymnIcon = (ImageView) itemView.findViewById(R.id.favorites_row_icon);
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
            if (listener != null) listener.onItemClicked(getLayoutPosition());
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
}
