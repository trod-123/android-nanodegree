package com.zn.expirytracker;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import timber.log.Timber;

public class ExpiryTrackerApplication extends MultiDexApplication {

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        // If we can't extend MultiDexApplication class, run this instead
//        MultiDex.install(this);
//    }

    boolean DEBUG_ENABLE_FIREBASE_DATABASE_DEEP_LOGGING = false;


    @Override
    public void onCreate() {
        super.onCreate();

        // Persists cached data across app restarts. Needs to be called before getting first
        // database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            if (DEBUG_ENABLE_FIREBASE_DATABASE_DEEP_LOGGING) {
                database.setLogLevel(Logger.Level.DEBUG);
            }
        } else {
            // TODO: Initialize a crash reporting tree
            // https://github.com/JakeWharton/timber/blob/master/sample/src/main/java/com/example/timber/ExampleApp.java
        }
    }
}
