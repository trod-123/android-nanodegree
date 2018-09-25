package com.zn.expirytracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.zn.expirytracker.R;
import com.zn.expirytracker.settings.SettingsActivity;
import com.zn.expirytracker.ui.DetailActivity;
import com.zn.expirytracker.ui.MainActivity;
import com.zn.expirytracker.ui.SignInActivity;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import java.util.Random;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class FoodWidget extends AppWidgetProvider {

    public static final int REFRESH_JITTER = 10;
    public static final String EXTRA_ITEM = Toolbox.createStaticKeyString(FoodWidget.class,
            "extra_item");

    public FoodWidget() {
        super();
        Timber.tag(FoodWidget.class.getSimpleName());
    }

    /**
     * Helper for updating all app widgets (this is also the place where to pass additional args
     * for updating
     *
     * @param context
     * @param apm
     * @param appWigetIds
     */
    public static void updateAppWidgets(Context context, AppWidgetManager apm, int[] appWigetIds,
                                        boolean hideProgressBar) {
        for (int i : appWigetIds) {
            updateAppWidget(context, apm, i, hideProgressBar);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, boolean hideProgressBar) {
        Timber.e("In updateAppWidget(): appWidgetId=%s", appWidgetId);

        RemoteViews views;

        if (AuthToolbox.isSignedIn()) {
            // Set the regular widget
            views = new RemoteViews(context.getPackageName(), R.layout.food_widget);
            if (!hideProgressBar) {
                views.setViewVisibility(R.id.pb_widget, View.VISIBLE);
                int sRandomNumber = (new Random()).nextInt();

                // get the entry corresponding to the set widget setting value and set as header
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                int spValue = Integer.parseInt(sp.getString(context.getString(R.string.pref_widget_num_days_key), "3"));
                String spEntry = context.getResources()
                        .getStringArray(R.array.pref_expire_num_days_entries)[spValue].toLowerCase();
                String header = context.getString(R.string.expiry_msg_generic_pp, spEntry);
                views.setTextViewText(R.id.tv_widget_header, header);

                // set the header click listener
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent mainPendingIntent = PendingIntent.getActivity(
                        context, appWidgetId, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.layout_widget_header, mainPendingIntent);

                // set the settings button click listener
                Intent settingsIntent = new Intent(context, SettingsActivity.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent settingsPendingIntent = PendingIntent.getActivity(
                        context, appWidgetId, settingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.btn_widget_settings, settingsPendingIntent);

                // set the list adapter
                Intent listViewIntent = new Intent(context, FoodListRemoteViewsService.class);
                // set data to appWidgetId to ensure pending intent is unique per appWidgetId
                // pass a random id to ensure RemoteViewsFactory returns a new factory when a request to
                // update is sent
                // https://stackoverflow.com/questions/14486888/android-appwidget-listview-not-updating
                listViewIntent.setData(Uri.fromParts(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        String.valueOf(appWidgetId + sRandomNumber), null));
                views.setRemoteAdapter(R.id.lv_widget, listViewIntent);

                // set the pending intent template for individual item clicks
                Intent detailIntent = new Intent(context, DetailActivity.class);
                PendingIntent detailPendingIntent = PendingIntent.getActivity(
                        context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setPendingIntentTemplate(R.id.lv_widget, detailPendingIntent);

                // hides the empty view until needed
                views.setEmptyView(R.id.lv_widget, R.id.tv_widget_empty);
                views.setImageViewResource(R.id.iv_widget_empty_animal,
                        DataToolbox.getRandomAnimalDrawableId());
            } else {
                // We're just hiding the progress bar; all views are already updated
                views.setViewVisibility(R.id.pb_widget, View.GONE);
            }
        } else {
            // User is not signed in. Get alternate layout
            views = new RemoteViews(context.getPackageName(), R.layout.food_widget_signed_out);
            // set the sign-in pending intent
            Intent signInIntent = new Intent(context, SignInActivity.class);
            signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent signInPendingIntent = PendingIntent.getActivity(
                    context, appWidgetId, signInIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.layout_widget_signed_out, signInPendingIntent);

            views.setImageViewResource(R.id.iv_widget_empty_animal,
                    DataToolbox.getRandomAnimalDrawableId());
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Timber.e("End of updateAppWidget() with appWidgetId=%s", appWidgetId);
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_widget);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update by calling the service instead so we can add any additional args to the widgets
        // Jitter is needed to ensure widget gets loaded properly upon first instantiation
        try {
            Thread.sleep(REFRESH_JITTER);
        } catch (InterruptedException e) {
            Timber.e(e, "There was a problem while waiting in onUpdate()");
        }
        UpdateWidgetService.updateFoodWidget(context);
    }
}

