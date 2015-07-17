package com.bitrient.mcchymns.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.database.HymnContract;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class HymnAdapter extends SelectableAdapter<HymnAdapter.ViewHolder>  {
    private static final int TYPE_HYMN_ROW = 1;
    private static final int TYPE_SEARCH_HYMN_ROW = 2;

    private Cursor mCursor;
    private int mRowIdColumn;
    private int mRowNumberColumn;

    private boolean mIsSearch;

    private ViewHolder.ClickListener clickListener;

    public HymnAdapter(Cursor cursor, ViewHolder.ClickListener clickListener) {
        mCursor = cursor;

        mRowIdColumn = cursor != null ? cursor.getColumnIndex(HymnContract.StanzaEntry._ID) : -1;
        mRowNumberColumn = cursor != null ? cursor.getColumnIndex(HymnContract.StanzaEntry.COLUMN_NAME_HYMN_NUMBER) : -1;

        this.clickListener = clickListener;
        mIsSearch = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HYMN_ROW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hymn_row, viewGroup, false);

            return new ViewHolder(view, viewType, clickListener);
        } else if (viewType == TYPE_SEARCH_HYMN_ROW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hymn_search_row, viewGroup, false);

            return new ViewHolder(view, viewType, clickListener);
//            return appropriate viewholder
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (isSearch()) {
            return TYPE_SEARCH_HYMN_ROW;
        }

        return TYPE_HYMN_ROW;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (mCursor == null) throw new IllegalStateException("This should only be called when the cursor is valid.");
        if (!mCursor.moveToPosition(position)) throw new IllegalStateException("Couldn't move the cursor to the position " + position);

        int end = TextUtils.indexOf(mCursor.getString(1), "\\n");

        String firstLine;

        try {
            firstLine = TextUtils.substring(mCursor.getString(1), 0, end);
        } catch (StringIndexOutOfBoundsException e) {
            firstLine = mCursor.getString(1);
        }

        String hymnNumber = mCursor.getString(0);
        String stanzaNumber = mCursor.getString(2);

        if (stanzaNumber.equals("0")) stanzaNumber = "Ch.";

        if (viewHolder.holderId == TYPE_HYMN_ROW) {
            viewHolder.firstLineTextView.setText(firstLine);
            viewHolder.hymnNumberTextView.setText(hymnNumber);
        } else {
            viewHolder.firstLineTextView.setText(firstLine);
            viewHolder.hymnNumberTextView.setText(hymnNumber);
            // prepare search view row
            viewHolder.hymnStanzaNumberTextView.setText(stanzaNumber);
        }
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

    public long getItemNumber(int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowNumberColumn);
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
            mRowIdColumn = cursor.getColumnIndexOrThrow(HymnContract.StanzaEntry._ID);
            mRowNumberColumn = cursor.getColumnIndexOrThrow(HymnContract.StanzaEntry.COLUMN_NAME_HYMN_NUMBER);
        } else {
            mRowIdColumn = -1;
            mRowNumberColumn = -1;
        }

        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @SuppressWarnings("unused")
        private static final String TAG = ViewHolder.class.getSimpleName();

        private TextView firstLineTextView;
        private TextView hymnNumberTextView;
        private TextView hymnStanzaNumberTextView;

        private View selectedOverlay;

        private ClickListener listener;

        private int holderId;

        public ViewHolder(View view, int viewType, ClickListener listener) {
            super(view);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.listener = listener;

            if (viewType == TYPE_HYMN_ROW) {
                firstLineTextView = (TextView) itemView.findViewById(R.id.hymn_row_title);
                hymnNumberTextView = (TextView) itemView.findViewById(R.id.hymn_row_number);
                holderId = TYPE_HYMN_ROW;
            } else {
                hymnStanzaNumberTextView = (TextView) itemView.findViewById(R.id.hymn_stanza_number);
                firstLineTextView = (TextView) itemView.findViewById(R.id.hymn_row_title);
                hymnNumberTextView = (TextView) itemView.findViewById(R.id.hymn_row_number);
//                set search view info here
                holderId = TYPE_SEARCH_HYMN_ROW;
            }

            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
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

    private boolean isSearch() {
        return mIsSearch;
    }

    public void setIsSearch(boolean isSearch) {
        mIsSearch = isSearch;
    }
}
