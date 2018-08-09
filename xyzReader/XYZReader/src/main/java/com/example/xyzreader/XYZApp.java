package com.example.xyzreader;

import android.app.Application;

import timber.log.Timber;

public class XYZApp extends Application {

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
