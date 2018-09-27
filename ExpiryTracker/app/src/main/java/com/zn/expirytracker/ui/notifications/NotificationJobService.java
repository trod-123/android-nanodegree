package com.zn.expirytracker.ui.notifications;

import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;

import java.util.List;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        // Fetch list of food expiring within the filter set in Settings
        int daysFilter = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_notifications_days_key), "3"));
        new GetFoodDataAsyncTask(job, daysFilter).execute();

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
    private class GetFoodDataAsyncTask extends AsyncTask<Void, Void, List<Food>> {
        private JobParameters mJob;
        private int mDaysFilter;

        GetFoodDataAsyncTask(JobParameters job, int daysFilter) {
            mJob = job;
            mDaysFilter = daysFilter;
        }

        @Override
        protected List<Food> doInBackground(Void... voids) {
            return NotificationHelper.fetchLatestData(NotificationJobService.this, mDaysFilter);
        }

        @Override
        protected void onPostExecute(List<Food> foods) {
            NotificationHelper.showNotification(NotificationJobService.this, mDaysFilter, foods);
            jobFinished(mJob, false);
        }
    }
}
