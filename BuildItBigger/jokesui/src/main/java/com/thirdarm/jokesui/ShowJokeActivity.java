package com.thirdarm.jokesui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class ShowJokeActivity extends AppCompatActivity {

    private static final String SCROLL_POSITION = "scroll-position";
    private ScrollView mContentScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_joke);
        mContentScrollView = (ScrollView) findViewById(R.id.scrollview_joke_content);

        TextView textview_main = (TextView) findViewById(R.id.textview_joke_main);
        TextView textview_content = (TextView) findViewById(R.id.textview_joke_content);
        TextView textview_sub = (TextView) findViewById(R.id.textview_joke_sub);

        // Extract and display the joke content
        Intent intent = getIntent();
        if (intent != null) {
            String main = intent.getStringExtra(JokesFragment.TITLE_TEXT);
            if (main != null && main.length() > 0 && textview_main != null) {
                textview_main.setText(main);
                textview_main.setGravity(Gravity.CENTER);
                setTitle(main);
            }
            String content = intent.getStringExtra(JokesFragment.CONTENT_TEXT);
            if (content != null && content.length() > 0 && textview_content != null) {
                textview_content.setText(content);
                textview_content.setGravity(Gravity.START);
            }
            String sub = intent.getStringExtra(JokesFragment.SUB_TEXT);
            if (sub != null && sub.length() > 0 && textview_sub != null) {
                textview_sub.setText(sub);
                textview_sub.setGravity(Gravity.END);
            }
        }

        // Enable back button programmatically (this library does not host the parent activity)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_POSITION, mContentScrollView.getVerticalScrollbarPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mContentScrollView.setVerticalScrollbarPosition(savedInstanceState.getInt(SCROLL_POSITION));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Return to the previous activity
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
