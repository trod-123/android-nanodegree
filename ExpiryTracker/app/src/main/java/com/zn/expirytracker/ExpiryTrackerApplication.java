package com.zn.expirytracker;

import android.os.StrictMode;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.zn.expirytracker.utils.DebugFields;

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
        if (BuildConfig.DEBUG && DebugFields.ENABLE_MEMORY_LEAKS_DETECTION) {
            // Needs to be checked before any other application code is run
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }

        // Persists cached data across app restarts. Needs to be called before getting first
        // database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            if (DebugFields.ENABLE_FIREBASE_DATABASE_DEEP_LOGGING) {
                database.setLogLevel(Logger.Level.DEBUG);
            }
            if (DebugFields.ENABLE_STRICT_MODE) {
                // https://developer.android.com/docs/quality-guidelines/core-app-quality#strictmode
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()   // or .detectAll() for all detectable problems
                        .penaltyLog()
                        .penaltyFlashScreen()
                        .build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
            }
        } else {
            // TODO: Initialize a crash reporting tree
            // https://github.com/JakeWharton/timber/blob/master/sample/src/main/java/com/example/timber/ExampleApp.java
        }
    }
}
