package com.example.koresuniku.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CommentActivity extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.comment_detail);
        textView = (TextView) findViewById(R.id.textView_comment);

        Intent intent = getIntent();
        String comment = intent.getStringExtra(Intent.EXTRA_TEXT);
        textView.setText(comment);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            onStop();
        }
        return super.onOptionsItemSelected(item);
    }
}

