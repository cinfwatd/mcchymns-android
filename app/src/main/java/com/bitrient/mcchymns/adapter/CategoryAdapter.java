package com.bitrient.mcchymns.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.database.HymnContract;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/19/15
 */
public class CategoryAdapter extends SelectableAdapter <CategoryAdapter.ViewHolder> {

    private Cursor mCursor;
    private ViewHolder.ClickListener mClickListener;

    private int mRowIdColumn;

    public CategoryAdapter(Cursor cursor, ViewHolder.ClickListener clickListener) {
        mCursor = cursor;
        mRowIdColumn = cursor != null ? cursor.getColumnIndexOrThrow(HymnContract.SubjectEntry._ID) : -1;

        mClickListener = clickListener;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_category_item, parent, false);

        return new ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, int position) {
        if (mCursor == null) throw new IllegalStateException("This should only be called when when cursor is valid.");
        if (!mCursor.moveToPosition(position)) throw new IllegalStateException("Couldn't move the cursor to the position " + position);

        String category = mCursor.getString(1);
        Long index = mCursor.getLong(0);

        holder.mCategory.setText(category);
        holder.mselectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

        final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
//        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
//            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
//            sglp.setFullSpan(index % 3 == 0);
//            holder.itemView.setLayoutParams(sglp);
//        }
    }

    @Override
    public long getItemId(int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            mCursor.getLong(mRowIdColumn);
        }

        return 0;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        if (cursor == mCursor) return;

        mCursor = cursor;
        mRowIdColumn = cursor != null ? cursor.getColumnIndexOrThrow(HymnContract.SubjectEntry._ID) : -1;

        notifyDataSetChanged();
    }

    public Cursor getCursor(){
        return mCursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView mCategory;
        private TextView mTopic;

        private View mselectedOverlay;

        private ClickListener mListener;

        public ViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);

            mCategory = (TextView) itemView.findViewById(R.id.category_title);
            mTopic = (TextView) itemView.findViewById(R.id.category_topic);

            mselectedOverlay = itemView.findViewById(R.id.selected_overlay);

            mListener = clickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onItemClicked(getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mListener != null && mListener.onItemLongClicked(getLayoutPosition());
        }

        public interface ClickListener {
            void onItemClicked(int position);
            boolean onItemLongClicked(int position);
        }
    }
}
