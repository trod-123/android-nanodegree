package com.zn.expirytracker;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.zn.expirytracker.utils.AuthToolbox;

import timber.log.Timber;

public class ExpiryTrackerApplication extends MultiDexApplication {

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        // If we can't extend MultiDexApplication class, run this instead
//        MultiDex.install(this);
//    }


    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean loggedIn = sp.getBoolean(getString(R.string.pref_account_signed_in_key), false);
        AuthToolbox.signIn(this, loggedIn);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO: Initialize a crash reporting tree
            // https://github.com/JakeWharton/timber/blob/master/sample/src/main/java/com/example/timber/ExampleApp.java
        }
    }
}
