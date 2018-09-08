package com.zn.expirytracker.ui;

import android.content.Context;
import android.content.Intent;
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
import butterknife.OnClick;

/**
 * An adapter that holds food details
 */
public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewHolder> {

    private String[] mFoodNames;
    private long[] mExpiryDates;
    private int[] mCounts;
    private Storage[] mStorageLocs;
    private int[] mColors;

    private Context mContext;
    private long mCurrentTime;
    private FoodListAdapterClickListener mClickListener;

    interface FoodListAdapterClickListener {
        void onItemClicked(int position);
    }

    FoodListAdapter(Context context, String[] names, long[] dates, int[] counts, Storage[] locs,
                    int[] colors) {
        // TODO: Replace with Paging and LiveData implementation
        mFoodNames = names;
        Arrays.sort(dates);
        mExpiryDates = dates;
        mCounts = counts;
        mStorageLocs = locs;
        mColors = colors;

        mContext = context;
        mCurrentTime = System.currentTimeMillis();

        mClickListener = new FoodListAdapterClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.ARG_ITEM_POSITION_INT, position);
                mContext.startActivity(intent);
            }
        };
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        // TODO: Access Food objects
        holder.bind(mFoodNames[position], mExpiryDates[position], mCounts[position],
                mStorageLocs[position], mColors[position]);
    }

    @Override
    public int getItemCount() {
        return mFoodNames.length;
    }

    class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ncv_list_item_count_days)
        NumberCircleView mNcvCountDays;
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

        FoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bind(String name, long date, int count, Storage storage, int color) {
            mName.setText(name);
            mExpiryDate.setText(DataToolbox.getFormattedExpiryDateString(
                    mContext, mCurrentTime, date));
            mCount.setText(String.valueOf(count));
            mStorageIcon.setImageResource(DataToolbox.getStorageIconResource(storage));
            int daysUntilExpiry = DataToolbox.getNumDaysBetweenDates(mCurrentTime, date);
            mNcvCountDays.mTvValue.setText(String.valueOf(daysUntilExpiry));
            mNcvCountDays.mTvLabel.setText(mContext.getResources().getQuantityString(
                    R.plurals.food_days_label, daysUntilExpiry));
            mImage.setBackgroundColor(color);

            // Set the color of a drawable
            // https://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically
            mNcvCountDays.setOutlineWidthAndColor(mContext.getResources()
                            .getDimensionPixelSize(R.dimen.number_circle_outline_width),
                    ContextCompat.getColor(mContext, DataToolbox.getAlertColorResource(
                            daysUntilExpiry, DataToolbox.DEFAULT_ALERT_THRESHOLD)));
        }

        @Override
        @OnClick(R.id.item_food_layout)
        public void onClick(View view) {
            mClickListener.onItemClicked(getAdapterPosition());
        }
    }
}
