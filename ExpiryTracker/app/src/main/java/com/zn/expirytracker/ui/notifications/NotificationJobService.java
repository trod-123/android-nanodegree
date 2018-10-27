package com.zn.expirytracker.ui.notifications;

import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;

import java.lang.ref.WeakReference;
import java.util.List;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        // Fetch list of food expiring within the filter set in Settings
        int daysFilter = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_notifications_days_key), "3"));
        new GetFoodDataAsyncTask(this, job, daysFilter,
                new NotificationHelper(this)).execute();

        return true; // true if there is still work going on
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // true if this job should be retried
    }

    /**
     * Gets the filtered food list for the notification, and shows the notification once the list
     * is available
     */
    private static class GetFoodDataAsyncTask extends AsyncTask<Void, Void, List<Food>> {
        // https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
        private WeakReference<NotificationJobService> mJobServiceReference;

        private JobParameters mJob;
        private int mDaysFilter;
        private NotificationHelper mHelper;

        GetFoodDataAsyncTask(NotificationJobService jobService, JobParameters job, int daysFilter, NotificationHelper helper) {
            mJobServiceReference = new WeakReference<>(jobService);
            mJob = job;
            mDaysFilter = daysFilter;
            mHelper = helper;
        }

        @Override
        protected List<Food> doInBackground(Void... voids) {
            return mHelper.fetchLatestData(mDaysFilter);
        }

        @Override
        protected void onPostExecute(List<Food> foods) {
            mHelper.showNotification(foods);

            NotificationJobService js = mJobServiceReference.get();
            js.jobFinished(mJob, false);
        }
    }
}
