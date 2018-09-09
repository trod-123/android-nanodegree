package com.zn.expirytracker.ui;

import android.app.Activity;
import android.os.Bundle;
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
import com.zn.expirytracker.data.TestDataGen;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailFragment extends Fragment {

    public static final String ARG_ITEM_POSITION_INT = Toolbox.createStaticKeyString(
            "detail_fragment.item_position_int");

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
    @BindView(R.id.tv_detail_size)
    TextView mTvSize;
    @BindView(R.id.tv_detail_weight)
    TextView mTvWeight;
    @BindView(R.id.tv_detail_notes)
    TextView mTvNotes;
    @BindView(R.id.tv_detail_barcode)
    TextView mTvBarcode;
    @BindView(R.id.tv_detail_input)
    TextView mTvInput;
    @BindView(R.id.tv_detail_credit_upcitemdb)
    TextView mTvCreditUpcItemDb;
    @BindView(R.id.tv_detail_credit_googleimgrec)
    TextView mTvCreditGoogleImgRec;

    private DetailImagePagerAdapter mPagerAdapter;

    private Activity mHostActivity;
    private TestDataGen mDataGenerator;
    private DateTime mCurrentDateTimeStartOfDay;
    private int mItemPosition;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(int itemPosition) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_POSITION_INT, itemPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHostActivity = getActivity();
        mDataGenerator = TestDataGen.getInstance();
        mCurrentDateTimeStartOfDay = DataToolbox.getDateTimeStartOfDay(System.currentTimeMillis());

        Bundle args = getArguments();
        if (args != null) {
            // The position will determine the populated elements
            mItemPosition = args.getInt(ARG_ITEM_POSITION_INT, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_detail, container, false);
        Timber.tag(DetailFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        populateViewElements();

        return rootView;
    }

    private void populateViewElements() {
        // Image pager
        mPagerAdapter = new DetailImagePagerAdapter(getChildFragmentManager(),
                mDataGenerator.getColors());
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

        // Main layout
        mTvFoodName.setText(mDataGenerator.getFoodNameAt(mItemPosition));
        mTvExpiryDate.setText(DataToolbox.getFormattedExpiryDateString(
                mHostActivity, mCurrentDateTimeStartOfDay.getMillis(),
                mDataGenerator.getExpiryDateAt(mItemPosition)));
        mIvStorageIcon.setImageResource(DataToolbox.getStorageIconResource(
                mDataGenerator.getStorageLocAt(mItemPosition)));

        mNcvCount.mTvValue.setText(String.valueOf(mDataGenerator.getCountAt(mItemPosition)));
        mNcvCount.mTvLabel.setText(getString(R.string.food_count_label));
        int daysUntilExpiry = DataToolbox.getNumDaysBetweenDates(
                mCurrentDateTimeStartOfDay.getMillis(),
                mDataGenerator.getExpiryDateAt(mItemPosition));
        mNcvCountDays.mTvValue.setText(String.valueOf(daysUntilExpiry));
        mNcvCountDays.mTvLabel.setText(mHostActivity.getResources().getQuantityString(
                R.plurals.food_days_label, daysUntilExpiry));
        mNcvCountDays.setOutlineWidthAndColor(mHostActivity.getResources()
                        .getDimensionPixelSize(R.dimen.number_circle_outline_width),
                ContextCompat.getColor(mHostActivity, DataToolbox.getAlertColorResource(
                        daysUntilExpiry, DataToolbox.DEFAULT_ALERT_THRESHOLD)));
        mTvDescription.setText("The current item position is: " + mItemPosition);

        // Other info layout
        mTvBrand.setText("Kellogg's");
        mTvSize.setText("12\" x 10\"");
        mTvWeight.setText("200 lbs");
        mTvNotes.setText("What is 1 plus " + mItemPosition);

        // Meta data layout
        mTvBarcode.setText("123123123");
        mTvInput.setText("Scanned by WHAT??");
    }
}
