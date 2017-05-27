package com.example.koresuniku.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.koresuniku.popularmovies.database.DatabaseContract;
import com.example.koresuniku.popularmovies.database.DatabaseHelper;
import com.example.koresuniku.popularmovies.settings.SettingsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GridView gridView;
    String message;
    String[] sendJSONMovie;
    ImageView[] sendImages;
    static String jsonMovie;
    static List<String> imageUrls;
    static String imageUrl;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.newicon);

        if (findViewById(R.id.fragment_main) != null) {
            mTwoPane = true;

        } else {
            mTwoPane = false;
        }
        int orientation = getResources().getConfiguration().orientation;

        gridView = (GridView) findViewById(R.id.grid_view);

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(2);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(3);
        } else gridView.setNumColumns(2);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String preference = sharedPreferences.getString(getString(R.string.pref_key), getString(R.string.most_popular_value_label));
        if(preference.equals(getString(R.string.most_popular_value_label))) {
            FetchImage fi1 = new FetchImage();
            fi1.execute("popular");
        }
        if(preference.equals(getString(R.string.top_rated_value_label))) {
            FetchImage fi2 = new FetchImage();
            fi2.execute("top_rated");
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mTwoPane) {
                    Intent intent = new Intent(getApplicationContext(), DetailFragment.class);
                    jsonMovie = sendJSONMovie[position];

                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_main, new DetailFragment())
                            .commit();
                } else {

                    Intent sendJSON = new Intent(getApplicationContext(), DetailActivity.class);
                    jsonMovie = sendJSONMovie[position];
                    imageUrl = imageUrls.get(position);
                    Log.v("My tag", imageUrl);
//                Bitmap bmp = sendImages[position].getDrawingCache();
//                sendJSON.putExtra("Image", bmp);
                    startActivity(sendJSON);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }


        if (id == R.id.action_favourites) {
            startActivity(new Intent(getApplicationContext(), FavouritesActivity.class));
        }

        if(id == R.id.action_clear) {
            DatabaseHelper mDbHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            int count = database.delete(
                    DatabaseContract.DatabaseEntry.TABLE_NAME,
                    "1",
                    null
            );
            Log.v("On database cleared", count + " rows cleared");

            Toast toast = Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_SHORT);
            toast.show();
        }

        return true;
    }

    class ImageAdapter extends BaseAdapter {
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
            if(convertView == null) {
                imageView = new ImageView(context);

                ////////////////////////////
                int orientation = getResources().getConfiguration().orientation;

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                float yInches= metrics.heightPixels/metrics.ydpi;
                float xInches= metrics.widthPixels/metrics.xdpi;
                double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
                if (diagonalInches >= 6.5) {
                    // 6.5inch device or bigger
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        gridView.setNumColumns(2);
                        GridView.LayoutParams params = new GridView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        //imageView.setLayoutParams(new GridView.LayoutParams(300, 460));
                        imageView.setLayoutParams(params);
                        imageView.setAdjustViewBounds(true);
                    } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        gridView.setNumColumns(2);
                        GridView.LayoutParams params = new GridView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        //imageView.setLayoutParams(new GridView.LayoutParams(260, 400));
                        imageView.setLayoutParams(params);
                        imageView.setAdjustViewBounds(true);
                    }
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    // smaller device
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        gridView.setNumColumns(2);
                        GridView.LayoutParams params = new GridView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        //imageView.setLayoutParams(new GridView.LayoutParams(385, 560));
                        imageView.setLayoutParams(params);
                        imageView.setAdjustViewBounds(true);
                    } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        gridView.setNumColumns(3);
                        GridView.LayoutParams params = new GridView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        imageView.setLayoutParams(params);
                        imageView.setAdjustViewBounds(true);
                        //imageView.setLayoutParams(new GridView.LayoutParams(400, 580));
                    }
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

            } else {
                imageView = (ImageView) convertView;
            }
            Picasso.with(context).load(imageRes.get(position)).into(imageView);
            sendImages[position] = imageView;
            imageView.setId(position);
            return imageView;
        }
    }

    class FetchImage extends AsyncTask<String, Void, String[]> {
        String[] localJSONMovie;

        private String[] getPathsFromJSON(String JSONString) throws JSONException {
            if(JSONString == null) {
                String[] result = new String[1];
                result[0] = "http://image.tmdb.org/t/p/w185//T3LrH6bnV74llVbFpQsCBrGaU9.jpg";
                return result;
            }
            final String posterPath = "poster_path";
            message += JSONString;
            JSONObject moviesJSON = new JSONObject(JSONString);
            JSONArray moviesArray = moviesJSON.getJSONArray("results");
            String[] results = new String[moviesArray.length()];
            localJSONMovie = new String[moviesArray.length()];
            sendImages = new ImageView[moviesArray.length()];

            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject indexedObject = moviesArray.getJSONObject(i);
                results[i] = indexedObject.getString(posterPath);
                message += indexedObject.getString(posterPath);
                localJSONMovie[i] = indexedObject.toString();
            }

            return results;
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;
            String rawJSONString = null;
            final String LOG_TAG = FetchImage.class.getSimpleName();

            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/" + params[0] + "?api_key=6bb8537d0d984dc527501e553f966741");
                //Log.v(LOG_TAG, url.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                //Log.v(LOG_TAG, buffer.toString());
                rawJSONString = buffer.toString();

                return getPathsFromJSON(rawJSONString);
            } catch (IOException e) {

            } catch (JSONException e) {

            } finally {
                if(httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {}
                }
            }
            return null;
        }

        protected void onPostExecute(String[] result) {
            List<String> urls = new ArrayList<>();

            if(result != null) {
                for (String path : result) {
                    urls.add("http://image.tmdb.org/t/p/w185/" + path);
                }
            }

            ImageAdapter adp = new ImageAdapter(getApplicationContext(), urls);
            imageUrls = urls;
            gridView.setAdapter(adp);
            sendJSONMovie = localJSONMovie;
        }
    }
}
