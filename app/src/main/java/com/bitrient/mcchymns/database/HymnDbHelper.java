package com.bitrient.mcchymns.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bitrient.mcchymns.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class HymnDbHelper extends SQLiteOpenHelper{
    @SuppressWarnings("unused")
    private static final String TAG = HymnDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "MCCHymns.db";

    private Context mContext;
    private static HymnDbHelper dbHelper;

    private HymnDbHelper(Context context) {
        super(context, DATABASE_NAME, null, context.getResources().getInteger(R.integer.database_version));
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
        db.execSQL(HymnContract.SQL_CREATE_HYMNS_VIEW);

        //TODO: pre-populate the db here.
        db.beginTransaction();

        int result = 0;
        InputStream insertStream = mContext.getResources().openRawResource(R.raw.mcchymns);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertStream));

        try {
            while (insertReader.ready()) {
                String insertStatement = insertReader.readLine();
                db.execSQL(insertStatement);

                result++;
            }

            insertReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
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
        db.execSQL(HymnContract.SQL_DELETE_HYMNS_VIEW);
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
