package com.bliszkot.schedulemeeting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ScheduleMeeting.db";

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "SCHEDULE_MEETING";
        public static final String COLUMN_NAME_DATE = "DATE";
        public static final String COLUMN_NAME_TIME = "TIME";
        public static final String COLUMN_NAME_MEETING_AGENDA = "MEETING_AGENDA";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedEntry.COLUMN_NAME_DATE + " TEXT UNIQUE," +
                    FeedEntry.COLUMN_NAME_TIME + " TEXT," +
                    FeedEntry.COLUMN_NAME_MEETING_AGENDA + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
