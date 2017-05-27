package com.example.koresuniku.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.koresuniku.popularmovies.database.DatabaseContract;
import com.example.koresuniku.popularmovies.database.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {
    private GridView gridView;
    private String[] columns = {
            DatabaseContract.DatabaseEntry.COLUMN_IMAGE_URL,
            DatabaseContract.DatabaseEntry.COLUMN_JSON_MOVIE
    };
    private String[] urlImagePaths;
    private String[] JSONStrings;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase database;

    private List<String> listImages;
    private List<String> listJSONs;

    @Override
    protected void onStart() {
        super.onStart();
        getUrlImagePathsFromDatabase();
        getJSONStringsFromDatabase();
        //database.close();
        gridView = (GridView) findViewById(R.id.grid_favourites);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(2);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
        } else gridView.setNumColumns(2);

        listImages = new ArrayList<>();
        Collections.addAll(listImages, urlImagePaths);
        ImageAdapter adapter = new ImageAdapter(getApplicationContext(), listImages);
        gridView.setAdapter(adapter);

        listJSONs = new ArrayList<>();
        Collections.addAll(listJSONs, JSONStrings);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.jsonMovie = JSONStrings[position];
                Log.v("On jsonMovie change", MainActivity.jsonMovie);
                startActivity(new Intent(getApplicationContext(), DetailActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mDbHelper = new DatabaseHelper(getApplicationContext());
        database = mDbHelper.getReadableDatabase();

        getUrlImagePathsFromDatabase();
        getJSONStringsFromDatabase();




    }

    private void getUrlImagePathsFromDatabase() {
        Cursor cursor = database.query(
                DatabaseContract.DatabaseEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        //cursor.moveToFirst();
        urlImagePaths = new String[cursor.getCount()];
        int counter = 0;
        Log.v("Cursor rows number", String.valueOf(cursor.getCount()));

            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_IMAGE_URL);
                String url = cursor.getString(index);
                Log.v("Url", String.valueOf(url));
                //if (index != 0) {
                    //list.add(url);
                    //Log.v("My tag first", list.toString());
                //}
                urlImagePaths[counter] = url;
                Log.v("My tag first", String.valueOf(urlImagePaths[counter]));
                counter++;
            }
            cursor.close();

    }

    private void getJSONStringsFromDatabase() {
        Cursor cursor = database.query(
                DatabaseContract.DatabaseEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );


        JSONStrings = new String[cursor.getCount()];
        int counter = 0;
        Log.v("Cursor rows number", String.valueOf(cursor.getCount()));

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(DatabaseContract.DatabaseEntry.COLUMN_JSON_MOVIE);
            String rawJSON = cursor.getString(index);
            Log.v("RawJSON", String.valueOf(rawJSON));
            //if (index != 0) {
            //list.add(url);
            //Log.v("My tag first", list.toString());
            //}
            JSONStrings[counter] = rawJSON;
            Log.v("My tag second", String.valueOf(JSONStrings[counter]));
            counter++;
        }
        cursor.close();
    }

    private class ImageAdapter extends BaseAdapter {
        Context context;
        List<String> imageRes;

        public ImageAdapter(Context context, List<String> imageRes) {
            this.context = context;
            this.imageRes = imageRes;
        }

        @Override
        public int getCount() {
            return imageRes.size();
        }

        @Override
        public Object getItem(int position) {
            return imageRes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    gridView.setNumColumns(2);
                    imageView.setLayoutParams(new GridView.LayoutParams(385, 560));
                } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    gridView.setNumColumns(3);
                    imageView.setLayoutParams(new GridView.LayoutParams(400, 580));
                }
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            Picasso.with(context).load(imageRes.get(position)).into(imageView);
            imageView.setId(position);
            return imageView;
        }


    }
}
