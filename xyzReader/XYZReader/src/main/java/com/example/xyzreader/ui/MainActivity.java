package com.example.xyzreader.ui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.R;

/**
 * Basic container that houses fragments and keeps track of the current article viewed through
 * {@code sCurrentPosition}
 */
public class MainActivity extends AppCompatActivity {

    //public static Cursor sCursor;
    public static int sCurrentPosition = 0;
    public static long sCurrentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_fragment_main, new ArticleListFragment(),
                        ArticleListFragment.class.getSimpleName())
                .commit();
    }
}
