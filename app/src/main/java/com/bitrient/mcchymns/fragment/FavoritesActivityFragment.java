package com.bitrient.mcchymns.fragment;

import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;

import com.bitrient.mcchymns.HymnViewActivity;
import com.bitrient.mcchymns.R;
import com.bitrient.mcchymns.adapter.HymnAdapter;
import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.fragment.dialog.ConfirmDialogFragment;
import com.bitrient.mcchymns.view.EmptiableRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesActivityFragment extends Fragment implements
        HymnAdapter.ViewHolder.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @SuppressWarnings("unused")
    private static final String TAG = FavoritesActivityFragment.class.getSimpleName();

    private EmptiableRecyclerView recyclerView;

    private HymnAdapter hymnAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    private SearchView searchView;
    private final String QUERY_STRING = "queryString";
    private final String SELECTED_ITEMS = "selectedItems";
    private CharSequence currentFilter;
    private boolean isSearchViewOpen;

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ContentValues values = new ContentValues();
////        values.put(HymnContract.HymnEntry._ID, "");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER, "9");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE, "Thank you Jesus 9");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE, "True");
//
//        getActivity().getContentResolver().insert(HymnContract.HymnEntry.CONTENT_URI, values);
//
//        values.clear();
////        values.put(HymnContract.HymnEntry._ID, "");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER, "10");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE, "Thank you Jesus 10");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE, "True");
//
//        getActivity().getContentResolver().insert(HymnContract.HymnEntry.CONTENT_URI, values);
//
//        values.clear();
////        values.put(HymnContract.HymnEntry._ID, "");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER, "11");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE, "Thank you Jesus 11");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE, "True");
//        getActivity().getContentResolver().insert(HymnContract.HymnEntry.CONTENT_URI, values);
//
//        values.clear();
////        values.put(HymnContract.HymnEntry._ID, "");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER, "12");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE, "Thank you Jesus 12 ");
//        values.put(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE, "True");
//        getActivity().getContentResolver().insert(HymnContract.HymnEntry.CONTENT_URI, values);

//        add all to favorites
        ContentValues values = new ContentValues();
        values.put(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE, "True");
        getActivity().getContentResolver().update(HymnContract.HymnEntry.CONTENT_URI,
                values, null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = (EmptiableRecyclerView) rootView.findViewById(R.id.favorites_recycler_view);
        recyclerView.setEmptyView(rootView.findViewById(R.id.empty_favorites));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

//        String[] titles = getResources().getStringArray(R.array.fruits_array);

        hymnAdapter = new HymnAdapter(null, R.mipmap.ic_hymn_gray, this);
        recyclerView.setAdapter(hymnAdapter);

        setRecyclerViewLayoutManager(recyclerView);

        return rootView;
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getLoaderManager().initLoader(0, null, this);
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

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link Activity#onCreateOptionsMenu(Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        searchView = (SearchView) menu.findItem(R.id.favorites_action_search).getActionView();

        searchView.setQueryHint(getActivity().getResources().getString(R.string.search_hint));

        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.textfield_search_selected_dashed);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchView.onActionViewCollapsed();

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

                currentFilter = !TextUtils.isEmpty(newText) ? newText : null;
                getLoaderManager().restartLoader(0, null, FavoritesActivityFragment.this);

                return false;
            }


        });

        if (isSearchViewOpen) {
            searchView.setIconified(false);
            searchView.setQuery(currentFilter, false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites_action_remove_all:
                if (hymnAdapter.getItemCount() == 0) return true;

                final ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance();
                dialogFragment.show(getFragmentManager(), "confirmDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when all saved state has been restored into the view hierarchy
     * of the fragment.  This can be used to do initialization based on saved
     * state that you are letting the view hierarchy track itself, such as
     * whether check box widgets are currently checked.  This is called
     * after {@link #onActivityCreated(Bundle)} and before
     * {@link #onStart()}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(QUERY_STRING)) {
            currentFilter = savedInstanceState.getCharSequence(QUERY_STRING);
            isSearchViewOpen = true;
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_ITEMS)) {
            hymnAdapter.setSelectedItems(savedInstanceState.getIntegerArrayList(SELECTED_ITEMS));
            initializeActionMode();
            invalidateActionMode();
        }

        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (searchView != null && !searchView.isIconified()) {
            outState.putCharSequence(QUERY_STRING, searchView.getQuery());
        }

        if (actionMode != null) {
            outState.putIntegerArrayList(SELECTED_ITEMS, new ArrayList<>(hymnAdapter.getSelectedItems()));
        }


        super.onSaveInstanceState(outState);
    }


    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            final long itemId = hymnAdapter.getItemId(position);
            Intent hymnIntent = new Intent(getActivity(), HymnViewActivity.class);
            hymnIntent.putExtra(HymnViewActivity.SELECTED_HYMN, (int) itemId);
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
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }
    }

    /**
     * Toggle the selection state of an item
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        hymnAdapter.toggleSelection(position);
        invalidateActionMode();
    }

    private void invalidateActionMode() {
        int count = hymnAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.format("%d selected", count));
            actionMode.invalidate();
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
                    removeItems(hymnAdapter.getSelectedItems());
//                    hymnAdapter.removeItems(hymnAdapter.getSelectedItems());
                    Log.d(TAG, "favorites menu selected remove");
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
            hymnAdapter.clearSelection();
            actionMode = null;
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;

        // Pick the base URI to use depending on whether we are currently filtering.
        if (currentFilter != null) {
            baseUri = Uri.withAppendedPath(HymnContract.HymnEntry.CONTENT_FILTER_URI, Uri.encode(currentFilter.toString()));
        } else {
            baseUri = HymnContract.HymnEntry.CONTENT_URI;
        }

        String[] projection = new String[] {
                HymnContract.HymnEntry._ID,
                HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE,
                HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER
        };
        String selection = HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE + " IS NOT NULL";

        return new CursorLoader(getActivity(), baseUri, projection, selection, null, null);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        hymnAdapter.swapCursor(data);
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
        hymnAdapter.swapCursor(null);
    }

    /**
     * Removes the item at specified position from the favorites list.
     * @param position postion to remove
     */
    public void removeItem(int position) {

        ContentValues values = new ContentValues();
        values.putNull(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE);

        String selection = HymnContract.HymnEntry._ID + " = ?";
        String[] selectionArgs = new String[] {
                Long.toString(hymnAdapter.getItemId(position))
        };
        getActivity().getContentResolver().update(HymnContract.HymnEntry.CONTENT_URI, values, selection, selectionArgs);
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
            Uri baseUri = Uri.withAppendedPath(HymnContract.HymnEntry.CONTENT_URI,
                    Uri.encode(Long.toString(hymnAdapter.getItemId(position))));

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
        }
    }
}
