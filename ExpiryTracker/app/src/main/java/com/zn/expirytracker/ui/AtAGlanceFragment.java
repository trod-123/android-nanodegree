package com.zn.expirytracker.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import com.zn.expirytracker.data.WeeklyDateFilter;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.utils.AuthToolbox;
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
public class AtAGlanceFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.layout_at_a_glance_root)
    View mRootLayout;
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
    private FoodViewModel mViewModel;

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
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_at_a_glance, container, false);
        Timber.tag(AtAGlanceFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

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
                                            updateUI();
                                            updateViewModel();
                                        }
                                        return true;
                                    case R.id.action_filter_14_days:
                                        if (mCurrentFilter != WeeklyDateFilter.NEXT_14) {
                                            mCurrentFilter = WeeklyDateFilter.NEXT_14;
                                            updateUI();
                                            updateViewModel();
                                        }
                                        return true;
                                    case R.id.action_filter_21_days:
                                        if (mCurrentFilter != WeeklyDateFilter.NEXT_21) {
                                            mCurrentFilter = WeeklyDateFilter.NEXT_21;
                                            updateUI();
                                            updateViewModel();
                                        }
                                        return true;
                                }
                                return false;
                            }
                        });
            }
        });
        mRootLayout.setAlpha(0f); // hide the views until data is ready
        mViewModel.getAllFoods(true).observe(this, new Observer<PagedList<Food>>() {
            @Override
            public void onChanged(@Nullable PagedList<Food> foods) {
                // This should only be called once
                if (foods != null && foods.size() > 0) {
                    setupBarChart(foods);
                    resetChartXAxisMaximum();
                    updateSummary(mCurrentFilter, mBarChartEntries);
                    mRootLayout.animate().setDuration(500).alpha(1f);
                }
            }
        });
        updateViewModel();

        // Prepare the UI elements first - these can be done independently of data
        setupRecyclerView();
        updateGreeting();
        updateListHeader();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Register in onDestroy() so change can still be "heard" when fragment is in background
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateViewModel() {
        mViewModel.getAllFoodsExpiringBeforeDate(
                DataToolbox.getDateBoundsFromFilter(mCurrentFilter, mCurrentDateTimeStartOfDay),
                true).observe(this, new Observer<PagedList<Food>>() {
            @Override
            public void onChanged(@Nullable PagedList<Food> foods) {
                mListAdapter.submitList(foods);
                updateSummary(mCurrentFilter, mBarChartEntries); // this requires data
            }
        });
    }

    /**
     * Helper for setting up the bar chart
     * Documentation and instructions: https://github.com/PhilJay/MPAndroidChart
     */
    private void setupBarChart(List<Food> allFoods) {
        mBarChartEntries = DataToolbox.getBarEntries(allFoods, mCurrentDateTimeStartOfDay.getMillis());
        BarDataSet dataSet = new BarDataSet(mBarChartEntries, null);
        dataSet.setColor(ContextCompat.getColor(mHostActivity, R.color.colorAccent)); // set color of bars
        dataSet.setDrawValues(false); // don't show values for each point
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

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
//        xAxis.setAxisMaximum(6.5f); // overshoot to add padding for last day
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
    }

    private void setupRecyclerView() {
        mRvFoodList.setLayoutManager(new LinearLayoutManager(mHostActivity,
                LinearLayoutManager.VERTICAL, false));
        mRvFoodList.setHasFixedSize(false); // since size dynamically changes, this should be false
        mListAdapter = new FoodListAdapter(mHostActivity);
        mRvFoodList.setAdapter(mListAdapter);
        ViewCompat.setNestedScrollingEnabled(mRvFoodList, false);
    }

    private void updateUI() {
        resetChartXAxisMaximum();
        updateFabText();
        updateListHeader();
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

    private void updateChartHeader(int daysFromCurrent, int foodsCount) {
        mTvChartHeaderDate.animate().alpha(1f);
        mTvChartHeaderNumFoods.animate().alpha(1f);
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        String name;
        if (AuthToolbox.checkIfSignedIn()) {
            // Only show the display name if user is signed in. User is signed in if the "sign out"
            // preference is visible (true)
            name = sp.getString(getString(R.string.pref_account_display_name_key), null);
        } else {
            name = null;
        }
        mTvGreeting.setText(DataToolbox.getGreeting(mHostActivity, name,
                mCurrentDateTime));
    }

    private void updateSummary(WeeklyDateFilter filter, List<BarEntry> barChartEntries) {
        if (barChartEntries == null) {
            return;
        }
        int limit;
        switch (filter) {
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
        for (int i = 0; i < limit && i < barChartEntries.size(); i++) {
            totalFoodsCountFromFilter += barChartEntries.get(i).getY();
        }
        int foodsCountCurrent = (int) barChartEntries.get(0).getY();
        int foodsCountNextDay = (int) barChartEntries.get(1).getY();

        mTvSummary.setText(DataToolbox.getFullSummary(mHostActivity, filter,
                totalFoodsCountFromFilter, foodsCountCurrent, foodsCountNextDay));
    }

    private void updateListHeader() {
        mTvListHeader.setText(DataToolbox.getAtAGlanceListHeader(mHostActivity, mCurrentFilter));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_account_display_name_key)) ||
                key.equals(getString(R.string.pref_account_signed_in_key))) {
            updateGreeting();
        }
    }
}