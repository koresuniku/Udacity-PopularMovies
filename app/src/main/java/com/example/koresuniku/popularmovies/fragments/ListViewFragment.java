package com.example.koresuniku.popularmovies.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.koresuniku.popularmovies.CustomAdapter;
import com.example.koresuniku.popularmovies.DetailFragment;
import com.example.koresuniku.popularmovies.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListViewFragment extends Fragment {
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.listview_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.lv);

        String[] array = DetailFragment.titles;

        List<String> values = new ArrayList<>(Arrays.asList(array));

        //Log.v("ListViewFragment", values.toString());
        CustomAdapter adapter = new CustomAdapter(getActivity(), values);
        listView.setAdapter(adapter);

        int height = listView.getHeight();
        //Log.v("LV Height", String.valueOf(height));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    URL url = new URL(DetailFragment.youtubeQueries[position]);
                    intent.setData(Uri.parse(String.valueOf(url)));
                    startActivity(intent);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });

        if (listView.getCount() == 0) {
            DetailFragment.disableTrailers(true);
        }
        return rootView;
    }
}
