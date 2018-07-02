package com.zn.baking;

import android.app.Application;

import timber.log.Timber;

public class BakingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
