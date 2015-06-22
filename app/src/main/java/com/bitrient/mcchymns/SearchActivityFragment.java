package com.bitrient.mcchymns;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.bitrient.mcchymns.adapter.CategoryAdapter;
import com.bitrient.mcchymns.adapter.SimpleCursorLoader;
import com.bitrient.mcchymns.database.HymnContract;
import com.bitrient.mcchymns.dialog.TopicDialogFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        CategoryAdapter.ViewHolder.ClickListener, TopicDialogFragment.TopicDialogListener {
    @SuppressWarnings("unused")
    private static final String TAG = SearchActivityFragment.class.getSimpleName();

    private SearchView mSearchView;

    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;

    private Cursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_category_recycler_view);

        mAdapter = new CategoryAdapter(null, this);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) menu.findItem(R.id.search_action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        View searchPlate = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.textfield_search_selected_dashed);

        mSearchView.setQueryRefinementEnabled(true);
//        mSearchView.onActionViewExpanded();
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_action_search:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Interface: ClickListener
     */

    @Override
    public void onItemClicked(int position) {
        if (mCursor == null) mCursor = mAdapter.getCursor();
        mCursor.moveToPosition(position);

        final TopicDialogFragment dialogFragment = TopicDialogFragment.getInstance(
                mCursor.getInt(mCursor.getColumnIndexOrThrow(HymnContract.SubjectEntry._ID)), mCursor.getString(1));
//        dialogFragment.getDialog().set
        dialogFragment.setTargetFragment(this, 1);
        dialogFragment.show(getFragmentManager(), "topicDialog" + position);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        mAdapter.toggleSelection(position);
        return true;
    }

    /**
     * Interface: LoaderManager.LoaderCallbacks
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = new String[] {
                HymnContract.SubjectEntry._ID,
                HymnContract.SubjectEntry.COLUMN_NAME_SUBJECT
        };

        return new SimpleCursorLoader(getActivity(), HymnContract.SubjectEntry.TABLE_NAME, columns,
                null, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Interface: TopicDialogListener
     */

    @Override
    public void onDialogPositiveClicked(DialogFragment dialog) {
        Log.d(TAG, "DIALOG== positve clicked.");
    }

    @Override
    public void onDialogNegativeClicked(DialogFragment dialog) {
        Log.d(TAG, "DIALOG== negative clicked.");
    }
}
