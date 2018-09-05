package com.zn.expirytracker;

import android.support.multidex.MultiDexApplication;

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

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO: Initialize a crash reporting tree
            // https://github.com/JakeWharton/timber/blob/master/sample/src/main/java/com/example/timber/ExampleApp.java
        }
    }
}
