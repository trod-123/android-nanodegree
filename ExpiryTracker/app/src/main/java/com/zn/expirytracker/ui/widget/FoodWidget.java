package com.zn.expirytracker.ui.widget;

import android.app.AlarmManager;
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

import org.joda.time.DateTime;

import java.util.Random;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class FoodWidget extends AppWidgetProvider {

    private static final String ACTION_SCHEDULED_UPDATE = "action_scheduled_update";
    private static final String KEY_SCHEDULED_UPDATE_SET = Toolbox.createStaticKeyString(
            FoodWidget.class, "scheduled_update_set");

    private static final int RC_SCHEDULED_UPDATE = 103;

    /**
     * Enable for debugging purposes only. Allows refreshing to be done quickly for testing
     */
    private static final boolean DEBUG_ENABLE_QUICK_REFRESH = false;

    private static final int HOUR_MIDNIGHT = 0;
    public static final int REFRESH_JITTER = 10;

    public FoodWidget() {
        super();
        Timber.tag(FoodWidget.class.getSimpleName());
    }

    // This runs every time when either:
    // 1) First widget is placed on home screen (e.g. after all having been removed)
    // 2) After device restarts
    //
    // Since alarms do not persist across device restarts, we will need to reset the alarm upon
    // restarting
    @Override
    public void onEnabled(Context context) {
        Timber.d("We have a widget!");
        enableNextUpdate(context, true);
        scheduleNextUpdate(context);
        super.onEnabled(context);
    }

    // This runs whenever the last widget on home screen is removed. Cancels the
    @Override
    public void onDisabled(Context context) {
        Timber.d("No more widgets jumping on the bed");
        cancelNextUpdate(context);
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                // Update the widget whenever the user's time or locale changes, or when it reaches
                // midnight
                case Intent.ACTION_TIMEZONE_CHANGED:
                case Intent.ACTION_TIME_CHANGED:
                case ACTION_SCHEDULED_UPDATE:
                    // Clear the update status keeper so we can set the next update
                    Timber.d("Received Widget update broadcast");
                    enableNextUpdate(context, true);
                    UpdateWidgetService.updateFoodWidget(context);
                    break;
            }
        }
        super.onReceive(context, intent);
    }

    /**
     * Schedules the next widget update at midnight, only if the update had not already been set
     *
     * @param context
     */
    private static void scheduleNextUpdate(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FoodWidget.class.getSimpleName(), Context.MODE_PRIVATE);
        // true by default to prevent alarms from being scheduled before widgets are added
        if (!sp.getBoolean(KEY_SCHEDULED_UPDATE_SET, true)) {
            // Prevent manually updating the widget from scheduling the next update.
            // Only schedule the next update if update status is reset (as in coming from
            // onReceive())
            Intent intent = new Intent(context, FoodWidget.class).setAction(ACTION_SCHEDULED_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, RC_SCHEDULED_UPDATE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            DateTime dateTime;
            long midnight;
            if (!DEBUG_ENABLE_QUICK_REFRESH) {
                // Set the widget update time to midnight of the next day
                dateTime = new DateTime().withTimeAtStartOfDay().hourOfDay()
                        .setCopy(HOUR_MIDNIGHT).plusDays(1);
                midnight = dateTime.getMillis();
            } else {
                // For debugging, allows quicker updating
                dateTime = new DateTime().plusMinutes(2);
                midnight = dateTime.getMillis();
            }

            // Since we call this every time widget is updated, there is no need to set an actual
            // repeating alarm. Note, if the time we set the alarm to occur is a time in the past,
            // the alarm will run immediately
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC, midnight, pendingIntent);
            // Mark that the update had been set
            enableNextUpdate(context, false);
            Timber.d("Next widget update scheduled for: %s %s %s %s %s",
                    dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), dateTime.getHourOfDay(),
                    dateTime.getMinuteOfHour(), dateTime.getSecondOfMinute());
        }
    }

    /**
     * Cancels the auto update widget process
     *
     * @param context
     */
    public static void cancelNextUpdate(Context context) {
        Intent intent = new Intent(context, FoodWidget.class).setAction(ACTION_SCHEDULED_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, RC_SCHEDULED_UPDATE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);

        // Reset the SP value
        enableNextUpdate(context, true);
        Timber.d("Widget updates cancelled");
    }

    /**
     * Updates SP to allow subsequent enabling of Widget updates
     *
     * @param context
     * @param enabled {@code false} to allow updating
     */
    private static void enableNextUpdate(Context context, boolean enabled) {
        SharedPreferences sp = context.getSharedPreferences(
                FoodWidget.class.getSimpleName(), Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_SCHEDULED_UPDATE_SET, !enabled).apply();
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
        Timber.d("Widgets updated");
        for (int i : appWigetIds) {
            updateAppWidget(context, apm, i, hideProgressBar);
        }
        scheduleNextUpdate(context);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, boolean hideProgressBar) {
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
        }

        int animalDrawableId = DataToolbox.getRandomAnimalDrawableId();
        views.setImageViewResource(R.id.iv_widget_empty_animal, animalDrawableId);
        views.setContentDescription(R.id.iv_widget_empty_animal,
                DataToolbox.getAnimalContentDescriptionById(animalDrawableId));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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

