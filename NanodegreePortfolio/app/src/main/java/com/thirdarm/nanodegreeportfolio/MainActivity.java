/*
 * Copyright (C) 2015 Teddy Rodriguez
 */

package com.thirdarm.nanodegreeportfolio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Here be settings", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a toast containing the name of an app specified by @position
     * @param position the index of the app in the portfolio (from 0 to 5)
     */
    private void getToast(int position) {
        assert position >= 0 && position <= 5;
        String app = "none";

        switch (position) {
            case 0:
                app = getString(R.string.portfolio_app1);
                break;
            case 1:
                app = getString(R.string.portfolio_app2);
                break;
            case 2:
                app = getString(R.string.portfolio_app3);
                break;
            case 3:
                app = getString(R.string.portfolio_app4);
                break;
            case 4:
                app = getString(R.string.portfolio_app5);
                break;
            case 5:
                app = getString(R.string.portfolio_app6);
                break;
        }

        // Checks if "App" is already contained in the resource string and omits "App" repetitions
        //  in shown toast
        if (app.toLowerCase().contains("app")) {
            Toast.makeText(this, "Launches my " + app + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Launches my " + app + " App!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Launches app 1: Popular Movies
     */
    public void launchMoviesApp(View view) {
        getToast(0);
    }

    /**
     * Launches app 2: Scores App
     */
    public void launchScoresApp(View view) {
        getToast(1);
    }

    /**
     * Launches app 3: Library App
     */
    public void launchLibraryApp(View view) {
        getToast(2);
    }

    /**
     * Launches app 4: Build it Bigger
     */
    public void launchBuildItBigger(View view) {
        getToast(3);
    }

    /**
     * Launches app 5: XYZ Reader
     */
    public void launchXYZ(View view) {
        getToast(4);
    }

    /**
     * Launches capstone app
     */
    public void launchCapstone(View view) {
        getToast(5);
    }
}
