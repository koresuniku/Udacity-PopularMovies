package com.example.koresuniku.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.koresuniku.popularmovies.fragments.ListViewFragment;

public class DebugActivity extends AppCompatActivity {
    ScrollView scrollView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_debug, new ListViewFragment())
                .commit();
    }
}
