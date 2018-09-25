package com.zn.expirytracker.ui;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * An adapter that holds food details
 */
public class FoodListAdapter extends PagedListAdapter<Food, FoodListAdapter.FoodViewHolder> {

    private Context mContext;
    private long mCurrentTime;
    private FoodListAdapterClickListener mClickListener;

    interface FoodListAdapterClickListener {
        void onItemClicked(long itemId);
    }

    private static DiffUtil.ItemCallback<Food> DIFF_CALLBACK = new DiffUtil.ItemCallback<Food>() {
        @Override
        public boolean areItemsTheSame(@NonNull Food oldFood, @NonNull Food newFood) {
            // Although task details are changed if reloaded from database, id is the same
            return oldFood.get_id() == newFood.get_id();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Food oldFood, @NonNull Food newFood) {
            return oldFood.equals(newFood);
        }
    };

    FoodListAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
        mCurrentTime = System.currentTimeMillis();

        mClickListener = new FoodListAdapterClickListener() {
            @Override
            public void onItemClicked(long itemId) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.ARG_ITEM_ID_LONG, itemId);
                mContext.startActivity(intent);
            }
        };
    }

    public Food getFoodAtPosition(int position) {
        if (getItemCount() > 0) {
            return getItem(position);
        } else {
            // If null, then item is a placeholder, so once the actual object is loaded,
            // PagedListAdapter automatically invalidates this row
            return null;
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = getItem(position);
        if (food != null) {
            holder.bind(food);
        } else {
            holder.clear();
        }
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
        @BindView(R.id.pb_list_item_image)
        ProgressBar mPb;
        @BindView(R.id.iv_list_item_image_broken)
        ImageView mIvBroken;

        FoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bind(Food food) {
            mName.setText(food.getFoodName());
            mExpiryDate.setText(DataToolbox.getFormattedExpiryDateString(
                    mContext, mCurrentTime, food.getDateExpiry()));
            int count = food.getCount();
            if (count != 1) {
                mCount.setVisibility(View.VISIBLE);
                mCount.setText("x" + String.valueOf(count));
            } else {
                mCount.setVisibility(View.GONE);
            }
            Storage storageLocation = food.getStorageLocation();
            if (storageLocation != Storage.NOT_SET) {
                // don't show icon if no storage set
                mStorageIcon.setVisibility(View.VISIBLE);
                mStorageIcon.setImageResource(DataToolbox.getStorageIconResource(storageLocation));
            } else {
                mStorageIcon.setVisibility(View.GONE);
            }
            int daysUntilExpiry =
                    DataToolbox.getNumDaysBetweenDates(mCurrentTime, food.getDateExpiry());
            mNcvCountDays.mTvValue.setText(String.valueOf(daysUntilExpiry));
            mNcvCountDays.mTvLabel.setText(mContext.getResources().getQuantityString(
                    R.plurals.food_days_label, daysUntilExpiry));

            // Load image only if there is any
            List<String> images = food.getImages();
            if (images != null && !images.isEmpty()) {
                mImage.setVisibility(View.VISIBLE);
                Toolbox.showView(mPb, true, false);
                Toolbox.loadImageFromUrl(mContext, images.get(0), mImage, new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Timber.e(e, "Error loading image in adapter");
                        Toolbox.showView(mPb, false, false);
                        Toolbox.showView(mIvBroken, true, false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Toolbox.showView(mPb, false, false);
                        Toolbox.showView(mIvBroken, false, false);
                        return false;
                    }
                });
            } else {
                mImage.setVisibility(View.GONE);
                Toolbox.showView(mIvBroken, false, false);
            }

            // Set the color of a drawable
            // https://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically
            mNcvCountDays.setOutlineWidthAndColor(mContext.getResources()
                            .getDimensionPixelSize(R.dimen.number_circle_outline_width),
                    ContextCompat.getColor(mContext, DataToolbox.getAlertColorResource(
                            daysUntilExpiry, DataToolbox.DEFAULT_ALERT_THRESHOLD)));
        }

        private void clear() {
            mName.setText("");
            mExpiryDate.setText("");
            mCount.setText("");
            mStorageIcon.setImageDrawable(null);
            mImage.setImageDrawable(null);
            mNcvCountDays.mTvValue.setText("");
            mNcvCountDays.mTvLabel.setText("");
            mNcvCountDays.setOutlineWidthAndColor(mContext.getResources()
                            .getDimensionPixelSize(R.dimen.number_circle_outline_width),
                    ContextCompat.getColor(mContext, R.color.expires_alert_none));
        }

        @Override
        @OnClick(R.id.item_food_layout)
        public void onClick(View view) {
            Food food = getItem(getAdapterPosition());
            if (food != null) {
                mClickListener.onItemClicked(food.get_id());
            } else {
                Timber.e("The food was null");
            }
        }
    }
}
