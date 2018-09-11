package com.zn.expirytracker.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
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
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailFragment extends Fragment {

    public static final String ARG_ITEM_ID_LONG = Toolbox.createStaticKeyString(
            "detail_fragment.item_id_long");

    @BindView(R.id.viewPager_detail_image)
    ViewPager mViewPager;
    @BindView(R.id.iv_scrim_detail_image)
    ImageView mImageScrim;
    @BindView(R.id.pageIndicatorView_detail_image)
    PageIndicatorView mPageIndicatorView;
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
    @BindView(R.id.tv_detail_credit_upcitemdb)
    TextView mTvCreditUpcItemDb;
    @BindView(R.id.tv_detail_credit_googleimgrec)
    TextView mTvCreditGoogleImgRec;

    private DetailImagePagerAdapter mPagerAdapter;

    private Activity mHostActivity;
    private FoodViewModel mViewModel;
    private DateTime mCurrentDateTimeStartOfDay;
    private long mItemId;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_detail, container, false);
        Timber.tag(DetailFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        mViewModel.getSingleFoodById(mItemId, false).observe(this, new Observer<Food>() {
            @Override
            public void onChanged(@Nullable Food food) {
                mPagerAdapter.setImageUris(food.getImages());
                populateViewElements(food);
            }
        });

        // Image pager
        mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Keep the current selected position in sync between ViewPager and PageIndicator
                mPageIndicatorView.setSelection(position);
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

    private void populateViewElements(Food food) {
        // Main layout
        mTvFoodName.setText(food.getFoodName());
        mTvExpiryDate.setText(DataToolbox.getFormattedExpiryDateString(
                mHostActivity, mCurrentDateTimeStartOfDay.getMillis(), food.getDateExpiry()));
        Storage storage = food.getStorageLocation();
        mTvStorage.setText(getString(R.string.storage_location_description,
                DataToolbox.getStorageIconString(storage, mHostActivity).toLowerCase()));
        mIvStorageIcon.setImageResource(DataToolbox.getStorageIconResource(storage));

        mNcvCount.mTvValue.setText(String.valueOf(food.getCount()));
        mNcvCount.mTvLabel.setText(getString(R.string.food_count_label));
        int daysUntilExpiry = DataToolbox.getNumDaysBetweenDates(
                mCurrentDateTimeStartOfDay.getMillis(),
                food.getDateExpiry());
        mNcvCountDays.mTvValue.setText(String.valueOf(daysUntilExpiry));
        mNcvCountDays.mTvLabel.setText(mHostActivity.getResources().getQuantityString(
                R.plurals.food_days_label, daysUntilExpiry));
        mNcvCountDays.setOutlineWidthAndColor(mHostActivity.getResources()
                        .getDimensionPixelSize(R.dimen.number_circle_outline_width),
                ContextCompat.getColor(mHostActivity, DataToolbox.getAlertColorResource(
                        daysUntilExpiry, DataToolbox.DEFAULT_ALERT_THRESHOLD)));

        String description = food.getDescription();
        mTvDescription.setText(description);
//        mTvDescription.setVisibility(description.isEmpty() ? View.GONE : View.VISIBLE);

        // Other info layout
        String brandName = food.getBrandName();
        mTvBrand.setText(brandName);
//        setInfoVisibility(mIvBrand, mTvBrandLabel, mTvBrand, !brandName.isEmpty());
        String size = food.getSize();
        mTvSize.setText(size);
//        setInfoVisibility(mIvSize, mTvSizeLabel, mTvSize, !size.isEmpty());
        String weight = food.getWeight();
        mTvWeight.setText(weight);
//        setInfoVisibility(mIvWeight, mTvWeightLabel, mTvWeight, !weight.isEmpty());
        String notes = food.getNotes();
        mTvNotes.setText(notes);
//        setInfoVisibility(mIvNotes, mTvNotesLabel, mTvNotes, !notes.isEmpty());

        // Meta data layout
        String barcode = food.getBarcode();
        mTvBarcode.setText(barcode);
//        setInfoVisibility(mIvBarcode, mTvBarcodeLabel, mTvBarcode, !barcode.isEmpty());
        mTvInput.setText(food.getInputType().toString());
//        switch (food.getInputType()) {
//            case BARCODE:
//                mTvCreditUpcItemDb.setVisibility(View.VISIBLE);
//                break;
//            case IMG_REC:
//                mTvCreditGoogleImgRec.setVisibility(View.VISIBLE);
//                break;
//        }
    }

    private void setInfoVisibility(ImageView iconView, TextView labelView, TextView valueView, boolean show) {
        int visibility = show ? View.VISIBLE : View.INVISIBLE;
        iconView.setVisibility(visibility);
        labelView.setVisibility(visibility);
        valueView.setVisibility(visibility);
    }
}
