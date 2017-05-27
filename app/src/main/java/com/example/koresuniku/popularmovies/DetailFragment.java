package com.example.koresuniku.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.koresuniku.popularmovies.asynktasks.FetchYoutubeQueryTask;
import com.example.koresuniku.popularmovies.database.DatabaseContract;
import com.example.koresuniku.popularmovies.database.DatabaseHelper;
import com.example.koresuniku.popularmovies.fragments.ListViewFragment;
import com.example.koresuniku.popularmovies.fragments.ReviewsFragment;
import com.example.koresuniku.popularmovies.settings.SettingsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    String posterPath;
    String originalTitle;
    String overView;
    String ratingString;
    String releaseDate;
    String url;
    String runtimeString;
    public static String[] titles;
    public static String[] youtubeQueries;
    public static List<String> reviews;
    public static String movie;
    int movieId;
    public static int commentCounter;

    ImageView thumbnail;
    Button mark;
    TextView title;
    TextView overview;
    TextView rating;
    TextView release;
    TextView runtime;
    public static TextView reviewText;
    public static TextView trailersText;
    LinearLayout containerFrame;
    ScrollView scrollView;
    LinearLayout linearLayout;
    LinearLayout containerComments;

    DatabaseHelper mDbHelper;
    SQLiteDatabase database;
    boolean movieExistsInDatabase;

    String[] columns = {DatabaseContract.DatabaseEntry.COLUNM_NAME_TITLE};

    View rootView;

    @Override
    public void onStart() {
        super.onStart();
        try {
            getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mark = (Button) rootView.findViewById(R.id.make_favourite_button);
        checkIfExists();

    }

//    @Override
//    protected void onRestart() {
//        super.onStart();
//        try {
//            getData();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mark = (Button) rootView.findViewById(R.id.make_favourite_button);
//        checkIfExists();
//
//    }

//    protected void onResume() {
//        super.onResume();
//        mark = (Button) findViewById(R.id.make_favourite_button);
//        checkIfExists();
//    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_detail, container, false);
        mDbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        database = mDbHelper.getWritableDatabase();


        try {
            getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        thumbnail = (ImageView) rootView.findViewById(R.id.thumbnail);
        title = (TextView) rootView.findViewById(R.id.original_title);
        overview = (TextView) rootView.findViewById(R.id.overview);
        rating = (TextView) rootView.findViewById(R.id.rating);
        release = (TextView) rootView.findViewById(R.id.release);
        runtime = (TextView) rootView.findViewById(R.id.runtime);
        containerFrame = (LinearLayout) rootView.findViewById(R.id.container_frame);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.linear_main);
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_main);
        containerComments = (LinearLayout) rootView.findViewById(R.id.container_comments);
        reviewText = (TextView) rootView.findViewById(R.id.reviews_text);
        trailersText = (TextView) rootView.findViewById(R.id.trailers_text);
        mark = (Button) rootView.findViewById(R.id.make_favourite_button);
        checkIfExists();

        url = "http://image.tmdb.org/t/p/w185/" + posterPath;
        title.setText(originalTitle);
        overview.setText(overView);
        rating.setText(ratingString);
        release.setText(releaseDate);
        SetImage setImage = new SetImage();
        setImage.execute(url);





        mark.setOnClickListener(buttonOnClickListener);

        FetchTrailersNamesTask ftmt = new FetchTrailersNamesTask();
        ftmt.execute(movieId);

        return rootView;
    }


    private void checkIfExists() {
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
        if (cursor.getCount() == 0) {
            Log.v("Database rows", " does not exist");
        }
        Log.v("Database rows", String.valueOf(cursor.getCount()));
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(DatabaseContract.DatabaseEntry.COLUNM_NAME_TITLE);
            String name = cursor.getString(index);
            Log.v("Movie in database", name);
            movieExistsInDatabase = false;
            if (name.equals(originalTitle)) {
                movieExistsInDatabase = true;
                break;
            }
        }

        if (movieExistsInDatabase) {
            mark.setText("Delete from favourites");
            mark.setTextColor(Color.GRAY);
        } else {
            mark.setText("Add to favourites");
            mark.setTextColor(Color.BLACK);
        }

        Log.v("Movie exists in database", String.valueOf(movieExistsInDatabase));

        cursor.close();
    }

    View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!movieExistsInDatabase) {
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.DatabaseEntry.COLUNM_NAME_TITLE, originalTitle);
                values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ENTRY_ID, movieId);
                values.put(DatabaseContract.DatabaseEntry.COLUMN_JSON_MOVIE, movie);
                values.put(DatabaseContract.DatabaseEntry.COLUMN_IMAGE_URL, MainActivity.imageUrl);
                long rowId = database.insert(
                        DatabaseContract.DatabaseEntry.TABLE_NAME,
                        null,
                        values
                );
                Log.v("On added", String.valueOf(rowId));
                mark.setText("Delete from favourites");
                mark.setTextColor(Color.GRAY);
                movieExistsInDatabase = true;

            } else {
                String selection = DatabaseContract.DatabaseEntry.COLUNM_NAME_TITLE
                        + " = \"" + originalTitle + "\"";
                database.delete(DatabaseContract.DatabaseEntry.TABLE_NAME, selection, null);
                mark.setText("Add to favourites");
                mark.setTextColor(Color.BLACK);

                movieExistsInDatabase = false;
            }


        }
    };



    public void getData() throws JSONException {

        movie = MainActivity.jsonMovie;
        JSONObject movie = new JSONObject(DetailFragment.movie);
        posterPath = movie.getString("poster_path");
        originalTitle = movie.getString("original_title");
        overView = movie.getString("overview");

        float ratingFloat = Float.parseFloat(movie.getString("vote_average"));
        ratingFloat *= 10;
        ratingFloat = Math.round(ratingFloat);
        ratingFloat /= 10;
        ratingString = String.valueOf(ratingFloat) + "/10";
        releaseDate = movie.getString("release_date").substring(0, 4);
        movieId = movie.getInt("id");

    }

    private class SetImage extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"
                        + movieId + "?api_key=6bb8537d0d984dc527501e553f966741");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                String rawJSON = stringBuilder.toString();
                //Log.v("Got JSON", rawJSON);
                //Log.v("From", String.valueOf(url));

                JSONObject main = new JSONObject(rawJSON);
                String runtime = main.getString("runtime");

                return new String[] {params[0], runtime};
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String[] results) {
            Picasso.with(getActivity().getApplicationContext()).load(results[0]).into(thumbnail);
            runtimeString = results[1];
            //Log.v("Got runtime", runtimeString);
            runtime.setText(runtimeString + "min");
        }
    }

    private class FetchTrailersNamesTask extends AsyncTask<Integer, Void, String[]> {
        private String LOG_TAG = FetchTrailersNamesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Integer... params) {
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"
                        + params[0] + "/videos" + "?api_key=6bb8537d0d984dc527501e553f966741");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                StringBuilder builder = new StringBuilder();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = buffer.readLine()) != null) {
                    builder.append(line);
                }

                String rawJSON = builder.toString();
                //Log.v(LOG_TAG, rawJSON);

                String[] result = getNamesArray(rawJSON);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] results) {
            //Log.v(LOG_TAG, String.valueOf(results.length));
            for(int i = 0; i < results.length; i++) {
                //Log.v(LOG_TAG, results[i]);
            }
            titles = results;
            Intent intent = new Intent(getActivity().getApplicationContext(), ListViewFragment.class);
            intent.putExtra(Intent.EXTRA_TEXT, results);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_frame, new ListViewFragment())
                    .commit();

            //Log.v("Height", String.valueOf(containerFrame.getHeight()));
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            float yInches= metrics.heightPixels/metrics.ydpi;
            float xInches= metrics.widthPixels/metrics.xdpi;
            double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
            if (diagonalInches >= 6.5) {
                containerFrame.setMinimumHeight((titles.length * 60) + titles.length);
            } else {
                containerFrame.setMinimumHeight((titles.length * 120) + titles.length);
            }

            FetchYoutubeQueryTask fyqt = new FetchYoutubeQueryTask();
            fyqt.execute(movieId);

            FetchReviewsTask frt = new FetchReviewsTask();
            frt.execute(movieId);
        }

        private String[] getNamesArray(String rawJSON) {
            List<String> list = new ArrayList<>();
            try {
                JSONObject main = new JSONObject(rawJSON);
                JSONArray jsonArray = main.getJSONArray("results");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String name = item.getString("name");
                    list.add(name);
                }
                String[] result = new String[list.size()];
                for(int v = 0; v < result.length; v++) {
                    result[v] = list.get(v);
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class FetchReviewsTask extends AsyncTask<Integer, Void, List<String>> {
        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
        @Override
        protected List<String> doInBackground(Integer... params) {
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"
                        + params[0] + "/reviews" + "?api_key=6bb8537d0d984dc527501e553f966741");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String rawJSON = builder.toString();
                //Log.v(LOG_TAG, url.toString());

                List<String> result = reviewFromJSON(rawJSON);
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private List<String> reviewFromJSON(String rawJSON) {
            try {
                JSONObject main = new JSONObject(rawJSON);
                JSONArray array = main.getJSONArray("results");
                List<String> list = new ArrayList<>();
                for(int i = 0; i < array.length();i ++) {
                    JSONObject item = array.getJSONObject(i);
                    list.add(item.getString("content"));
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            DetailFragment.reviews = result;
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_comments, new ReviewsFragment())
                    .commit();

        }
    }

    public static void disableReviews(boolean thereAre) {
        if (thereAre) {
            reviewText.setVisibility(View.GONE);
        }
    }

    public static void disableTrailers(boolean thereAre) {
        if (thereAre) {
            trailersText.setVisibility(View.GONE);
        }
    }

}
