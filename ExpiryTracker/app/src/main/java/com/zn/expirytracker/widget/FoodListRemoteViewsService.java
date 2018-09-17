package com.zn.expirytracker.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.FoodRoomDb;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.ui.DetailActivity;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import java.util.List;

import timber.log.Timber;

public class FoodListRemoteViewsService extends RemoteViewsService {

    public static final int INVALID_ID = -1;

    public FoodListRemoteViewsService() {
        super();
        Timber.tag(FoodListRemoteViewsService.class.getSimpleName());
        Timber.e("Created foodlistremoteviewsservice");
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        if (intent != null) {
            return new FoodListRemoteViewsFactory(this);
        } else {
            return null;
        }
    }

    class FoodListRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private List<Food> mFoodsList;
        private long mCurrentBaseDateTimeInMillis;

        FoodListRemoteViewsFactory(Context context) {
            mContext = context;
            Timber.e("Created foodlistremoteviewsfactory");
        }

        @Override
        public void onCreate() {
            Timber.tag(FoodListRemoteViewsFactory.class.getSimpleName());
            Timber.e("In onCreate()");
            // get days filter directly from settings
            mCurrentBaseDateTimeInMillis = DataToolbox.getTimeInMillisStartOfDay(
                    System.currentTimeMillis());
            UpdateWidgetService.hideProgressBar(mContext);
        }

        /**
         * This method is called once after onCreate(), and after
         * AppWidgetManager.notifyAppWidgetViewDataChanged()
         */
        @Override
        public void onDataSetChanged() {
            Timber.e("In onDatasetChanged()");
            if (mFoodsList != null) {
                mFoodsList.clear();
            }
            fetchLatestData();
        }

        /**
         * Helper that fetches latest data via an AsyncTask. This can safely be called synchronously
         * within onDataSetChanged()
         */
        private void fetchLatestData() {
            int daysFilter = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getString(getString(R.string.pref_widget_num_days_key), "3"));
            long expiryDateFilter = DataToolbox.getDateBounds(
                    mCurrentBaseDateTimeInMillis, daysFilter);

            FoodRoomDb db = FoodRoomDb.getDatabase(mContext);
            mFoodsList = db.foodDao().getAllFoodExpiringBeforeDate_Widget(expiryDateFilter);
        }

        @Override
        public void onDestroy() {
            if (mFoodsList != null) {
                mFoodsList.clear();
            }
        }

        @Override
        public int getCount() {
            // Waiting here is necessary or else list will not be populated when widget is created
            // The weird thing is that this always works in debug mode when the "return" line has a
            // breakpoint, so it is probable we need to pause here for a bit to make sure data is
            // loaded
            try {
                Thread.sleep(FoodWidget.REFRESH_JITTER);
            } catch (InterruptedException e) {
                Timber.e(e, "There was a problem while waiting in " +
                        "FoodListRemoteViewsFactory.getCount()");
            }
            Timber.e("In getCount(), Setting the new foods list. Size: " +
                    (mFoodsList != null ? mFoodsList.size() : "null"));

            return mFoodsList != null ? mFoodsList.size() : 0;
        }

        /**
         * Processing intensive operations can be done here
         *
         * @param i
         * @return
         */
        @Override
        public RemoteViews getViewAt(int i) {
            final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_widget);
            Food food = mFoodsList.get(i);

            views.setTextViewText(R.id.tv_widget_name, food.getFoodName());
            views.setTextViewText(R.id.tv_widget_date, DataToolbox.getFormattedExpiryDateString(
                    mContext, mCurrentBaseDateTimeInMillis, food.getDateExpiry()));

            // populate the iv
            views.setViewVisibility(R.id.pb_widget_iv_item, View.VISIBLE);
            if (food.getImages() != null && food.getImages().size() > 0) {
                String imageUri = food.getImages().get(0);
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    // we're in main thread
                    ImageView ivDummy = new ImageView(mContext); // dummy view for getting glide response
                    Toolbox.loadImageFromUrl(mContext, imageUri, ivDummy, new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Bitmap> target, boolean isFirstResource) {
                            Timber.e(e, "There was an issue getting the RemoteView image");
                            views.setViewVisibility(R.id.pb_widget_iv_item, View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model,
                                                       Target<Bitmap> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            views.setImageViewBitmap(R.id.iv_widget, resource); // load into real iv
                            views.setViewVisibility(R.id.pb_widget_iv_item, View.GONE);
                            return true;
                        }
                    });
                } else {
                    // if coming through @update method, then we're in background thread
                    Bitmap bitmap = null;
                    try {
                        bitmap = Toolbox.getThumbnailFromUrl(mContext, imageUri);
                    } catch (Throwable t) {
                        Timber.e(t);
                    }
                    if (bitmap != null) {
                        views.setImageViewBitmap(R.id.iv_widget, bitmap);
                        views.setViewVisibility(R.id.pb_widget_iv_item, View.GONE);
                    }
                }
            } else {
                // there are no images
                views.setImageViewBitmap(R.id.iv_widget, null);
                views.setViewVisibility(R.id.pb_widget_iv_item, View.GONE);
            }

            // Set the fill-in intent for the detail click
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(DetailActivity.ARG_ITEM_ID_LONG, food.get_id());
            views.setOnClickFillInIntent(R.id.layout_item_widget, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return mFoodsList != null ? mFoodsList.get(i).get_id() : INVALID_ID;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
