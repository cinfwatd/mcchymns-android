package com.bitrient.mcchymns.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class HymnDbHelper extends SQLiteOpenHelper{
    private static final String TAG = "DB_HELPER";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MCCHymns.db";

    private Context mContext;
    private static HymnDbHelper dbHelper;

    private HymnDbHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
        mContext = context;
    }

    public static HymnDbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new HymnDbHelper(context);
            return dbHelper;
        } else {
            return dbHelper;
        }
    }

    /**
     * Called when the database is created for the first time.
     * This is where the creation of the tables and the initial population of the tables
     * should happen
     *
     * @param db SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HymnContract.SubjectEntry.SQL_CREATE_ENTRIES);
        db.execSQL(HymnContract.TopicEntry.SQL_CREATE_ENTRIES);
        db.execSQL(HymnContract.HymnEntry.SQL_CREATE_ENTRIES);
        db.execSQL(HymnContract.StanzaEntry.SQL_CREATE_ENTRIES);
        db.execSQL(HymnContract.ChorusEntry.SQL_CREATE_ENTRIES);

        //TODO: pre-populate the db here.
    }

    /**
     * Deletes every database table and recreates them.
     * This would be modified later.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HymnContract.ChorusEntry.SQL_DELETE_ENTRIES);
        db.execSQL(HymnContract.StanzaEntry.SQL_DELETE_ENTRIES);
        db.execSQL(HymnContract.HymnEntry.SQL_DELETE_ENTRIES);
        db.execSQL(HymnContract.TopicEntry.SQL_DELETE_ENTRIES);
        db.execSQL(HymnContract.SubjectEntry.SQL_DELETE_ENTRIES);

        onCreate(db);
    }

    /**
     * Deletes every database table and recreates them.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onUpgrade(db, oldVersion, newVersion);
    }
}
