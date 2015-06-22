package com.bitrient.mcchymns.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bitrient.mcchymns.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLDataException;
import java.sql.SQLException;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public class HymnDbHelper extends SQLiteOpenHelper{
    @SuppressWarnings("unused")
    private static final String TAG = HymnDbHelper.class.getSimpleName();

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
//        db.execSQL(HymnContract.SubjectEntry.SQL_CREATE_ENTRIES);
        db.execSQL(HymnContract.TopicEntry.SQL_CREATE_ENTRIES);
//        db.execSQL(HymnContract.HymnEntry.SQL_CREATE_ENTRIES);
        db.execSQL(HymnContract.StanzaEntry.SQL_CREATE_ENTRIES);
        db.execSQL(HymnContract.ChorusEntry.SQL_CREATE_ENTRIES);

        //TODO: pre-populate the db here.

//        insertSubjects(db, HymnContract.SubjectEntry.TABLE_NAME);
        insertTopics(db, HymnContract.TopicEntry.TABLE_NAME);
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
//        db.execSQL(HymnContract.HymnEntry.SQL_DELETE_ENTRIES);
        db.execSQL(HymnContract.TopicEntry.SQL_DELETE_ENTRIES);
//        db.execSQL(HymnContract.SubjectEntry.SQL_DELETE_ENTRIES);

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

    private String loadJson(String table) {
        String json;

        InputStream inputStream = null;

        switch (table) {
            case HymnContract.SubjectEntry.TABLE_NAME:
                inputStream = mContext.getResources().openRawResource(R.raw.subject);
                break;
            case HymnContract.TopicEntry.TABLE_NAME:
                inputStream = mContext.getResources().openRawResource(R.raw.topic);
                break;
        }
        try {

            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return json;
    }

    private void insertSubjects(SQLiteDatabase db, String table) {
        String json = loadJson(table);

        try {
            JSONArray jsonArray = new JSONArray(json);


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ContentValues values = new ContentValues();
                values.put(HymnContract.SubjectEntry._ID, jsonObject.getInt("_id"));
                values.put(HymnContract.SubjectEntry.COLUMN_NAME_SUBJECT, jsonObject.getString("subject"));

                db.insert(HymnContract.SubjectEntry.TABLE_NAME, null, values);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertTopics(SQLiteDatabase db, String table) {
        String json = loadJson(table);

        try {
            JSONArray jsonArray = new JSONArray(json);


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ContentValues values = new ContentValues();
                values.put(HymnContract.TopicEntry._ID, jsonObject.getInt("_id"));
                values.put(HymnContract.TopicEntry.COLUMN_NAME_SUBJECT_ID, jsonObject.getString("subject_id"));
                values.put(HymnContract.TopicEntry.COLUMN_NAME_TOPIC, jsonObject.getString("topic"));

                db.insert(HymnContract.TopicEntry.TABLE_NAME, null, values);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
