package com.thirdarm.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.RemoteViews;

import com.thirdarm.footballscores.MainActivity;
import com.thirdarm.footballscores.R;
import com.thirdarm.footballscores.provider.fixture.FixtureSelection;

/**
 * Created by TROD on 20151230.
 */
public class ScoresWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = ScoresWidgetProvider.class.getSimpleName();

    private static ScoresDataProviderObserver sDataObserver;
    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;

    public static final String CURSOR_POSITION =
            "com.thirdarm.footballscores.widget.cursorPosition";
    private static final String ACTION_NEXT =
            "com.thirdarm.footballscores.widget.actionNext";
    private static final String ACTION_PREVIOUS =
            "com.thirdarm.footballscores.widget.actionPrevious";
    private static final String ACTION_REFRESH =
            "com.thirdarm.footballscores.widget.actionRefresh";
    private static final String WIDGET_IDS_KEY =
            "com.thirdarm.footballscores.widget.widgetIds";
    public static final String ACTION_UPDATE_EMPTY_TEXT =
            "com.thirdarm.footballscores.widget.emptyText";

    public ScoresWidgetProvider() {
        // Start the worker thread
        sWorkerThread = new HandlerThread("ScoresWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    /**
     * Called for every broadcast and before any callback methods below. This does not normally
     *  need to be implemented because default implementation already takes care of the main things.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews root = new RemoteViews(context.getPackageName(), R.layout.widget);
        String intent_action = intent.getAction();
        int id = intent.getIntExtra(WIDGET_IDS_KEY, 0);
        if (intent_action != null) {
            switch (intent_action) {
                case ACTION_NEXT:
                    root.showNext(R.id.widget_view_flipper);
                    // This needs to be called to display widget changes
                    appWidgetManager.updateAppWidget(id, root);
                    break;
                case ACTION_PREVIOUS:
                    root.showPrevious(R.id.widget_view_flipper);
                    appWidgetManager.updateAppWidget(id, root);
                    break;
                case ACTION_REFRESH:
                    AppWidgetManager.getInstance(context)
                            .notifyAppWidgetViewDataChanged(id, R.id.widget_view_flipper);
                    break;
                case ACTION_UPDATE_EMPTY_TEXT:
                    int message = intent.getIntExtra(ACTION_UPDATE_EMPTY_TEXT, -1);
                    if (message != -1) {
                        root.setTextViewText(R.id.widget_view_flipper_empty, context.getString(message));
                        appWidgetManager.updateAppWidget(id, root);
                    }
                    break;
                default:
            }
        }
        super.onReceive(context, intent);
    }

    /**
     * Called to update the widget at regular intervals defined by updatePeriodMillis in the
     *  AppWidgetProviderInfo xml
     * Also called when the user adds the widget to perform initial essential setup
     * Not called when there is a configuration activity declared
     * This may be the only method you need to use
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Query to retrieve data here and extract needed information
        // To loop over each app widget put on the homescreen, use a loop iterating over appWidgetIds
        for (int appWidgetId : appWidgetIds) {
            // Set up the intent that will launch the service that will provide the views
            Intent intent = new Intent(context, ScoresWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // When comparing intents, by default extras are ignored, so make sure extras are
            //  compared by embedding the extras into the data
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // To get the views associated with the widget, use RemoteViews
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            // Set the adapter
            views.setRemoteAdapter(R.id.widget_view_flipper, intent);
            // Set the empty view (empty view must be a sibling of collection view)
            views.setEmptyView(R.id.widget_view_flipper, R.id.widget_view_flipper_empty);

            // The pending intent allows widget to launch the host activity when a user clicks
            //  on the widget. For a widget provider that uses collections, an intent template
            //  needs to be used if you want individual behavior for each item. But, if you want
            //  a global button independent of the item, then a pending template is not needed.
            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
            // The view that is passed in should be the id of the view the user clicks on to
            //  activate the intent. It has to be a view declared within the RemoteView
            views.setPendingIntentTemplate(R.id.widget_view_flipper, clickPendingIntent);

            // For the next/down button
            Intent nextIntent = new Intent(context, ScoresWidgetProvider.class);
            nextIntent.setAction(ACTION_NEXT);
            nextIntent.putExtra(WIDGET_IDS_KEY, appWidgetId);
            PendingIntent pendingNextIntent = PendingIntent
                    .getBroadcast(context, 0, nextIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_button_down, pendingNextIntent);

            // For the previous/up button
            Intent previousIntent = new Intent(context, ScoresWidgetProvider.class);
            previousIntent.setAction(ACTION_PREVIOUS);
            previousIntent.putExtra(WIDGET_IDS_KEY, appWidgetId);
            PendingIntent pendingPreviousIntent = PendingIntent
                    .getBroadcast(context, 0, previousIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_button_up, pendingPreviousIntent);

            // TODO: Implement a refresh button that would reload the scores for today
//            Intent updateIntent = new Intent();
//            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//            updateIntent.putExtra(WIDGET_IDS_KEY, appWidgetId);
//            PendingIntent pendingUpdateIntent = PendingIntent
//                    .getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            views.setOnClickPendingIntent(R.id.widget_button_refresh, pendingUpdateIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * Called when widget is first placed and when widget is resized. This method can manage
     *  visibiility and layout of content as widget's size changes. Get the widget's current size
     *  via calling getAppWidgetOptions().
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     * @param newOptions
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    /**
     * Called whenever widget is deleted.
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * Called when instance of app widget is created for the first time. Good place to open up a
     *  new database or perform other setup that needs to occur once for all app widget instances.
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        final ContentResolver cr = context.getContentResolver();
        if (sDataObserver == null) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, ScoresWidgetProvider.class);
            sDataObserver = new ScoresDataProviderObserver(mgr, cn, sWorkerQueue);
            cr.registerContentObserver((new FixtureSelection()).uri(), true, sDataObserver);
        }
    }

    /**
     * Called when last instance of app widget is deleted. Clean up any work done in onEnabled()
     *  here.
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}
