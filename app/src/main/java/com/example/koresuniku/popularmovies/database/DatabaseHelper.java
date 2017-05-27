package com.example.koresuniku.popularmovies.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public final static int DATABASE_VARSION = 2;
    public final static String DATABASE_NAME = "favourites.db";

    private final static String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS" + DatabaseContract.DatabaseEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VARSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DatabaseContract.DatabaseEntry.TABLE_NAME
                + " (" + DatabaseContract.DatabaseEntry.COLUMN_NAME_ENTRY_ID + " INTEGER, "
                + DatabaseContract.DatabaseEntry.COLUNM_NAME_TITLE + " TEXT, "
                + DatabaseContract.DatabaseEntry.COLUMN_JSON_MOVIE + " TEXT, "
                + DatabaseContract.DatabaseEntry.COLUMN_IMAGE_URL + " TEXT);";
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
