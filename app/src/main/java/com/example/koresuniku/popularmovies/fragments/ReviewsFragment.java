package com.example.koresuniku.popularmovies.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.koresuniku.popularmovies.CommentActivity;
import com.example.koresuniku.popularmovies.DetailFragment;
import com.example.koresuniku.popularmovies.R;

import java.util.List;

public class ReviewsFragment extends Fragment {
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reviews_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.lv_comment);
        final List<String> values = DetailFragment.reviews;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.comment_item, R.id.comment_text, values);

        listView.setAdapter(adapter);
        DetailFragment.commentCounter = listView.getCount();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, values.get(position));
                startActivity(intent);
            }
        });

        setListViewHeightBasedOnChildren(listView);
        if (listView.getCount() == 0) {
            DetailFragment.disableReviews(true);
        }

        return rootView;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight  + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
