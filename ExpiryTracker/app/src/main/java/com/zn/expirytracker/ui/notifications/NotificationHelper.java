package com.zn.expirytracker.ui.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.FoodRoomDb;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.ui.DetailActivity;
import com.zn.expirytracker.ui.MainActivity;
import com.zn.expirytracker.utils.Constants;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.DateToolbox;
import com.zn.expirytracker.utils.DebugFields;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import timber.log.Timber;

public class NotificationHelper {

    private static final int ID_REMINDER_NOTIFICATIONS = 10;

    /**
     * The start period, in minutes, before the notification can show again
     */
    private static final int RECURRING_TRIGGER_START = (23 * 60 + 59) * 60 + 30; // 23:59:30 in seconds

    /**
     * The end period, in minutes, before the notification must show again
     */
    private static final int RECURRING_TRIGGER_END = (24 * 60) * 60 + 30; // 24:00:30 in seconds

    /**
     * The end period, in minutes, before the notification must show again. If specifying a
     * trigger determined by the user-selected {@link TimeOfDay} from preferences, use this
     */
    private static final int DEFAULT_TRIGGER_END_JITTER = 1; // 1 minute

    public enum TimeOfDay {
        MORNING, AFTERNOON, EVENING, OVERNIGHT
    }

    private Context mContext;

    /**
     * Constructor needed for creating and showing the notification
     *
     * @param context
     */
    public NotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Schedules the notification using {@link FirebaseJobDispatcher}, with the "Time of Day"
     * value passed as an argument. To be used within
     * {@link com.zn.expirytracker.settings.SettingsFragment#sOnPreferenceChangeListener} with
     * the fresh value
     *
     * @param context
     * @param recurring
     */
    public static void scheduleNotificationJob(Context context, boolean recurring, String todValue) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        NotificationHelper.TimeOfDay tod = getTimeOfDayFromPreferenceValue(todValue);
        Job notifJob = NotificationHelper.getNotificationJobBuilder(dispatcher, recurring, tod);
        dispatcher.mustSchedule(notifJob);
    }

    /**
     * Schedules the notification using {@link FirebaseJobDispatcher}. Gets the "Time of Day" value
     * directly from SharedPreferences. This should not be used within
     * {@link com.zn.expirytracker.settings.SettingsFragment#sOnPreferenceChangeListener} since
     * the SharedPreferences will be stale
     *
     * @param context
     * @param recurring
     */
    public static void scheduleNotificationJob(Context context, boolean recurring) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String todValue = sp.getString(context.getString(R.string.pref_notifications_tod_key),
                context.getString(R.string.pref_notifications_tod_default));
        scheduleNotificationJob(context, recurring, todValue);
    }

    /**
     * Cancels all current notification jobs
     */
    public static void cancelNotificationJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancel(NotificationJobService.class.getSimpleName());
    }

    /**
     * Type converter that converts the Time of Day preference value into its
     * {@link TimeOfDay} equivalent value
     *
     * @param todValue
     * @return
     */
    private static TimeOfDay getTimeOfDayFromPreferenceValue(String todValue) {
        switch (todValue) {
            case "0":
                return TimeOfDay.MORNING;
            case "1":
                return TimeOfDay.AFTERNOON;
            case "2":
                return TimeOfDay.EVENING;
            case "3":
                return TimeOfDay.OVERNIGHT;
            default:
                throw new IllegalArgumentException(
                        String.format("The passed todValue is invalid: %s", todValue));
        }
    }

    /**
     * Create the job  that will show the notification at the user-specified time intervals. The
     * job dispatcher takes care of the timing, while {@link NotificationHelper}
     * handles what to show in the notification and what kind of notification to show (single,
     * or list)
     * <p>
     *
     * @param dispatcher
     * @param recurring  set to {@code true} to show the notification again as specified by
     *                   {@code triggerRange}
     * @param tod        {@code @Nullable} used to set the {@code triggerRange} for the Job. If
     *                   null, {@code triggerRange} will default to a period between
     *                   {@link NotificationHelper#RECURRING_TRIGGER_START} and
     *                   {@link NotificationHelper#RECURRING_TRIGGER_END}
     * @return {@link Job.Builder} with the required notification job specs:
     * <ul>
     * <li>{@code SERVICE} = {@link NotificationJobService}</li>
     * <li>{@code TAG} = {@link NotificationJobService} class name</li>
     * <li>{@code LIFETIME} = {@link Lifetime#FOREVER}</li>
     * <li>{@code RETRY_STRATEGY} = {@link RetryStrategy#DEFAULT_EXPONENTIAL}</li>
     * <li>{@code REPLACE_CURRENT} = {@code true}</li>
     * </ul>
     */
    private static Job getNotificationJobBuilder(FirebaseJobDispatcher dispatcher, boolean recurring,
                                                 @NonNull TimeOfDay tod) {
        Job.Builder builder = dispatcher.newJobBuilder()
                .setService(NotificationJobService.class)
                .setTag(NotificationJobService.class.getSimpleName())
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(true)
                .setRecurring(recurring);
        // Set trigger only for the first time. The recurring trigger is set in
        // NotificationHelper when this job gets recreated and scheduled periodically
        // Trigger is based entirely on tod, and not numDays
        // Once we recreate the trigger, set to recur in the next 24 hours (give or take a few
        // hours)
        if (!recurring) {
            // If we're running for first time, et the trigger to meet the user-specified
            // notification time
            Pair<Integer, Integer> triggerRange = getNotificationTriggerRange(tod);
            if (!DebugFields.ENABLE_QUICK_REMINDERS) {
                Timber.d("Notification non-recurring triggerRange in seconds: %s %s",
                        triggerRange.first * 60, triggerRange.second * 60);
                builder.setTrigger(Trigger.executionWindow(
                        triggerRange.first * 60, triggerRange.second * 60));
            } else {
                // For debugging
                builder.setTrigger(Trigger.NOW);
            }
        } else {
            // The notification has been set, so set the trigger so notification can recur in the
            // next ~24 hours
            Timber.d("Notification recurring triggerRange in seconds: %s %s",
                    RECURRING_TRIGGER_START, RECURRING_TRIGGER_END);
            builder.setTrigger(
                    Trigger.executionWindow(RECURRING_TRIGGER_START, RECURRING_TRIGGER_END));

        }
        return builder.build();
    }

    /**
     * Calculates the difference in minutes between the System's current time and the targeted
     * {@link TimeOfDay}. This is set in the first integer in the pair. The second is the first
     * plus a jitter number of minutes, represented by
     * {@link NotificationHelper#DEFAULT_TRIGGER_END_JITTER}
     * <p>
     * Integers represent number of minutes from when the trigger's job is scheduled or done, to
     * when the job should occur again
     *
     * @param tod
     * @return
     */
    private static Pair<Integer, Integer> getNotificationTriggerRange(TimeOfDay tod) {
        DateTime currentDateTime = new DateTime();
        DateTime desiredDateTime;
        int hour;
        switch (tod) {
            case MORNING:
                hour = Constants.DEFAULT_MORNING_HOUR;
                break;
            case AFTERNOON:
                hour = Constants.DEFAULT_AFTERNOON_HOUR;
                break;
            case EVENING:
                hour = Constants.DEFAULT_EVENING_HOUR;
                break;
            case OVERNIGHT:
                hour = Constants.DEFAULT_OVERNIGHT_HOUR;
                break;
            default:
                throw new IllegalArgumentException(String.format("Unexpected tod: %s", tod));
        }
        desiredDateTime = currentDateTime.withTimeAtStartOfDay()
                .hourOfDay().setCopy(hour);
        if (desiredDateTime.isBefore(currentDateTime)) {
            // Adjust if desi red is a day behind
            desiredDateTime = desiredDateTime.plusDays(1);
        }
        int numMinutesStart = Minutes.minutesBetween(currentDateTime, desiredDateTime).getMinutes();
        return new Pair<>(numMinutesStart, numMinutesStart + DEFAULT_TRIGGER_END_JITTER);
    }

    /**
     * Shows the notification for the provided list of foods
     *
     * @param foods
     */
    public void showNotification(@NonNull List<Food> foods) {
        if (foods.size() == 0) {
            // Don't show any notification if there is no food expiring within the time range set
            // by the JobScheduler
            return;
        }

        boolean singleFoodCase = false;
        long currentTimeStartOfDay = DateToolbox.getTimeInMillisStartOfDay(
                System.currentTimeMillis());
        PendingIntent contentIntent;

        if (foods.size() == 1) {
            // Single food case
            Food food = foods.get(0);
            if (food.getDateExpiry() < currentTimeStartOfDay) {
                // Do not show notification if food is already expired
                return;
            }
            singleFoodCase = true;
        }

        // Get the number of foods expiring today and tomorrow
        SparseIntArray frequencies = DataToolbox.getIntFrequencies(
                foods, currentTimeStartOfDay, true, 7); // 7 days in a week. Guaranteed array will have size = 7

        // Don't include expired foods (negative X values)
        int startIndex = DataToolbox.getStartingPositiveIndex(
                frequencies, DataToolbox.NO_INDEX_LIMIT);

        // Get the total number of foods expiring, without including foods already expired
        int totalFoodCountsFromFilter = DataToolbox.getTotalFrequencyCounts(
                frequencies, startIndex, DataToolbox.NO_INDEX_LIMIT);

        if (totalFoodCountsFromFilter == 0) {
            // Don't show notification if there are no foods expiring!
            return;
        }

        // Get the food mapping of day counts and food lists
        SparseArray<List<Food>> foodDateMap = DataToolbox.getFoodDateMap(
                foods, currentTimeStartOfDay, true, 7);

        if (totalFoodCountsFromFilter == 1 || singleFoodCase) {
            // Get the sole food that's expiring
            Food food = DataToolbox.getFirstFoodFromFoodDateMap(foodDateMap, false);
            if (food != null) {
                // Get the readable date food is expiring, and set in message
                String contentTitle = DateToolbox.getFormattedExpiryDateString(mContext,
                        currentTimeStartOfDay, food.getDateExpiry());

                // Set pending intent to the food detail
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.ARG_ITEM_ID_LONG, food.get_id());
                intent.putExtra(DetailActivity.EXTRA_LAUNCHED_EXTERNALLY, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                contentIntent = PendingIntent.getActivity(
                        mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // TODO: Set action to "delete" food, and handle it

                new GenerateSingleFoodNotificationTask(food, contentTitle, food.getFoodName(),
                        contentIntent).execute();
            }
        } else {
            // Set pending intent to launch AAG fragment
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            contentIntent = PendingIntent.getActivity(
                    mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the content and title strings
            StringBuilder contentBuilder = new StringBuilder();
            List<String> lines = DataToolbox.getDailyLinesSummary(mContext, foodDateMap, currentTimeStartOfDay,
                    Constants.MAX_DAILY_LINE_SUMMARY_FOOD_COUNT);
            for (int i = 0; i < lines.size(); i++) {
                contentBuilder.append(lines.get(i));
                contentBuilder.append("\n");
            }
            String contentTitle = DataToolbox.getRandomTitleSummary(mContext);
            if (lines.size() == 1) {
                // Get the readable date food is expiring, and set in message
                Food food = DataToolbox.getFirstFoodFromFoodDateMap(foodDateMap, false);
                if (food != null) contentTitle = DateToolbox.getFormattedExpiryDateString(mContext,
                        currentTimeStartOfDay, food.getDateExpiry());
            }

            showNotificationHelper(mContext, getNotificationBuilder(mContext, contentTitle,
                    contentBuilder.toString().trim(), null, contentIntent));
        }

        // Schedule the next notification
        scheduleNotificationJob(mContext, true);
    }

    /**
     * Helper that fetches latest data. This needs to be called on a background thread
     */
    public List<Food> fetchLatestData(int daysFilter) {
        long expiryDateFilter = DateToolbox.getDateBounds(
                DateToolbox.getTimeInMillisStartOfDay(System.currentTimeMillis()), daysFilter);

        FoodRoomDb db = FoodRoomDb.getDatabase(mContext);
        return db.foodDao().getAllFoodExpiringBeforeDate_List(expiryDateFilter);
    }

    /**
     * Shows a notification and sets the channel if device SDK >= O
     *
     * @param context
     * @param nBuilder
     */
    private void showNotificationHelper(Context context, Notification.Builder nBuilder) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = context.getString(R.string.notification_channel_id);
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(notificationChannel);
            nBuilder.setChannelId(channelId);
        }
        nm.notify(ID_REMINDER_NOTIFICATIONS, nBuilder.build());
    }

    /**
     * Returns the Notification Builder for a multiple foods notification
     *
     * @param context
     * @param contentTitle
     * @param contentText
     * @param largeIcon
     * @param contentIntent
     * @return
     */
    private Notification.Builder getNotificationBuilder(Context context, String contentTitle,
                                                        String contentText, Bitmap largeIcon,
                                                        PendingIntent contentIntent) {
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_cube_black_24dp)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        if (largeIcon != null) {
            builder.setLargeIcon(largeIcon)
                    .setStyle(new Notification.BigPictureStyle()
                            .bigPicture(largeIcon));
        } else {
            builder.setStyle(new Notification.BigTextStyle()
                    .bigText(contentText)); // for multiline text
        }
        return builder;
    }

    private class GenerateSingleFoodNotificationTask extends AsyncTask<Void, Void, Bitmap> {
        private Food mFood;
        private String mTitle;
        private String mContent;
        private PendingIntent mIntent;

        GenerateSingleFoodNotificationTask(Food food, String title, String content,
                                           PendingIntent intent) {
            mFood = food;
            mTitle = title;
            mContent = content;
            mIntent = intent;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            showNotificationHelper(mContext, getNotificationBuilder(mContext, mTitle, mContent,
                    bitmap, mIntent));
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            // Get the first food image, if available, and set in message
            if (mFood.getImages() != null && mFood.getImages().size() > 0) {
                String imageUri = mFood.getImages().get(0);
                return Toolbox.getThumbnailFromUrl(mContext, imageUri); // must be in bg thread
            } else {
                return null;
            }
        }
    }
}
