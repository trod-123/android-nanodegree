package com.zn.expirytracker;

import android.support.multidex.MultiDexApplication;

import com.zn.expirytracker.data.TestDataGen;

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
            // Generate random data. Accessible anywhere in the app
            TestDataGen.generateInstance(TestDataGen.DEFAULT_NUM_CHART_ENTRIES,
                    TestDataGen.DEFAULT_NUM_FOOD_DATA, TestDataGen.DEFAULT_DATE_BOUNDS,
                    TestDataGen.DEFAULT_COUNT_BOUNDS);
        } else {
            // TODO: Initialize a crash reporting tree
            // https://github.com/JakeWharton/timber/blob/master/sample/src/main/java/com/example/timber/ExampleApp.java
        }
    }
}
