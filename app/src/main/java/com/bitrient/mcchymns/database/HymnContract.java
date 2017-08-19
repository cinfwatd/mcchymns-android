package com.bitrient.mcchymns.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Cinfwat Probity <czprobity@bitrient.com>
 * @since 6/10/15
 */
public final class HymnContract {
    public HymnContract(){}

    public static final String AUTHORITY = "com.bitrient.mcchymns.provider.HymnsProvider";

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER ";
    public static final String SEPARATOR = ", ";
    public static final String BASE_URL = "http://mcchymns.bitrient.com/api/v1/";

    public static final String HYMNS_VIEW = "hymns_view";
    public static final String SQL_CREATE_HYMNS_VIEW = "CREATE VIEW " + HYMNS_VIEW +
            " AS SELECT h." + HymnEntry._ID + ", s." + StanzaEntry.COLUMN_NAME_HYMN_NUMBER +
            ", s." + StanzaEntry.COLUMN_NAME_STANZA_NUMBER + ", s." + StanzaEntry.COLUMN_NAME_STANZA +
            ", h." + HymnEntry.COLUMN_NAME_FAVOURITE + ", h." + HymnEntry.COLUMN_NAME_TOPIC_ID +

            ", t." + TopicEntry.COLUMN_NAME_TOPIC + ", c." + SubjectEntry.COLUMN_NAME_SUBJECT +

            " FROM " + StanzaEntry.TABLE_NAME + " AS s LEFT OUTER JOIN " + HymnEntry.TABLE_NAME + " AS h " +
            "ON s." + StanzaEntry.COLUMN_NAME_HYMN_NUMBER + " = h." + HymnEntry.COLUMN_NAME_HYMN_NUMBER +

            " LEFT JOIN " + TopicEntry.TABLE_NAME + " AS t ON h." + HymnEntry.COLUMN_NAME_TOPIC_ID + " = t." +
            TopicEntry._ID + " LEFT JOIN " + SubjectEntry.TABLE_NAME + " AS c ON t." + TopicEntry.COLUMN_NAME_SUBJECT_ID
            + " = c." + SubjectEntry._ID;


    public static final String SQL_DELETE_HYMNS_VIEW = "DROP VIEW " + HYMNS_VIEW;

    public static abstract class HymnEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/hymns");
        public static final Uri CONTENT_FTS_URI = Uri.parse("content://" + AUTHORITY + "/hymns_fts");
        public static final Uri CONTENT_FILTER_FTS_URI = Uri.parse("content://" + AUTHORITY + "/hymns/filter_fts");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.bitrient.hymn";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.bitrient.hymn";

        public static final String TABLE_NAME = "hymn";
        public static final String COLUMN_NAME_HYMN_NUMBER = "number";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_CREATION_DATE = "date";
        public static final String COLUMN_NAME_CREATION_STORY = "story";
        public static final String COLUMN_NAME_FAVOURITE = "favourite";
        public static final String COLUMN_NAME_FIRST_LINE = "first_line";
        public static final String COLUMN_NAME_TOPIC_ID = "topic_id";
        public static final String DEFAULT_SORT_ORDER = COLUMN_NAME_HYMN_NUMBER + " COLLATE LOCALIZED ASC";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        HymnEntry._ID + INTEGER_TYPE + "PRIMARY KEY AUTOINCREMENT" + SEPARATOR +
                        COLUMN_NAME_HYMN_NUMBER + INTEGER_TYPE +  " NOT NULL UNIQUE" + SEPARATOR +
                        COLUMN_NAME_FIRST_LINE + TEXT_TYPE + SEPARATOR +
                        COLUMN_NAME_AUTHOR + TEXT_TYPE + SEPARATOR +
                        COLUMN_NAME_CREATION_DATE + TEXT_TYPE + SEPARATOR +
                        COLUMN_NAME_CREATION_STORY + TEXT_TYPE + SEPARATOR +
                        COLUMN_NAME_FAVOURITE + TEXT_TYPE + SEPARATOR +
                        COLUMN_NAME_TOPIC_ID + INTEGER_TYPE + "REFERENCES " +
                        TopicEntry.TABLE_NAME + "(" + TopicEntry._ID + "))";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class StanzaEntry implements BaseColumns {
        public static final String TABLE_NAME = "stanza";
        public static final String COLUMN_NAME_STANZA_NUMBER = "stanza_number";
        public static final String COLUMN_NAME_HYMN_NUMBER = "hymn_number";
        public static final String COLUMN_NAME_STANZA = "stanza_";
        public static final String COLUMN_NAME_IS_CHORUS = "is_chorus";
        public static final String DEFAULT_SORT_ORDER = COLUMN_NAME_HYMN_NUMBER + " COLLATE LOCALIZED ASC";
        public static final String FIRST_LINES_SORT_ORDER = HymnEntry._ID + " COLLATE LOCALIZED ASC";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE VIRTUAL TABLE " + TABLE_NAME + " USING fts3 (" +
                        StanzaEntry._ID + INTEGER_TYPE + "PRIMARY KEY AUTOINCREMENT" + SEPARATOR +
                        COLUMN_NAME_STANZA_NUMBER + INTEGER_TYPE + SEPARATOR +
                        COLUMN_NAME_STANZA + TEXT_TYPE + SEPARATOR +
                        COLUMN_NAME_IS_CHORUS + INTEGER_TYPE + SEPARATOR +
                        COLUMN_NAME_HYMN_NUMBER + INTEGER_TYPE + "REFERENCES " +
                        HymnEntry.TABLE_NAME + "(" + HymnEntry.COLUMN_NAME_HYMN_NUMBER + "))";


        public static final String SQL_CREATE_ENTRIES_FST =
                "CREATE VIRTUAL TABLE " + TABLE_NAME + " USING fts3 (" +
                        StanzaEntry._ID + SEPARATOR + COLUMN_NAME_STANZA_NUMBER + SEPARATOR +
                        COLUMN_NAME_STANZA + SEPARATOR + COLUMN_NAME_IS_CHORUS + SEPARATOR +
                        COLUMN_NAME_HYMN_NUMBER + ")";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class SubjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "subject";
        public static final String COLUMN_NAME_SUBJECT = "subject";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        SubjectEntry._ID + INTEGER_TYPE + "PRIMARY KEY AUTOINCREMENT" + SEPARATOR +
                        COLUMN_NAME_SUBJECT + TEXT_TYPE + " )";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class TopicEntry implements BaseColumns {
        public static final String TABLE_NAME = "topic";
        public static final String COLUMN_NAME_TOPIC = "topic";
        public static final String COLUMN_NAME_SUBJECT_ID = "subject_id";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        TopicEntry._ID + INTEGER_TYPE + "PRIMARY KEY AUTOINCREMENT" + SEPARATOR +
                        COLUMN_NAME_TOPIC + TEXT_TYPE + SEPARATOR +
                        COLUMN_NAME_SUBJECT_ID + INTEGER_TYPE + "REFERENCES " +
                        SubjectEntry.TABLE_NAME + "(" + SubjectEntry._ID + "))";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

