package com.bitrient.mcchymns.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.bitrient.mcchymns.HymnViewActivity;
import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.adapter.HymnAdapter;
import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.fragment.dialog.ConfirmDialogFragment;
import com.bitrient.mcchymns.fragment.dialog.GotoHymnDialogFragment;
import com.bitrient.mcchymns.fragment.dialog.SortDialog;
import com.bitrient.mcchymns.view.EmptiableRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesActivityFragment extends Fragment implements
        HymnAdapter.ViewHolder.ClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        SortDialog.SortDialogListener {

    @SuppressWarnings("unused")
    private static final String TAG = FavoritesActivityFragment.class.getSimpleName();
    private final static String QUERY_STRING = "queryString";
    private final static String SELECTED_ITEMS = "selectedItems";
    private static final int LOADER_ID = 0;

    private EmptiableRecyclerView recyclerView;
    private SearchView mSearchView;

    private HymnAdapter mHymnAdapter;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    private ActionMode mActionMode;



    private CharSequence mCurrentFilter;
    private boolean mIsSearchViewOpen;
    private int mSortType = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = (EmptiableRecyclerView) rootView.findViewById(R.id.favorites_recycler_view);
        recyclerView.setEmptyView(rootView.findViewById(R.id.empty_favorites));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mHymnAdapter = new HymnAdapter(null, this);
        recyclerView.setAdapter(mHymnAdapter);

        setRecyclerViewLayoutManager(recyclerView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

//        If a layout manager has already been set, get current scroll position
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_goto_hymn, menu);

        mSearchView = (SearchView) menu.findItem(R.id.favorites_action_search).getActionView();

        mSearchView.setQueryHint(getActivity().getResources().getString(R.string.search_hint));

        View searchPlate = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.textfield_search_selected_dashed);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // hack used to tell the RecyclerView that its empty due to a search filter so that
                // it can respond with the appropriate background.
                recyclerView.setSearch(!TextUtils.isEmpty(newText));

                mCurrentFilter = !TextUtils.isEmpty(newText) ? newText : null;
                getLoaderManager().restartLoader(LOADER_ID, null, FavoritesActivityFragment.this);

                return false;
            }


        });

        if (mIsSearchViewOpen) {
            mSearchView.setIconified(false);
            mSearchView.setQuery(mCurrentFilter, false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites_action_remove_all:
                if (mHymnAdapter.getItemCount() == 0) return true;

                final ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance();
                dialogFragment.show(getFragmentManager(), "confirmDialog");
                return true;

            case R.id.action_goto:
                GotoHymnDialogFragment hymnDialogFragment
                        = GotoHymnDialogFragment.newInstance();
                hymnDialogFragment.show(getFragmentManager(), "HymnDialogFragment");
                return true;

            case R.id.action_sort:
                final SortDialog sortDialog = new SortDialog();
                sortDialog.setTargetFragment(this, 2);
                sortDialog.show(getFragmentManager(), "sortDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(QUERY_STRING)) {
            mCurrentFilter = savedInstanceState.getCharSequence(QUERY_STRING);
            mIsSearchViewOpen = true;
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_ITEMS)) {
            mHymnAdapter.setSelectedItems(savedInstanceState.getIntegerArrayList(SELECTED_ITEMS));
            initializeActionMode();
            invalidateActionMode();
        }

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSearchView != null && !mSearchView.isIconified()) {
            outState.putCharSequence(QUERY_STRING, mSearchView.getQuery());
        }

        if (mActionMode != null) {
            outState.putIntegerArrayList(SELECTED_ITEMS, new ArrayList<>(mHymnAdapter.getSelectedItems()));
        }


        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            toggleSelection(position);
        } else {
            final long itemNumber = mHymnAdapter.getItemNumber(position);
            Intent hymnIntent = new Intent(getActivity(), HymnViewActivity.class);
            hymnIntent.putExtra(HymnViewActivity.SELECTED_HYMN, (int) itemNumber);
            startActivity(hymnIntent);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        initializeActionMode();

        toggleSelection(position);

        return true;
    }

    private void initializeActionMode() {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        }
    }

    /**
     * Toggle the selection state of an item
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (mActionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        mHymnAdapter.toggleSelection(position);
        invalidateActionMode();
    }

    private void invalidateActionMode() {
        int count = mHymnAdapter.getSelectedItemCount();

        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.format("%d selected", count));
            mActionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        /**
         * Called when action mode is first created. The menu supplied will be used to
         * generate action buttons for the action mode.
         *
         * @param mode ActionMode being created
         * @param menu Menu used to populate action buttons
         * @return true if the action mode should be created, false if entering this
         * mode should be aborted.
         */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.favorites_menu_selected, menu);
            return true;
        }

        /**
         * Called to refresh an action mode's action menu whenever it is invalidated.
         *
         * @param mode ActionMode being prepared
         * @param menu Menu used to populate action buttons
         * @return true if the menu or action mode was updated, false otherwise.
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /**
         * Called to report a user click on an action button.
         *
         * @param mode The current ActionMode
         * @param item The item that was clicked
         * @return true if this callback handled the event, false if the standard MenuItem
         * invocation should continue.
         */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.favorites_selected_action_remove:
                    removeItems(mHymnAdapter.getSelectedItems());
//                    mHymnAdapter.removeItems(mHymnAdapter.getSelectedItems());
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Called when an action mode is about to be exited and destroyed.
         *
         * @param mode The current ActionMode being destroyed
         */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mHymnAdapter.clearSelection();
            mActionMode = null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;

        // Pick the base URI to use depending on whether we are currently filtering.
        if (mCurrentFilter != null) {
            baseUri = Uri.withAppendedPath(HymnContract.HymnEntry.CONTENT_FILTER_FTS_URI, Uri.encode(mCurrentFilter.toString()));
        } else {
            baseUri = HymnContract.HymnEntry.CONTENT_FTS_URI;
        }

        String[] projection = new String[] {
                HymnContract.StanzaEntry.COLUMN_NAME_HYMN_NUMBER,
                HymnContract.StanzaEntry.COLUMN_NAME_STANZA,
                HymnContract.StanzaEntry.COLUMN_NAME_STANZA_NUMBER,
                HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE,
                HymnContract.StanzaEntry._ID
        };
        String selection = HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE + " IS NOT NULL";

        String sortOrder;
        if (mSortType == SortDialog.SORT_BY_FIRST_LINES) {
            sortOrder = HymnContract.StanzaEntry.FIRST_LINES_SORT_ORDER;
        } else {
            sortOrder = HymnContract.StanzaEntry.DEFAULT_SORT_ORDER;
        }

        return new CursorLoader(getActivity(), baseUri, projection, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        mHymnAdapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        mHymnAdapter.swapCursor(null);
    }

    /**
     * Removes the item at specified position from the favorites list.
     * @param position postion to remove
     */
    public void removeItem(int position) {

        ContentValues values = new ContentValues();
        values.putNull(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE);

        String selection = HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER + " = ?";
        String[] selectionArgs = new String[] {
                Long.toString(mHymnAdapter.getItemNumber(position))
        };
        getActivity().getContentResolver().update(HymnContract.HymnEntry.CONTENT_FTS_URI, values, selection, selectionArgs);
    }

    /**
     * Removes multiple hymns from favorites based on the specified list
     * @param positions list of items to remove.
     */
    public void removeItems(List<Integer> positions) {

        if (positions.size() == 1) {
            removeItem(positions.get(0));
        } else {
            removeRange(positions);
        }
    }

    public void removeRange(List<Integer> positions) {

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        ContentValues values = new ContentValues();
        for (int position: positions) {
            Uri baseUri = Uri.withAppendedPath(HymnContract.HymnEntry.CONTENT_FTS_URI,
                    Uri.encode(Long.toString(mHymnAdapter.getItemNumber(position))));

            values.clear();
            values.putNull(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(baseUri);
            builder.withValues(values);

            operations.add(builder.build());
        }

        try {
            getActivity().getContentResolver().applyBatch(HymnContract.AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            // If any error is thrown, the operation is implicitly aborted.
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSortTypeSelected(int which) {
        mSortType = which;
        getLoaderManager().restartLoader(LOADER_ID, null, FavoritesActivityFragment.this);
    }
}
