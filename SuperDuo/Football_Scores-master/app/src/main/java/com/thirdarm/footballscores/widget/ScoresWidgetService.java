package com.thirdarm.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.thirdarm.footballscores.R;
import com.thirdarm.footballscores.provider.ateam.AteamColumns;
import com.thirdarm.footballscores.provider.bteam.BteamColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureCursor;
import com.thirdarm.footballscores.provider.fixture.FixtureSelection;
import com.thirdarm.footballscores.provider.fixture.Status;
import com.thirdarm.footballscores.utilities.Utilities;

/**
 * Created by TROD on 20151230.
 *
 * This service allows the factory to be bound to the collection service.
 */
public class ScoresWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScoresRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}