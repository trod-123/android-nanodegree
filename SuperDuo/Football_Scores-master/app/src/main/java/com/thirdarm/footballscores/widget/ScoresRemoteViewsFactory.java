package com.thirdarm.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.thirdarm.footballscores.R;
import com.thirdarm.footballscores.provider.ateam.AteamColumns;
import com.thirdarm.footballscores.provider.bteam.BteamColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureColumns;
import com.thirdarm.footballscores.provider.fixture.FixtureCursor;
import com.thirdarm.footballscores.provider.fixture.FixtureSelection;
import com.thirdarm.footballscores.provider.fixture.Status;
import com.thirdarm.footballscores.utilities.Utilities;

import java.util.concurrent.ExecutionException;

/**
 * This provides data to the collection widget and populates view elements. Thsi is essentially a
 *  widget's adapter.
 */
class ScoresRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = ScoresRemoteViewsFactory.class.getSimpleName();

    private Context mContext;
    private int mAppWidgetId;
    private FixtureCursor mCursor;

    public ScoresRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    // Set up any connections and/or cursors to data source here
    @Override
    public void onCreate() {
        // Since cursor is reloaded in onDateSetChanged(), which gets called immediately after
        //  onCreate(), nothing needs to be done here
    }

    // This is where the cursor is loaded
    @Override
    public void onDataSetChanged() {
        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }

        // This method is called by the app hosting the widget, i.e. launcher. However, we need
        //  access to the content provider's data so we can display it in the widget. Because the
        //  content provider is not exported, we need to clear the calling identity so that the
        //  call to the content provider uses our process and permission.
        final long identityToken = Binder.clearCallingIdentity();

        // The error "java.lang.IllegalArgumentException: column 'x' does not exist" just means
        //  x is not part of projection.
        String[] projection = {
                FixtureColumns._ID, FixtureColumns.DATE, FixtureColumns.TIME,
                FixtureColumns.STATUS, FixtureColumns.TEAMA_ID, FixtureColumns.TEAMB_ID,
                FixtureColumns.LEAGUEID, FixtureColumns.HOMEGOALS, FixtureColumns.AWAYGOALS,
                FixtureColumns.MATCHID, FixtureColumns.MATCHDAY,
                AteamColumns.NAME, AteamColumns.SHORTNAME, AteamColumns.CRESTURL,
                BteamColumns.NAME, BteamColumns.SHORTNAME, BteamColumns.CRESTURL
        };
        String[] date = {Utilities.getUserDate(System.currentTimeMillis())};
        Log.d(LOG_TAG, "The date is " + date[0]);

        mCursor = new FixtureCursor(mContext.getContentResolver().query((new FixtureSelection()).uri(),
                projection,
                FixtureColumns.DATE + " == ? ",
                date,
                null));

        // Restore the calling identity
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    // Construct a remoteViews object based on widget xml file
    @Override
    public RemoteViews getViewAt(int position) {
        mCursor.moveToPosition(position);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        // Set the remote view contents
        String homeTeamName = mCursor.getAteamShortname();
        String awayTeamName = mCursor.getBteamShortname();
        if (homeTeamName == null) {
            homeTeamName = mCursor.getAteamName();
        }
        if (awayTeamName == null) {
            awayTeamName = mCursor.getBteamName();
        }
        String matchTime = mContext.getString(R.string.date_time, mCursor.getDate(), mCursor.getTime());
        rv.setTextViewText(R.id.widget_item_time, matchTime);
        String leagueName = Utilities.getLeague(mContext, mCursor.getLeagueid());

        // Set the scores
        if (mCursor.getHomegoals() != -1 && mCursor.getAwaygoals() != -1) {
            // Only set goals if not null
            // NOTE: WHEN SETTING TEXT TO TEXTVIEWS, TEXT MUST BE A STRING. NOT AN INT.
            int homeGoals = mCursor.getHomegoals();
            int awayGoals = mCursor.getAwaygoals();
            rv.setTextViewText(R.id.widget_textview_home_score, "" + homeGoals);
            rv.setTextViewText(R.id.widget_textview_away_score, "" + awayGoals);

            // Set the scores colors
            int homeColor, awayColor, winColor, loseColor;
            String matchStatus, winStatus, notedTeam;
            if (mCursor.getStatus() == Status.FINISHED) {
                matchStatus = mContext.getString(R.string.status_finished);
                rv.setTextColor(R.id.widget_item_status, mContext.getResources().getColor(R.color.primary_text));
                winColor = mContext.getResources().getColor(R.color.primary_text);
                loseColor = mContext.getResources().getColor(R.color.tertiary_text);
            } else {
                matchStatus = mContext.getString(R.string.status_timed);
                rv.setTextColor(R.id.widget_item_status, mContext.getResources().getColor(R.color.secondary_text));
                winColor = mContext.getResources().getColor(R.color.primary_text);
                loseColor = mContext.getResources().getColor(R.color.secondary_text);
            }
            if (homeGoals > awayGoals) {
                homeColor = winColor;
                awayColor = loseColor;
                winStatus = mContext.getString(R.string.scores_winner);
                notedTeam = homeTeamName;
            } else if (homeGoals < awayGoals) {
                homeColor = loseColor;
                awayColor = winColor;
                winStatus = mContext.getString(R.string.scores_winner);
                notedTeam = awayTeamName;
            } else {
                homeColor = loseColor;
                awayColor = loseColor;
                winStatus = mContext.getString(R.string.scores_tied);
                notedTeam = "";
            }
            rv.setTextViewText(R.id.widget_item_status, matchStatus);
            rv.setTextColor(R.id.widget_textview_home_score, homeColor);
            rv.setTextColor(R.id.widget_textview_away_score, awayColor);

            // Set content description
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                rv.setContentDescription(R.id.widget_item_rootview, mContext.getString(R.string.a11y_full_match_description,
                                matchStatus, leagueName, mCursor.getMatchday(), matchTime,
                                awayTeamName, homeTeamName, awayGoals, homeGoals,
                                winStatus, notedTeam)
                );
            }
        } else {
            // Cursors that don't have scores should be hidden score views
            rv.setTextViewText(R.id.widget_item_status, mContext.getString(R.string.status_upcoming));
            rv.setTextColor(R.id.widget_item_status, mContext.getResources().getColor(R.color.tertiary_text));
            rv.setViewVisibility(R.id.widget_textview_home_score, View.GONE);
            rv.setViewVisibility(R.id.widget_textview_away_score, View.GONE);

            // Set content description
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                rv.setContentDescription(R.id.widget_item_rootview, mContext.getString(R.string.a11y_upcoming_match_description,
                                leagueName, mCursor.getMatchday(), matchTime,
                                awayTeamName, homeTeamName)
                );
            }
        }

        // Set the crests
        Bitmap homeCrestBitmap = null;
        Bitmap awayCrestBitmap = null;
        String homeCrestUrl = Utilities.convertCrestUrl(mCursor.getAteamCresturl());
        try {
            homeCrestBitmap = Glide.with(mContext)
                    .load(homeCrestUrl)
                    .asBitmap()
                    .error(R.drawable.ic_launcher)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG_TAG, "Error retrieving home crest icon from " + homeCrestUrl, e);
        }
        if (homeCrestBitmap != null) {
            rv.setImageViewBitmap(R.id.widget_imageview_home_crest, homeCrestBitmap);
        }

        String awayCrestUrl = Utilities.convertCrestUrl(mCursor.getBteamCresturl());
        try {
            awayCrestBitmap = Glide.with(mContext)
                    .load(awayCrestUrl)
                    .asBitmap()
                    .error(R.drawable.ic_launcher)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG_TAG, "Error retrieving away crest icon from " + awayCrestUrl, e);
        }
        if (awayCrestBitmap != null) {
            rv.setImageViewBitmap(R.id.widget_imageview_away_crest, awayCrestBitmap);
        }
        Bundle extras = new Bundle();
        extras.putInt(ScoresWidgetProvider.CURSOR_POSITION, mCursor.getPosition());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item_rootview, fillInIntent);


        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_loading);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position)) {
            return mCursor.getId();
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}