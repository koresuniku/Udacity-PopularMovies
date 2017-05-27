package com.example.koresuniku.popularmovies.database;


import android.provider.BaseColumns;

public class DatabaseContract {

    public DatabaseContract() {}

    public static abstract class DatabaseEntry implements BaseColumns {
        public final static String TABLE_NAME = "favourites";
        public final static String COLUMN_NAME_ENTRY_ID = "entryid";
        public final static String COLUNM_NAME_TITLE = "title";
        public final static String COLUMN_JSON_MOVIE = "jsonmovie";
        public final static String COLUMN_IMAGE_URL = "imageurl";
    }
}
