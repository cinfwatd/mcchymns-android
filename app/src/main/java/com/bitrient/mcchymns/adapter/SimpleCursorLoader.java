package com.bitrient.mcchymns.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;

import com.bitrient.mcchymns.database.HymnDbHelper;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/21/15
 */
public class SimpleCursorLoader extends CursorLoader {

    private String mTable;
    private String[] mColumns;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;


    public SimpleCursorLoader(Context context, String table, String[] columns, String selection,
                              String[] selectionArgs, String groupBy, String having, String orderBy) {
        super(context);

        mTable = table;
        mColumns = columns;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
    }

    @Override
    public Cursor loadInBackground() {
        HymnDbHelper dbHelper = HymnDbHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.query(mTable, mColumns, mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy);
    }
}