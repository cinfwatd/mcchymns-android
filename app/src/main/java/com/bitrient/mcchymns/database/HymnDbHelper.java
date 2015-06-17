package com.bitrient.mcchymns.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

//        try {
//            insertHymn(db, downloadData(HymnContract.HymnEntry.TABLE_NAME));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private void insertHymn(SQLiteDatabase db, JSONArray jsonArray) throws JSONException {
//        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            ContentValues values = new ContentValues();
            values.put(HymnContract.HymnEntry._ID, jsonObject.getInt("_id"));
            values.put(HymnContract.HymnEntry.COLUMN_NAME_HYMN_NUMBER, jsonObject.getInt("number"));
            values.put(HymnContract.HymnEntry.COLUMN_NAME_FIRST_LINE, jsonObject.getString("first_line"));
            values.put(HymnContract.HymnEntry.COLUMN_NAME_TOPIC_ID, jsonObject.getInt("topic_id"));

            //TODO: fetch other details: author, year, story, sacred_songs, new_hymns, christian_choir
            db.insert(HymnContract.HymnEntry.TABLE_NAME, HymnContract.HymnEntry.COLUMN_NAME_CREATION_STORY, values);
        }
    }

    private JSONArray downloadData(String tableName) throws IOException, SQLException, JSONException {
        if (isNetworkAvailable()) {
            InputStream inputStream;
            HttpURLConnection urlConnection;
            String requestUrl = HymnContract.BASE_URL + tableName;

            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

//        optional request header
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

//        Get request
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();

//        200 == HTTP OK
            if  (statusCode == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                return new JSONArray(convertInputStreamToString(inputStream));
            } else {
                throw new SQLDataException("Failed to fetch data!! - Database (Table - " + tableName + ")");
            }
        } else {
            throw new SQLDataException("Please enable your network.");
        }
    }

    private boolean isNetworkAvailable() {
        return true;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

//        close stream
        inputStream.close();

        return result;
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
