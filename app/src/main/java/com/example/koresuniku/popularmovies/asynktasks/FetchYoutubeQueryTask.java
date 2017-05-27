package com.example.koresuniku.popularmovies.asynktasks;


import android.os.AsyncTask;

import com.example.koresuniku.popularmovies.DetailFragment;

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

public class FetchYoutubeQueryTask extends AsyncTask<Integer, Void, String[]> {
    private final static String LOG_TAG = FetchYoutubeQueryTask.class.getSimpleName();
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

            String[] result = youtubeQueriesForTrailer(rawJSON);
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
        DetailFragment.youtubeQueries = results;
    }

    private String[] youtubeQueriesForTrailer(String rawJSON) {
        List<String> list = new ArrayList<>();
        try {
            JSONObject main = new JSONObject(rawJSON);
            JSONArray jsonArray = main.getJSONArray("results");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String key = item.getString("key");
               list.add("https://www.youtube.com/watch?v=" + key);
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
