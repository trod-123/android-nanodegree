package com.zn.expirytracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.TestDataGen;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String ARG_ITEM_POSITION_INT = Toolbox.createStaticKeyString(
            "detail_activity.item_position_int");

    @BindView(R.id.viewPager_detail)
    ViewPager mViewPager;

    private DetailPagerAdapter mPagerAdapter;
    private int mCurrentItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            mCurrentItemPosition = intent.getIntExtra(ARG_ITEM_POSITION_INT, 0);
        }

        mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), TestDataGen.getInstance());
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.pager_page_margin));
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentItemPosition, false);
    }
}
