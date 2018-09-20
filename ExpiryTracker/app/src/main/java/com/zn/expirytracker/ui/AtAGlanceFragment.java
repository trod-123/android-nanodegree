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
import android.support.v7.widget.DividerItemDecoration;
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

    private List<BarEntry> mFullList_barChartEntries;

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
                                            updateUI_dataIndependent(
                                                    mCurrentFilter = WeeklyDateFilter.NEXT_7);
                                            updateViewModelWithFilter(mCurrentFilter);
                                        }
                                        return true;
                                    case R.id.action_filter_14_days:
                                        if (mCurrentFilter != WeeklyDateFilter.NEXT_14) {
                                            updateUI_dataIndependent(
                                                    mCurrentFilter = WeeklyDateFilter.NEXT_14);
                                            updateViewModelWithFilter(mCurrentFilter);
                                        }
                                        return true;
                                    case R.id.action_filter_21_days:
                                        if (mCurrentFilter != WeeklyDateFilter.NEXT_21) {
                                            updateUI_dataIndependent(
                                                    mCurrentFilter = WeeklyDateFilter.NEXT_21);
                                            updateViewModelWithFilter(mCurrentFilter);
                                        }
                                        return true;
                                }
                                return false;
                            }
                        });
            }
        });
        mRootLayout.setAlpha(0f); // hide the views until data is ready TODO: Add progress bar
        updateViewModelWithFilter(mCurrentFilter);

        // Prepare the UI elements first - these can be done independently of data
        setupBarChart();
        setupRecyclerView();
        updateGreeting(mCurrentDateTime);

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

    /**
     * Helper for updating data for the list ONLY, not the bar chart, based on the {@code filter}
     * provided
     */
    private void updateViewModelWithFilter(WeeklyDateFilter filter) {
        mViewModel.getAllFoodsExpiringBeforeDate(
                DataToolbox.getDateBoundsFromFilter(filter, mCurrentDateTimeStartOfDay),
                true).observe(this, new Observer<PagedList<Food>>() {
            @Override
            public void onChanged(@Nullable PagedList<Food> foods) {
                if (foods != null) {
                    loadBarChartData(foods);
                    mListAdapter.submitList(foods);
                    resetXAxisMaximum(mCurrentFilter);
                    resetYAxisMaximum(foods, mCurrentDateTimeStartOfDay.getMillis());
                    updateListHeader(foods, mCurrentFilter);
                    updateSummary(mCurrentFilter, mFullList_barChartEntries);
                    mRootLayout.animate().setDuration(500).alpha(1f);
                }
            }
        });
    }

    /**
     * TODO: Encapsulate determination for showing or hiding the bar chart, based not only on
     * whether the list of bar entries is empty, but also if the highest frequency is 0. This is
     * equivalent to having no data at all
     * <p>
     * Currently, the bar chart shows even when the highest frequency is 0
     */
    private boolean showBarChart(List<Food> allFoods) {
        return false;
    }

    /**
     * Loads the barchart with data
     *
     * @param allFoods
     */
    private void loadBarChartData(List<Food> allFoods) {
        mFullList_barChartEntries = DataToolbox.getBarEntries(allFoods, mCurrentDateTimeStartOfDay.getMillis());
        if (mFullList_barChartEntries.size() > 0) {
            BarDataSet dataSet = new BarDataSet(mFullList_barChartEntries, null);
            dataSet.setColor(ContextCompat.getColor(mHostActivity, R.color.colorAccent)); // set color of bars
            dataSet.setDrawValues(false); // don't show values for each point
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.6f);
            mBarChart.setData(barData);

            mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    updateChartHeader((int) e.getX(), (int) e.getY());
                }

                @Override
                public void onNothingSelected() {

                }
            });
            mBarChart.highlightValue(0, 0);
        } else {
            mBarChart.setData(null);
            mBarChart.setNoDataText(getString(R.string.expiring_food_none));
            showEmptyChartHeader();
        }
    }

    /**
     * Helper for setting up the bar chart without data. Needs to only be done once
     * Documentation and instructions: https://github.com/PhilJay/MPAndroidChart
     */
    private void setupBarChart() {
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
        axisLeft.setGranularity(1); // limit labels only to whole numbers
        axisLeft.setTextColor(ContextCompat.getColor(mHostActivity, R.color.textColorPrimaryLight));
        axisLeft.setTextSize(12f);

        mBarChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setAxisMinimum(-0.5f); // overshoot to add padding for first day
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
    }

    /**
     * Sets up the recylerview and the adapter. No data is loaded here.
     */
    private void setupRecyclerView() {
        mRvFoodList.setLayoutManager(new LinearLayoutManager(mHostActivity,
                LinearLayoutManager.VERTICAL, false));
        mRvFoodList.setHasFixedSize(false); // since size dynamically changes, this should be false
        mListAdapter = new FoodListAdapter(mHostActivity);
        mRvFoodList.setAdapter(mListAdapter);
        mRvFoodList.addItemDecoration(new DividerItemDecoration(mHostActivity, DividerItemDecoration.VERTICAL));
        ViewCompat.setNestedScrollingEnabled(mRvFoodList, false);
    }

    /**
     * Helper that calls UI update methods that only requires the updated filter. This does not
     * require any data. These are run before any data is loaded
     */
    private void updateUI_dataIndependent(WeeklyDateFilter filter) {
        resetXAxisMaximum(filter);
        updateFabText(filter);
    }

    /**
     * Stretches the X axis based on the current filter
     */
    private void resetXAxisMaximum(WeeklyDateFilter filter) {
        float xAxisMaximum;
        switch (filter) {
            case NEXT_7:
                xAxisMaximum = 6.5f;
                break;
            case NEXT_14:
                xAxisMaximum = 13.5f;
                break;
            case NEXT_21:
                xAxisMaximum = 20.5f;
                break;
            default:
                xAxisMaximum = 6.5f;
        }
        mBarChart.getXAxis().setAxisMaximum(xAxisMaximum);
        mBarChart.zoomOut();
        mBarChart.animateY(1000);
    }

    /**
     * Stretches the Y axis based on the highest frequency value of the food list.
     * {@code baseDateTime} is needed to get the daily frequencies of expiring food items
     */
    private void resetYAxisMaximum(List<Food> filteredFoodList, long baseDateTime) {
        int highestFrequency = DataToolbox.getHighestDailyFrequency(
                filteredFoodList, baseDateTime);
        float yMax = highestFrequency + (float) Math.ceil(highestFrequency * 0.10);
        mBarChart.getAxisLeft().setAxisMaximum(yMax > 0 ? yMax : 2);
        mBarChart.zoomOut(); // calling this is needed to reset the axes
    }

    private void updateFabText(WeeklyDateFilter filter) {
        String text;
        switch (filter) {
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

    private void showEmptyChartHeader() {
        mTvChartHeaderDate.setText("");
        mTvChartHeaderNumFoods.setText(R.string.expiring_food_none);
    }

    /**
     * Updates the time-based greeting based on the user's current time. Note this is not the
     * {@code baseDateTime} where hours, minutes, and seconds at set at 00:00.
     * {@code currentDateTime} needs to also include the user's current hours, minutes, and seconds
     *
     * @param currentDateTime
     */
    private void updateGreeting(long currentDateTime) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        String name;
        if (AuthToolbox.isSignedIn()) {
            // Only show the display name if user is signed in. User is signed in if the "sign out"
            // preference is visible (true)
            name = sp.getString(getString(R.string.pref_account_display_name_key), null);
        } else {
            name = null;
        }
        mTvGreeting.setText(DataToolbox.getGreeting(mHostActivity, name,
                currentDateTime));
    }

    /**
     * Updates the summary description to show a summary with the total number of food expiring
     * within the given {@code filter}, based on the {@code barChartEntries}.
     * {@code barChartEntries} need not be pre-filtered; the full scope of entries can be passed
     *
     * @param filter
     * @param barChartEntries
     */
    private void updateSummary(WeeklyDateFilter filter, List<BarEntry> barChartEntries) {
        if (barChartEntries == null) {
            return;
        }
        int totalFoodsCountFromFilter = DataToolbox.getTotalFoodsCountFromFilter(filter,
                barChartEntries);
        int entriesSize = barChartEntries.size();
        int foodsCountCurrent = entriesSize > 0 ? (int) barChartEntries.get(0).getY() : 0;
        int foodsCountNextDay = entriesSize > 0 ? (int) barChartEntries.get(1).getY() : 0;

        mTvSummary.setText(DataToolbox.getFullSummary(mHostActivity, filter,
                totalFoodsCountFromFilter, foodsCountCurrent, foodsCountNextDay));
    }

    /**
     * Updates the list header based on the {@code filter}. If {@code filteredFoodList} is empty,
     * then hide the header
     *
     * @param filteredFoodList
     * @param filter
     */
    private void updateListHeader(List<Food> filteredFoodList, WeeklyDateFilter filter) {
        if (filteredFoodList.size() > 0) {
            mTvListHeader.setText(DataToolbox.getAtAGlanceListHeader(mHostActivity, filter));
        } else {
            mTvListHeader.setText("");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_account_display_name_key)) ||
                key.equals(getString(R.string.pref_account_signed_in_key))) {
            updateGreeting(mCurrentDateTime);
        }
    }
}