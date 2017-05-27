package com.example.koresuniku.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.koresuniku.popularmovies.settings.SettingsActivity;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_container);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new DetailFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        if ((id == R.id.action_favourites) || (id == R.id.action_favourites_checked)) {
            startActivity(new Intent(getApplicationContext(), FavouritesActivity.class));
        }
        if (id == android.R.id.home) {
            onBackPressed();
            onStop();
        }

        return true;
    }
}
