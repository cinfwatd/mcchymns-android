package com.bitrient.mcchymns.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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

import com.bitrient.mcchymns.HymnActivity;
import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.adapter.HymnAdapter;
import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.view.EmptiableRecyclerView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavouritesFragment extends Fragment implements
        HymnAdapter.ViewHolder.ClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        SortDialogFragment.SortDialogListener {

    @SuppressWarnings("unused")
    private static final String TAG = FavouritesFragment.class.getSimpleName();
    private final static String QUERY_STRING = "queryString";
    private final static String SELECTED_ITEMS = "selectedItems";
    private static final int LOADER_ID = 0;

    private EmptiableRecyclerView recyclerView;
    private SearchView mSearchView;

    private HymnAdapter mHymnAdapter;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    private ActionMode mActionMode;

    private HymnActivity.HymnSelectionListener mSelectionListener;

    private CharSequence mCurrentFilter;
    private boolean mIsSearchViewOpen;
    private int mSortType;

    private boolean mIsLoaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsLoaded = false;
        mSortType = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_favourites, container, false);

        recyclerView = (EmptiableRecyclerView) rootView.findViewById(R.id.favorites_recycler_view);
        recyclerView.setEmptyView(rootView.findViewById(R.id.empty_favorites));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mHymnAdapter = new HymnAdapter(null, this);
        recyclerView.setAdapter(mHymnAdapter);

        setRecyclerViewLayoutManager(recyclerView);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mHymnAdapter));

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null && !mIsLoaded) {
            mIsLoaded = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        mSearchView.setQueryHint(getActivity().getResources().getString(R.string.search));

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
                getLoaderManager().restartLoader(LOADER_ID, null, FavouritesFragment.this);

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
            case R.id.action_remove_all:
                if (mHymnAdapter.getItemCount() == 0) return true;

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.confirm_clear_favourites);
                builder.setMessage(R.string.operation_not_undone);
                builder.setIcon(R.drawable.ic_delete_black_24dp);
                builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new RemoveTask().execute();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();

                return true;

            case R.id.action_sort:
                if (mHymnAdapter.getItemCount() == 0) return true;

                final SortDialogFragment sortDialog = new SortDialogFragment();
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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mSelectionListener = (HymnActivity.HymnSelectionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement the HymnSelectionListener interface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mSelectionListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        /**
         * Hide softkey if open.
         * Like when a user searches for a hymn and selects one.
         */
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * Returning from backstack scenario
         */
        if (mCurrentFilter != null && mIsSearchViewOpen == false) {
            mIsSearchViewOpen = true;
        } else if (mIsLoaded) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            toggleSelection(position);
        } else {
            final long itemNumber = mHymnAdapter.getItemNumber(position);
            mSelectionListener.onHymnSelectedInteraction((int) itemNumber);
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
                HymnContract.HymnEntry._ID,
                HymnContract.HymnEntry.COLUMN_NAME_TOPIC_ID,
                HymnContract.TopicEntry.COLUMN_NAME_TOPIC,
                HymnContract.SubjectEntry.COLUMN_NAME_SUBJECT
        };
        String selection = HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE + " IS NOT NULL";

        String sortOrder;
        if (mSortType == SortDialogFragment.SORT_BY_FIRST_LINES) {
            sortOrder = HymnContract.StanzaEntry.FIRST_LINES_SORT_ORDER;
        } else {
            sortOrder = HymnContract.StanzaEntry.DEFAULT_SORT_ORDER;
        }

        return new CursorLoader(getActivity(), baseUri, projection, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        final boolean isSearch = mCurrentFilter != null;
        mHymnAdapter.setHideHeaders(mSortType == SortDialogFragment.SORT_BY_FIRST_LINES);
        mHymnAdapter.setIsSearch(isSearch);
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
        getLoaderManager().restartLoader(LOADER_ID, null, FavouritesFragment.this);
    }

    private class RemoveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ContentValues values = new ContentValues();
            values.putNull(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE);

            String selection = HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE + " IS NOT NULL";
            getActivity().getContentResolver().update(HymnContract.HymnEntry.CONTENT_FTS_URI, values, selection, null);

            return null;
        }
    }
}