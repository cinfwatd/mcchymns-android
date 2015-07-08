package com.bitrient.mcchymns.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bitrient.mcchymns.R;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/18/15
 */
public class NavigationDrawerAdapter extends SelectableAdapter<NavigationDrawerAdapter.ViewHolder> {

    private String[] mNavigationTitles;
    private int[] mNavigationIcons;

    private ViewHolder.ClickListener mClickListener;

    public NavigationDrawerAdapter(String[] navigationTitles, int[] navigationIcons,
                                   ViewHolder.ClickListener clickListener) {
        mNavigationIcons = navigationIcons;
        mNavigationTitles = navigationTitles;

        mClickListener = clickListener;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_drawer_row, parent, false);
        return new ViewHolder(itemView, mClickListener);

    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder holder, int position) {

        holder.title.setText(mNavigationTitles[position]);
        holder.icon.setImageResource(mNavigationIcons[position]);

//        highlight the item if it's selected
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mNavigationTitles.length;
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     * <p>
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView icon;
        private TextView title;
        private View selectedOverlay;

        private ClickListener mListener;

        public ViewHolder (View itemView, ClickListener clickListener) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.navigation_drawer_row_icon);
            title = (TextView) itemView.findViewById(R.id.navigation_drawer_row_title);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            mListener = clickListener;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onItemClicked(getLayoutPosition());
        }

        public interface ClickListener {
            void onItemClicked(int position);
        }
    }
}
