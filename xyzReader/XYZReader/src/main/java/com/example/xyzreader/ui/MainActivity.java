package com.example.xyzreader.ui;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.R;

/**
 * Basic container that houses fragments and keeps track of the current article viewed through
 * {@code sCurrentPosition}
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_POSITION = "com.example.xyzreader.currentPosition";
    private static final String KEY_CURRENT_ID = "com.example.xyzreader.currentId";

    private static final int DEFAULT_POSITION = 0;
    private static final long DEFAULT_ID = 0;

    public static Cursor sCursor;
    public static int sCurrentPosition = 0;
    public static long sCurrentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            sCurrentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, DEFAULT_POSITION);
            sCurrentId = savedInstanceState.getLong(KEY_CURRENT_ID, DEFAULT_ID);
            // To retain current fragment it is necessary to return here to prevent additional
            // detail fragments when changing orientation
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_fragment_main, new ArticleListFragment(),
                        ArticleListFragment.class.getSimpleName())
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, sCurrentPosition);
        outState.putLong(KEY_CURRENT_ID, sCurrentId);
    }
}
