package com.zn.baking;

import android.support.multidex.MultiDexApplication;

import timber.log.Timber;

public class BakingApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
