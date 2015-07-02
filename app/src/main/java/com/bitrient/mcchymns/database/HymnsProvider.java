package com.bitrient.mcchymns.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class HymnsProvider extends ContentProvider {
    private static final String TAG = "HymnsProvider";

    private HymnDbHelper dbHelper;

    /**
     * Setup projection map
     */
    private static HashMap<String, String> hymnsProjectionMap;
    static {
        hymnsProjectionMap = new HashMap<>();
        hymnsProjectionMap.put(HymnContract.HymnEntry._ID, HymnContract.HymnEntry._ID);
        hymnsProjectionMap.put(HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER,
                HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER);
        hymnsProjectionMap.put(HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE,
                HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE);
        hymnsProjectionMap.put(HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE,
                HymnContract.HymnEntry.COLUMN_NAME_FAVOURITE);
    }

    /**
     * setup URIs
     */
    private static final UriMatcher URIMatcher;
    private static final int HYMNS = 1;
    private static final int HYMNS_ID = 2;
    private static final int HYMNS_FILTER = 3;
    static {
        URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        URIMatcher.addURI(HymnContract.AUTHORITY, "hymns", HYMNS);
        URIMatcher.addURI(HymnContract.AUTHORITY, "hymns/#", HYMNS_ID);
        URIMatcher.addURI(HymnContract.AUTHORITY, "hymns/filter/*", HYMNS_FILTER);
    }

    public HymnsProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (URIMatcher.match(uri)) {
            case HYMNS:
                count = db.delete(HymnContract.HymnEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HYMNS_ID:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(HymnContract.HymnEntry.TABLE_NAME,
                        HymnContract.HymnEntry._ID + "=" + rowId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
//        throw new UnsupportedOperationException("Not yet implemented");
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (URIMatcher.match(uri)) {
            case HYMNS:
                return HymnContract.HymnEntry.CONTENT_TYPE;
            case HYMNS_ID:
                return HymnContract.HymnEntry.CONTENT_ITEM_TYPE;
            case HYMNS_FILTER:
                return HymnContract.HymnEntry.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URIMatcher.match(uri) != HYMNS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(HymnContract.HymnEntry.TABLE_NAME, HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE, values);

        if (rowId > 0) {
            Uri insertedHymnUri = ContentUris.withAppendedId(HymnContract.HymnEntry.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(insertedHymnUri, null);

            return insertedHymnUri;
        }

        Log.i(TAG, "ROW_ID = " + rowId);

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "main onCreate Called");
        dbHelper = HymnDbHelper.getInstance(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (URIMatcher.match(uri)) {
            case HYMNS:
                queryBuilder.setTables(HymnContract.HymnEntry.TABLE_NAME);
                queryBuilder.setProjectionMap(hymnsProjectionMap);
                break;

            case HYMNS_ID:
                queryBuilder.setTables(HymnContract.HymnEntry.TABLE_NAME);
                queryBuilder.setProjectionMap(hymnsProjectionMap);
                queryBuilder.appendWhere(
                        HymnContract.HymnEntry._ID + "=" +
                                uri.getPathSegments().get(1));
                break;
            case HYMNS_FILTER:
                queryBuilder.setTables(HymnContract.HymnEntry.TABLE_NAME);
                queryBuilder.setProjectionMap(hymnsProjectionMap);
                queryBuilder.appendWhere(
                        HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE + " LIKE \"%" + uri.getPathSegments().get(2) + "%\"");
                Log.d(TAG, "QUERY BUILDER = " + queryBuilder.buildQuery(null, null, null, null, null, null));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

//        If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = HymnContract.HymnEntry.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

//        get the database and run the query
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);

//        Tell the cursor what URI to watch,
//        so it knows when its source data changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;

        switch (URIMatcher.match(uri)) {
            case HYMNS:
                count = db.update(HymnContract.HymnEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case HYMNS_ID:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(HymnContract.HymnEntry.TABLE_NAME, values, HymnContract.HymnEntry._ID + "=" + rowId
                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
