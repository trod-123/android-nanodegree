package com.thirdarm.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.database.ContentObserver;
import android.os.Handler;

import com.thirdarm.footballscores.R;

/**
 * Created by TROD on 20151230.
 *
 * This notifies an update for all scores widgets when it detects a change.
 */
public class ScoresDataProviderObserver extends ContentObserver {

    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;

    public ScoresDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler handler) {
        super(handler);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }

    @Override public void onChange(boolean selfChange) {
        // Notifies the widget that the collection view needs to be updated, allowing the
        //  factory's onDataSetChanged() to be called, querying the cursor for new data
        mAppWidgetManager.notifyAppWidgetViewDataChanged(
                mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.widget_view_flipper
        );
    }
}
