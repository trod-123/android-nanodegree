package com.zn.expirytracker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.TestDataGen;
import com.zn.expirytracker.data.WeeklyDateFilter;
import com.zn.expirytracker.data.model.Storage;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Provides summary information
 */
public class AtAGlanceFragment extends Fragment {

    @BindView(R.id.chart_at_a_glance)
    BarChart mBarChart;
    @BindView(R.id.rv_glance_food_list)
    RecyclerView mRvFoodList;
    @BindView(R.id.tv_chart_header_num_foods)
    TextView mTvChartHeaderNumFoods;
    @BindView(R.id.tv_chart_header_date)
    TextView mTvChartHeaderDate;
    @BindView(R.id.tv_at_a_glance_greeting)
    TextView mTvGreeting;
    @BindView(R.id.tv_at_a_glance_summary)
    TextView mTvSummary;
    @BindView(R.id.tv_at_a_glance_list_header)
    TextView mTvListHeader;
    @BindView(R.id.fab_chart_date_range)
    View mFabChartDateRange;
    @BindView(R.id.tv_fab_chart_date_range)
    TextView mTvChartDateRange;

    private List<BarEntry> mBarChartEntries;

    private FoodListAdapter mListAdapter;
    private String[] mNames;
    private long[] mDates;
    private int[] mCounts;
    private Storage[] mLocs;

    private Activity mHostActivity;
    private DateTime mCurrentDateTimeStartOfDay; // for dates calculations
    private long mCurrentDateTime; // for when the exact time is needed
    private WeeklyDateFilter mCurrentFilter;

    public AtAGlanceFragment() {
        // Required empty public constructor
    }

    /**
     * TODO: Temp, for if we need to pass any arguments
     *
     * @return
     */
    public static AtAGlanceFragment newInstance() {
        AtAGlanceFragment fragment = new AtAGlanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHostActivity = getActivity();
        mCurrentDateTime = System.currentTimeMillis();
        mCurrentDateTimeStartOfDay = DataToolbox.getDateTimeStartOfDay(mCurrentDateTime);
        mCurrentFilter = WeeklyDateFilter.NEXT_7;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_at_a_glance, container, false);
        Timber.tag(AtAGlanceFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        mNames = TestDataGen.getFoodNames();
        mDates = TestDataGen.getExpiryDates();
        mCounts = TestDataGen.getCounts();
        mLocs = TestDataGen.getStorageLocs();

        // Set up the listener for the extended fab. Only take action if the filter changes
        mFabChartDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toolbox.showMenuPopup(mHostActivity, mFabChartDateRange,
                        R.menu.menu_glance_date_filter, new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_filter_7_days:
                                        if (mCurrentFilter != WeeklyDateFilter.NEXT_7) {
                                            mCurrentFilter = WeeklyDateFilter.NEXT_7;
                                            resetChartXAxisMaximum();
                                            updateFabText();
                                            updateSummary();
                                            updateListHeader();
                                        }
                                        return true;
                                    case R.id.action_filter_14_days:
                                        if (mCurrentFilter != WeeklyDateFilter.NEXT_14) {
                                            mCurrentFilter = WeeklyDateFilter.NEXT_14;
                                            resetChartXAxisMaximum();
                                            updateFabText();
                                            updateSummary();
                                            updateListHeader();
                                        }
                                        return true;
                                    case R.id.action_filter_21_days:
                                        if (mCurrentFilter != WeeklyDateFilter.NEXT_21) {
                                            mCurrentFilter = WeeklyDateFilter.NEXT_21;
                                            resetChartXAxisMaximum();
                                            updateFabText();
                                            updateSummary();
                                            updateListHeader();
                                        }
                                        return true;
                                }
                                return false;
                            }
                        });
            }
        });

        setupBarChart();
        updateFabText();
        setupRecyclerView();
        updateGreeting();
        updateSummary();
        updateListHeader();

        return rootView;
    }

    /**
     * Helper for setting up the bar chart
     * Documentation and instructions: https://github.com/PhilJay/MPAndroidChart
     */
    private void setupBarChart() {
        // Data elements
        // TODO: Figure out how to handle the app calling System.currentTimeMillis() from different places
        // Need to find a viable and consistent way of reading this value across the app so counts
        // are consistent in all screens, but still up to date, in case user is using the app
        // through midnight
        // TODO: Implement under LiveData (data should already be filtered before it goes here)
        mBarChartEntries = DataToolbox.getTestChartValues(mDates, mCurrentDateTimeStartOfDay.getMillis());
        BarDataSet dataSet = new BarDataSet(mBarChartEntries, null);
        dataSet.setColor(ContextCompat.getColor(mHostActivity, R.color.colorAccent)); // set color of bars
        dataSet.setDrawValues(false); // don't show values for each point
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        // TODO: TEMP, FIGURE OUT HOW TO REDUCE THE LOADING LAG
        mBarChart.setLogEnabled(true);

        mBarChart.setData(barData);
        mBarChart.setNoDataText(getString(R.string.at_a_glance_bar_chart_empty));

        // Label elements
        mBarChart.getDescription().setEnabled(false);
        mBarChart.getLegend().setEnabled(false);

        // Touch elements
        mBarChart.setDragEnabled(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setDoubleTapToZoomEnabled(false);

        // Axis elements
        YAxis axisLeft = mBarChart.getAxisLeft();
        axisLeft.setAxisMinimum(0); // by default, minimum is auto-calculated
        axisLeft.setDrawAxisLine(false); // don't draw the y axis
        axisLeft.setAxisMaximum(axisLeft.getAxisMaximum() + (float) Math.ceil(axisLeft.getAxisMaximum() * 0.10));
        axisLeft.setGranularity(1); // limit labels only to whole numbers
        axisLeft.setTextColor(ContextCompat.getColor(mHostActivity, R.color.textColorPrimaryLight));
        axisLeft.setTextSize(12f);

        mBarChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setAxisMinimum(-0.5f); // overshoot to add padding for first day
        xAxis.setAxisMaximum(6.5f); // overshoot to add padding for last day
        xAxis.setDrawGridLines(false); // hide the vertical lines for each x axis value
        xAxis.setAxisLineWidth(2); // make the x axis thicker than others
        xAxis.setAxisLineColor(ContextCompat.getColor(mHostActivity, R.color.colorPrimary));
        xAxis.setGranularity(1); // limit labels only to whole numbers
        xAxis.setTextColor(ContextCompat.getColor(mHostActivity, R.color.textColorPrimaryLight));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Show Sun, Mon, Tue... on the x axis
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DataToolbox.getFormattedShortDateString(mCurrentDateTimeStartOfDay, (int) value);
            }
        });

        // Highlighting
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                updateChartHeader((int) e.getX(), (int) e.getY());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        mBarChart.highlightValue(0, 0); // Automatically highlight the first value
        mBarChart.animateY(500);
    }

    private void updateFabText() {
        String text;
        switch (mCurrentFilter) {
            case NEXT_7:
                text = getString(R.string.date_weekly_filter_7_days);
                break;
            case NEXT_14:
                text = getString(R.string.date_weekly_filter_14_days);
                break;
            case NEXT_21:
                text = getString(R.string.date_weekly_filter_21_days);
                break;
            default:
                text = getString(R.string.at_a_glance_date_range_label);
        }
        mTvChartDateRange.setText(text);
    }

    private void resetChartXAxisMaximum() {
        float axisMaximum;
        switch (mCurrentFilter) {
            case NEXT_7:
                axisMaximum = 6.5f;
                break;
            case NEXT_14:
                axisMaximum = 13.5f;
                break;
            case NEXT_21:
                axisMaximum = 20.5f;
                break;
            default:
                axisMaximum = 6.5f;
        }
        mBarChart.getXAxis().setAxisMaximum(axisMaximum);
        mBarChart.zoomOut();
        mBarChart.animateY(1000);
    }

    private void setupRecyclerView() {
        mRvFoodList.setLayoutManager(new LinearLayoutManager(mHostActivity,
                LinearLayoutManager.VERTICAL, false));
        mRvFoodList.setHasFixedSize(true);
        // TODO: Implement under LiveData
        mListAdapter = new FoodListAdapter(mNames, mDates, mCounts, mLocs);
        mRvFoodList.setAdapter(mListAdapter);
        ViewCompat.setNestedScrollingEnabled(mRvFoodList, false);
    }

    private void updateChartHeader(int daysFromCurrent, int foodsCount) {
        mTvChartHeaderDate.setText(
                DataToolbox.getFormattedLessShortDateString(mCurrentDateTimeStartOfDay, daysFromCurrent));
        if (foodsCount != 0) {
            mTvChartHeaderNumFoods.setText(mHostActivity.getResources().getQuantityString(
                    R.plurals.at_a_glance_bar_chart_header_count, foodsCount, foodsCount,
                    DataToolbox.getFormattedRelativeDateString(mHostActivity, mCurrentDateTimeStartOfDay,
                            daysFromCurrent)));
        } else {
            mTvChartHeaderNumFoods.setText(getString(
                    R.string.at_a_glance_bar_chart_header_count_none, null,
                    DataToolbox.getFormattedRelativeDateString(mHostActivity, mCurrentDateTimeStartOfDay,
                            daysFromCurrent)));
        }
    }

    private void updateGreeting() {
        mTvGreeting.setText(DataToolbox.getGreeting(mHostActivity, "Teddy",
                mCurrentDateTime));
    }

    private void updateSummary() {
        int limit;
        switch (mCurrentFilter) {
            case NEXT_7:
                limit = 7;
                break;
            case NEXT_14:
                limit = 14;
                break;
            case NEXT_21:
            default:
                limit = 21;
        }
        int totalFoodsCountFromFilter = 0;
        for (int i = 0; i < limit && i < mBarChartEntries.size(); i++) {
            totalFoodsCountFromFilter += mBarChartEntries.get(i).getY();
        }
        int foodsCountCurrent = (int) mBarChartEntries.get(0).getY();
        int foodsCountNextDay = (int) mBarChartEntries.get(1).getY();

        mTvSummary.setText(DataToolbox.getFullSummary(mHostActivity, mCurrentFilter,
                totalFoodsCountFromFilter, foodsCountCurrent, foodsCountNextDay));
    }

    private void updateListHeader() {
        mTvListHeader.setText(DataToolbox.getAtAGlanceListHeader(mHostActivity, mCurrentFilter));
    }
}