package com.zn.expirytracker.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.utils.DataToolbox;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An adapter that holds food details
 */
public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewHolder> {

    private String[] mFoodNames;
    private long[] mExpiryDates;
    private int[] mCounts;
    private Storage[] mStorageLocs;

    private Context mContext;
    private long mCurrentTime;

    FoodListAdapter(String[] names, long[] dates, int[] counts, Storage[] locs) {
        // TODO: Replace with Paging and LiveData implementation
        mFoodNames = names;
        Arrays.sort(dates);
        mExpiryDates = dates;
        mCounts = counts;
        mStorageLocs = locs;

        mCurrentTime = System.currentTimeMillis();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        // TODO: Access Food objects
        holder.bind(mFoodNames[position], mExpiryDates[position], mCounts[position],
                mStorageLocs[position]);
    }

    @Override
    public int getItemCount() {
        return mFoodNames.length;
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_list_item_food_name)
        TextView mName;
        @BindView(R.id.tv_list_item_expiry_date)
        TextView mExpiryDate;
        @BindView(R.id.iv_list_item_storage_icon)
        ImageView mStorageIcon;
        @BindView(R.id.tv_list_item_count)
        TextView mCount;
        @BindView(R.id.iv_list_item_image)
        ImageView mImage;
        @BindView(R.id.tv_number_circle_num_days)
        TextView mExpiryDaysNum;
        @BindView(R.id.tv_number_circle_days_label)
        TextView mExpiryDaysLabel;
        @BindView(R.id.iv_number_circle_rim)
        ImageView mExpiryDaysRim;

        FoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(String name, long date, int count, Storage storage) {
            mName.setText(name);
            mExpiryDate.setText(DataToolbox.getFormattedExpiryDateString(
                    mContext, mCurrentTime, date));
            mCount.setText(String.valueOf(count));
            mStorageIcon.setImageResource(DataToolbox.getStorageIconResource(storage));
            int daysUntilExpiry = DataToolbox.getNumDaysBetweenDates(mCurrentTime, date);
            mExpiryDaysNum.setText(String.valueOf(daysUntilExpiry));
            mExpiryDaysLabel.setText(mContext.getResources().getQuantityString(
                    R.plurals.food_days_label, daysUntilExpiry));

            // Set the color of a drawable
            // https://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically
            Drawable rim = mExpiryDaysRim.getBackground();
            if (rim instanceof GradientDrawable) {
                ((GradientDrawable) rim.mutate()).setStroke(mContext.getResources()
                                .getDimensionPixelSize(R.dimen.number_circle_outline_width),
                        ContextCompat.getColor(mContext, DataToolbox.getAlertColorResource(
                                daysUntilExpiry, DataToolbox.DEFAULT_ALERT_THRESHOLD))
                );
            }
        }
    }
}
