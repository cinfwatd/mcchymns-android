package com.bitrient.mcchymns.database;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class HymnDbHelper extends SQLiteAssetHelper{
    @SuppressWarnings("unused")
    private static final String TAG = HymnDbHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mcchymns.db";

    private static HymnDbHelper dbHelper;

    private HymnDbHelper(Context context) {
        super(context, DATABASE_NAME + ".zip", null, DATABASE_VERSION);
    }

    public static HymnDbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new HymnDbHelper(context);
            return dbHelper;
        } else {
            return dbHelper;
        }
    }
}
