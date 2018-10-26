package com.zn.expirytracker;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.leakcanary.LeakCanary;
import com.zn.expirytracker.utils.DebugFields;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import androidx.multidex.MultiDexApplication;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class ExpiryTrackerApplication extends MultiDexApplication {

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        // If we can't extend MultiDexApplication class, run this instead
//        MultiDex.install(this);
//    }

    private boolean mEnableCrashAnalyticsReporting = true;

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
            // TODO: Test in release build to make sure this is working properly
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            // Only enable Crashlytics reporting based on user preferences. True by default
            if (mEnableCrashAnalyticsReporting = sp.getBoolean(
                    getString(R.string.pref_about_crashlytics_optin_key), true)) {
                Fabric.with(this, new Crashlytics());
                Crashlytics.setUserIdentifier(FirebaseInstanceId.getInstance().getId());
            }
            Timber.plant(new CrashReportingTree());
        }
    }

    /**
     * Handles crashes by reporting Warnings, Errors, and Exceptions to Crashlytics
     */
    private class CrashReportingTree extends Timber.Tree {
        private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
        private static final String CRASHLYTICS_KEY_TAG = "tag";
        private static final String CRASHLYTICS_KEY_MESSAGE = "message";

        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message,
                           @Nullable Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            if (t == null) {
                t = new Exception(message);
            }

            // Crashlytics
            if (mEnableCrashAnalyticsReporting) {
                Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
                Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
                Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);
                Crashlytics.logException(t);
            }
        }
    }
}
