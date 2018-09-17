package com.zn.expirytracker.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import timber.log.Timber;

/**
 * Uses a JobIntentService to schedule widget updates
 * <p>
 * https://stackoverflow.com/questions/46445265/android-8-0-java-lang-illegalstateexception-not-allowed-to-start-service-inten
 * <p>
 * https://developer.android.com/reference/android/support/v4/app/JobIntentService
 * <p>
 * https://android.jlelse.eu/keep-those-background-services-working-when-targeting-android-oreo-sdk-26-cbf6cc2bdb7f
 */
public class UpdateWidgetService extends JobIntentService {

    public static final String ACTION_UPDATE_FOOD_WIDGET = "update_food_widget";
    public static final String ACTION_HIDE_PROGRESS_BAR = "hide_progress_bar";
    private static final int JOB_ID = 1024;

    public UpdateWidgetService() {
        super();
        Timber.tag(UpdateWidgetService.class.getSimpleName());
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_UPDATE_FOOD_WIDGET:
                    handleUpdateFoodWidget(false);
                    Timber.d("Called handleUpdateFoodWidget(false)");
                    break;
                case ACTION_HIDE_PROGRESS_BAR:
                    handleUpdateFoodWidget(true);
                    Timber.d("Called handleUpdateFoodWidget(true)");
            }
        }
    }

    /**
     * Helper for updating the food widget directly
     *
     * @param context
     */
    public static void updateFoodWidget(Context context) {
        // TODO: Update the widget automatically at 00:00 every day
        Intent intent = new Intent(context, UpdateWidgetService.class);
        intent.setAction(ACTION_UPDATE_FOOD_WIDGET);
        enqueueWork(context, UpdateWidgetService.class, JOB_ID, intent);
    }

    /**
     * Helper for hiding the progress bar in the food widget. Must be called or else progress
     * bar will remain visible. This does NOT update any widget views, except for the progress bar
     *
     * @param context
     */
    public static void hideProgressBar(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        intent.setAction(ACTION_HIDE_PROGRESS_BAR);
        enqueueWork(context, UpdateWidgetService.class, JOB_ID, intent);
    }

    /**
     * Updates the food widget
     */
    private void handleUpdateFoodWidget(boolean hideProgressBar) {
        // update all widgets
        AppWidgetManager apm = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = apm.getAppWidgetIds(new ComponentName(this, FoodWidget.class));
        FoodWidget.updateAppWidgets(this, apm, appWidgetIds, hideProgressBar);
    }
}
