package com.zn.expirytracker.ui;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.model.InputType;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailFragment extends Fragment {

    public static final String ARG_ITEM_ID_LONG = Toolbox.createStaticKeyString(
            DetailFragment.class, "item_id_long");

    /**
     * For restoring current image pager position after screen rotation
     */
    private static final String KEY_CURRENT_IMAGE_PAGER_POSITION =
            Toolbox.createStaticKeyString(DetailFragment.class, "current_image_pager_position");

    private static final int IMAGE_PAGER_POSITION_NOT_SET = -1;

    @BindView(R.id.viewPager_detail_image)
    ViewPager mViewPager;
    @BindView(R.id.iv_scrim_detail_image)
    ImageView mImageScrim;
    @BindView(R.id.pageIndicatorView_detail_image)
    PageIndicatorView mPageIndicatorView;
    @BindView(R.id.iv_detail_pager_empty)
    ImageView mIvPagerEmpty;
    @BindView(R.id.tv_detail_food_name)
    TextView mTvFoodName;
    @BindView(R.id.tv_detail_expiry_date)
    TextView mTvExpiryDate;
    @BindView(R.id.tv_detail_storage_label)
    TextView mTvStorage;
    @BindView(R.id.iv_detail_storage_icon)
    ImageView mIvStorageIcon;
    @BindView(R.id.ncv_detail_count_days)
    NumberCircleView mNcvCountDays;
    @BindView(R.id.ncv_detail_count)
    NumberCircleView mNcvCount;
    @BindView(R.id.tv_detail_description)
    TextView mTvDescription;
    @BindView(R.id.layout_date_calendar)
    View mCalendarLayout;
    @BindView(R.id.tv_date_calendar_day)
    TextView mTvCalendarDay;
    @BindView(R.id.tv_date_calendar_month)
    TextView mTvCalendarMonth;

    @BindView(R.id.border_detail_other_info)
    View mBorderOtherInfo;
    @BindView(R.id.tv_detail_other_info_label)
    TextView mTvOtherInfoLabel;

    @BindView(R.id.tv_detail_brand)
    TextView mTvBrand;
    @BindView(R.id.iv_detail_brand)
    ImageView mIvBrand;
    @BindView(R.id.tv_detail_brand_label)
    TextView mTvBrandLabel;

    @BindView(R.id.tv_detail_size)
    TextView mTvSize;
    @BindView(R.id.iv_detail_size)
    ImageView mIvSize;
    @BindView(R.id.tv_detail_size_label)
    TextView mTvSizeLabel;

    @BindView(R.id.tv_detail_weight)
    TextView mTvWeight;
    @BindView(R.id.iv_detail_weight)
    ImageView mIvWeight;
    @BindView(R.id.tv_detail_weight_label)
    TextView mTvWeightLabel;

    @BindView(R.id.tv_detail_notes)
    TextView mTvNotes;
    @BindView(R.id.iv_detail_notes)
    ImageView mIvNotes;
    @BindView(R.id.tv_detail_notes_label)
    TextView mTvNotesLabel;

    @BindView(R.id.tv_detail_barcode)
    TextView mTvBarcode;
    @BindView(R.id.iv_detail_barcode)
    ImageView mIvBarcode;
    @BindView(R.id.tv_detail_barcode_label)
    TextView mTvBarcodeLabel;

    @BindView(R.id.tv_detail_input)
    TextView mTvInput;
    @BindView(R.id.iv_detail_input)
    ImageView mIvInput;
    @BindView(R.id.tv_detail_input_label)
    TextView mTvInputLabel;

    @BindView(R.id.tv_detail_credit_upcitemdb)
    TextView mTvCreditUpcItemDb;
    @BindView(R.id.tv_detail_credit_googleimgrec)
    TextView mTvCreditGoogleImgRec;

    private DetailImagePagerAdapter mPagerAdapter;

    private Activity mHostActivity;
    private FoodViewModel mViewModel;
    private DateTime mCurrentDateTimeStartOfDay;
    private long mItemId;

    // for savedInstanceState
    private int mCurrentImagePosition = IMAGE_PAGER_POSITION_NOT_SET;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_CURRENT_IMAGE_PAGER_POSITION, mCurrentImagePosition);
        super.onSaveInstanceState(outState);
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(long itemId) {
        DetailFragment fragment = new DetailFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID_LONG, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHostActivity = getActivity();
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        mCurrentDateTimeStartOfDay = DataToolbox.getDateTimeStartOfDay(System.currentTimeMillis());

        Bundle args = getArguments();
        if (args != null) {
            mItemId = args.getLong(ARG_ITEM_ID_LONG, 0);
        }

        if (savedInstanceState != null) {
            mCurrentImagePosition = savedInstanceState.getInt(KEY_CURRENT_IMAGE_PAGER_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_detail, container, false);
        Timber.tag(DetailFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        final LiveData<Food> liveData = mViewModel.getSingleFoodById(mItemId, false);
        liveData.observe(this, new Observer<Food>() {
            @Override
            public void onChanged(@Nullable Food food) {
                if (food != null) {
                    populateViewElements(food);
                } else {
                    // Called when a fragment is removed. Leaves fragment with an unpopulated shell
                }
                liveData.removeObserver(this);
            }
        });

        // Image pager
        mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager(), false,
                Toolbox.isLeftToRightLayout());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Keep the current selected position in sync between ViewPager and PageIndicator
                mPageIndicatorView.setSelection(position);
                mCurrentImagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        // Fade in the indicator view while dragging
                        Toolbox.showPageIndicator(true, mImageScrim, mPageIndicatorView);
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                    default:
                        // Fade out when idle
                        Toolbox.showPageIndicator(false, mImageScrim, mPageIndicatorView);
                        break;
                }
            }
        });

        return rootView;
    }

    private void populateViewElements(@NonNull Food food) {
        // ViewPager
        @Nullable List<String> images = food.getImages();
        mPagerAdapter.setImageUris(images);
        mPagerAdapter.notifyDataSetChanged(); // call again out here to invalidate views
        mViewPager.setCurrentItem(mCurrentImagePosition != IMAGE_PAGER_POSITION_NOT_SET ?
                mCurrentImagePosition : images != null && !Toolbox.isLeftToRightLayout() ?
                images.size() - 1 : 0, false);
        Toolbox.showView(mIvPagerEmpty, images == null || images.isEmpty(), false);
        mViewPager.setContentDescription(food.getFoodName());

        // Main layout
        mTvFoodName.setText(food.getFoodName());
        mTvExpiryDate.setText(DataToolbox.getFormattedExpiryDateString(
                mHostActivity, mCurrentDateTimeStartOfDay.getMillis(), food.getDateExpiry()));
        DateTime dateTime = new DateTime(food.getDateExpiry());
        mTvCalendarDay.setText(String.valueOf(dateTime.getDayOfMonth()));
        mTvCalendarMonth.setText(dateTime.monthOfYear().getAsShortText());
        mCalendarLayout.setContentDescription(getString(R.string.expiry_msg_month_day,
                mTvCalendarMonth.getText(), Integer.parseInt(mTvCalendarDay.getText().toString())));

        @Nullable Storage storage = food.getStorageLocation();
        if (storage != null && storage != Storage.NOT_SET) {
            mTvStorage.setText(getString(R.string.storage_location_description,
                    DataToolbox.getStorageIconString(storage, mHostActivity).toLowerCase()));
            mIvStorageIcon.setImageResource(DataToolbox.getStorageIconResource(storage));
            mIvStorageIcon.setContentDescription(mHostActivity.getString(
                    R.string.storage_location_description,
                    DataToolbox.getStorageIconString(storage, mHostActivity)));
        } else {
            // Don't show text-icon if storage not set
            mTvStorage.setVisibility(View.GONE);
            mIvStorageIcon.setVisibility(View.GONE);
        }

        mNcvCount.mTvValue.setText(String.valueOf(food.getCount()));
        mNcvCount.mTvLabel.setText(getString(R.string.food_count_label));
        mNcvCount.updateContentDescription(R.string.food_msg_count);
        int daysUntilExpiry = DataToolbox.getNumDaysBetweenDates(
                mCurrentDateTimeStartOfDay.getMillis(),
                food.getDateExpiry());
        mNcvCountDays.mTvValue.setText(String.valueOf(daysUntilExpiry));
        mNcvCountDays.mTvLabel.setText(mHostActivity.getResources().getQuantityString(
                R.plurals.food_days_label, daysUntilExpiry));
        mNcvCountDays.updateContentDescription(R.string.expiry_msg_num_days);
        mNcvCountDays.setOutlineWidthAndColor(mHostActivity.getResources()
                        .getDimensionPixelSize(R.dimen.number_circle_outline_width),
                ContextCompat.getColor(mHostActivity, DataToolbox.getAlertColorResource(
                        daysUntilExpiry, DataToolbox.DEFAULT_ALERT_THRESHOLD)));

        @Nullable String description = food.getDescription();
        mTvDescription.setText(description);
        mTvDescription.setVisibility(
                description == null || description.isEmpty() ? View.GONE : View.VISIBLE);

        // Other info layout
        @Nullable String brandName = food.getBrandName();
        mTvBrand.setText(brandName);
        setInfoVisibility(mIvBrand, mTvBrandLabel, mTvBrand,
                brandName != null && !brandName.isEmpty());
        @Nullable String size = food.getSize();
        mTvSize.setText(size);
        setInfoVisibility(mIvSize, mTvSizeLabel, mTvSize,
                size != null && !size.isEmpty());
        @Nullable String weight = food.getWeight();
        mTvWeight.setText(weight);
        setInfoVisibility(mIvWeight, mTvWeightLabel, mTvWeight,
                weight != null && !weight.isEmpty());
        @Nullable String notes = food.getNotes();
        mTvNotes.setText(notes);
        setInfoVisibility(mIvNotes, mTvNotesLabel, mTvNotes,
                notes != null && !notes.isEmpty());

        // Hide the "Other info" section if all of its fields are blank
        boolean hide = (brandName == null || brandName.isEmpty()) && (size == null || size.isEmpty()) &&
                (weight == null || weight.isEmpty()) && (notes == null || notes.isEmpty());
        mTvOtherInfoLabel.setVisibility(hide ? View.GONE : View.VISIBLE);
        mBorderOtherInfo.setVisibility(hide ? View.GONE : View.VISIBLE);

        // Meta data layout
        @Nullable String barcode = food.getBarcode();
        mTvBarcode.setText(barcode);
        setInfoVisibility(mIvBarcode, mTvBarcodeLabel, mTvBarcode,
                barcode != null && !barcode.isEmpty());
        @Nullable InputType inputType = food.getInputType();
        if (inputType != null) {
            switch (inputType) {
                case BARCODE:
                    mTvInput.setText(R.string.food_input_type_barcode);
                    mTvCreditUpcItemDb.setVisibility(View.VISIBLE);
                    break;
                case IMG_REC:
                    mTvInput.setText(R.string.food_input_type_imgrec);
                    mTvCreditGoogleImgRec.setVisibility(View.VISIBLE);
                    break;
                case IMG_ONLY:
                    mTvInput.setText(R.string.food_input_type_imgonly);
                    break;
                case TEXT_ONLY:
                    mTvInput.setText(R.string.food_input_type_textonly);
                    break;
            }
        }
        setInfoVisibility(mIvInput, mTvInputLabel, mTvInput, inputType != null);
    }

    private void setInfoVisibility(ImageView iconView, TextView labelView, TextView valueView,
                                   boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        iconView.setVisibility(visibility);
        labelView.setVisibility(visibility);
        valueView.setVisibility(visibility);
    }
}
