package com.zn.expirytracker.ui;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
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
import com.zn.expirytracker.utils.DateToolbox;
import com.zn.expirytracker.utils.Toolbox;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Provides summary information
 */
public class AtAGlanceFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_CURRENT_DATE_FILTER =
            Toolbox.createStaticKeyString(AtAGlanceFragment.class, "current_date_filter");

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
    @Nullable
    @BindView(R.id.guideline_at_a_glance_center)
    Guideline mGuidelineCenter;

    private List<BarEntry> mFullList_barChartEntries;

    private FoodListAdapter mListAdapter;
    private FoodViewModel mViewModel;

    private Activity mHostActivity;
    private DateTime mCurrentDateTimeStartOfDay; // for dates calculations
    private long mCurrentDateTime; // for when the exact time is needed
    private WeeklyDateFilter mCurrentFilter = WeeklyDateFilter.NEXT_7;

    /**
     * Reversed x-values for the bar chart, ordered in descending order for RTL layouts. This
     * value is {@code null} if device is not in RTL layout
     */
    private int[] mRtlBarchartLabels;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_CURRENT_DATE_FILTER, mCurrentFilter);
        super.onSaveInstanceState(outState);
    }

    public AtAGlanceFragment() {
        // Required empty public constructor
    }

    public static AtAGlanceFragment newInstance() {
        AtAGlanceFragment fragment = new AtAGlanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(AtAGlanceFragment.class.getSimpleName());

        mHostActivity = getActivity();
        mCurrentDateTime = System.currentTimeMillis();
        mCurrentDateTimeStartOfDay = DateToolbox.getDateTimeStartOfDay(mCurrentDateTime);
        mViewModel = MainActivity.obtainViewModel(getActivity());

        if (savedInstanceState != null) {
            mCurrentFilter = (WeeklyDateFilter) savedInstanceState.getSerializable(
                    KEY_CURRENT_DATE_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_at_a_glance, container, false);
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
        updateViewModelWithFilter(mCurrentFilter);

        // Prepare the UI elements first - these can be done independently of data
        setupBarChartLayout(Toolbox.isLeftToRightLayout());
        updateFabText(mCurrentFilter);
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
        final LiveData<PagedList<Food>> liveData = mViewModel.getAllFoodsExpiringBeforeDate(
                DataToolbox.getDateBoundsFromFilter(filter, mCurrentDateTimeStartOfDay),
                true);
        liveData.observe(this, new Observer<PagedList<Food>>() {
            @Override
            public void onChanged(@Nullable PagedList<Food> foods) {
                if (foods != null) {
                    loadBarChartData(foods, Toolbox.isLeftToRightLayout());
                    mListAdapter.submitList(foods);
                    resetXAxisMaximum(mCurrentFilter);
                    resetYAxisMaximum(foods, mCurrentDateTimeStartOfDay.getMillis(),
                            Toolbox.isLeftToRightLayout());
                    updateListHeader(foods, mCurrentFilter);
                    updateSummary(mCurrentFilter, mFullList_barChartEntries);
                    mRootLayout.animate().setDuration(750).alpha(1f);
                }
                // removing the observer means removing foods from list won't update liveData
//                liveData.removeObserver(this);
            }
        });
    }

    /**
     * Loads the barchart with data
     *
     * @param allFoods
     */
    private void loadBarChartData(List<Food> allFoods, boolean leftToRightLayout) {
        mFullList_barChartEntries = DataToolbox.getBarEntries(allFoods,
                mCurrentDateTimeStartOfDay.getMillis(), mCurrentFilter);
        int initialHighlightIndex = 0;
        if (mFullList_barChartEntries.size() > 0) {
            BarDataSet dataSet;
            if (leftToRightLayout) {
                dataSet = new BarDataSet(mFullList_barChartEntries, null);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            } else {
                // Reverse the bar entries if in RTL layout
                List<BarEntry> reversedEntries = new ArrayList<>();
                int size = mFullList_barChartEntries.size();
                mRtlBarchartLabels = new int[size];
                for (int i = 0; i < size; i++) {
                    BarEntry reversedCurrent = mFullList_barChartEntries.get(size - 1 - i);
                    // For the reversed entries, the bar entry x values are irrelevant. Set those
                    // to the separate labels array which handle the x values
                    reversedEntries.add(new BarEntry(i, reversedCurrent.getY()));
                    mRtlBarchartLabels[i] = (int) reversedCurrent.getX();
                    // For the initial highlight, keep track of where "0" value is in the labels
                    if (reversedCurrent.getX() == 0) initialHighlightIndex = i;
                }
                dataSet = new BarDataSet(reversedEntries, null);
                // required to associate RIGHT axis with the data. by default it is dependent on LEFT
                dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            }
            dataSet.setColor(ContextCompat.getColor(mHostActivity, R.color.colorAccent)); // set color of bars
            dataSet.setDrawValues(false); // don't show values for each point
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.6f);
            mBarChart.setData(barData);

            mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    int daysFromCurrent;
                    if (mRtlBarchartLabels != null) {
                        // For RTL layout
                        daysFromCurrent = mRtlBarchartLabels[(int) e.getX()];
                    } else {
                        daysFromCurrent = (int) e.getX();
                    }
                    updateChartHeader(daysFromCurrent, (int) e.getY());
                }

                @Override
                public void onNothingSelected() {
                }
            });
            mBarChart.highlightValue(initialHighlightIndex, 0); // dataSetIndex needs to be 0
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
    private void setupBarChartLayout(boolean leftToRightLayout) {
        // Label elements
        mBarChart.getDescription().setEnabled(false);
        mBarChart.getLegend().setEnabled(false);

        // Touch elements
        mBarChart.setDragEnabled(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setDoubleTapToZoomEnabled(false);

        // Axis elements
        AxisBase verticalAxis;
        if (leftToRightLayout) {
            verticalAxis = mBarChart.getAxisLeft();
            mBarChart.getAxisRight().setEnabled(false);
        } else {
            verticalAxis = mBarChart.getAxisRight();
            mBarChart.getAxisLeft().setEnabled(false);
        }
        verticalAxis.setAxisMinimum(0); // by default, minimum is auto-calculated
        verticalAxis.setDrawAxisLine(false); // don't draw the y axis
        verticalAxis.setGranularity(1); // limit mRtlBarchartLabels only to whole numbers
        verticalAxis.setTextColor(ContextCompat.getColor(mHostActivity, R.color.textColorPrimaryLight));
        verticalAxis.setTextSize(12f);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setAxisMinimum(-0.5f); // overshoot to add padding for first day
        xAxis.setDrawGridLines(false); // hide the vertical lines for each x axis value
        xAxis.setAxisLineWidth(2); // make the x axis thicker than others
        xAxis.setAxisLineColor(ContextCompat.getColor(mHostActivity, R.color.colorPrimary));
        xAxis.setGranularity(1); // limit mRtlBarchartLabels only to whole numbers
        xAxis.setTextColor(ContextCompat.getColor(mHostActivity, R.color.textColorPrimaryLight));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Show Sun, Mon, Tue... on the x axis
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (mRtlBarchartLabels != null) {
                    return DateToolbox.getFormattedShortDateString(mCurrentDateTimeStartOfDay, mRtlBarchartLabels[(int) value]);
                } else {
                    return DateToolbox.getFormattedShortDateString(mCurrentDateTimeStartOfDay, (int) value);
                }
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
        mListAdapter = new FoodListAdapter(mHostActivity, false);
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
    private void resetYAxisMaximum(List<Food> filteredFoodList, long baseDateTime,
                                   boolean leftToRightLayout) {
        int highestFrequency = DataToolbox.getHighestDailyFrequency(
                filteredFoodList, baseDateTime, false);
        float yMax = highestFrequency + (float) Math.ceil(highestFrequency * 0.10);
        if (leftToRightLayout) {
            mBarChart.getAxisLeft().setAxisMaximum(yMax > 0 ? yMax : 2);
        } else {
            mBarChart.getAxisRight().setAxisMaximum(yMax > 0 ? yMax : 2);
        }
        mBarChart.zoomOut(); // calling this is needed to reset the axes
    }

    /**
     * Updates the date filter fab text as well as its content description
     *
     * @param filter
     */
    private void updateFabText(WeeklyDateFilter filter) {
        String text;
        switch (filter) {
            case NEXT_7:
                text = getString(R.string.date_weekly_filter_7_days_btn);
                break;
            case NEXT_14:
                text = getString(R.string.date_weekly_filter_14_days_btn);
                break;
            case NEXT_21:
                text = getString(R.string.date_weekly_filter_21_days_btn);
                break;
            default:
                text = getString(R.string.at_a_glance_date_range_label);
        }
        mTvChartDateRange.setText(text);
        mFabChartDateRange.setContentDescription(
                getString(R.string.action_change_date_filter, text));
    }

    private void updateChartHeader(int daysFromCurrent, int foodsCount) {
        mTvChartHeaderDate.setText(
                DateToolbox.getFormattedLessShortDateString(mCurrentDateTimeStartOfDay, daysFromCurrent));
        if (foodsCount != 0) {
            mTvChartHeaderNumFoods.setText(mHostActivity.getResources().getQuantityString(
                    R.plurals.at_a_glance_bar_chart_header_count, foodsCount, foodsCount,
                    DateToolbox.getFormattedRelativeDateString(mHostActivity, mCurrentDateTimeStartOfDay,
                            daysFromCurrent)));
        } else {
            mTvChartHeaderNumFoods.setText(getString(
                    R.string.at_a_glance_bar_chart_header_count_none, null,
                    DateToolbox.getFormattedRelativeDateString(mHostActivity, mCurrentDateTimeStartOfDay,
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
            if (name != null) name = name.trim();
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
     * <p>
     * also sets the bar chart's content description to the summary
     *
     * @param filter
     * @param barChartEntries
     */
    private void updateSummary(WeeklyDateFilter filter, List<BarEntry> barChartEntries) {
        if (barChartEntries == null) {
            return;
        }
        int totalFoodsCountFromFilter = DataToolbox.getTotalFoodsCountFromFilter(filter,
                barChartEntries, false);
        int entriesSize = barChartEntries.size();

        // Don't include expired foods (negative X values)
        int index = DataToolbox.getStartingPositiveIndex(
                barChartEntries, DataToolbox.NO_INDEX_LIMIT);

        int foodsCountCurrent = entriesSize > index ? (int) barChartEntries.get(index).getY() : 0;
        int foodsCountNextDay = entriesSize > index + 1 ? (int) barChartEntries.get(index + 1).getY() : 0;

        String summary = DataToolbox.getFullSummary(mHostActivity, filter,
                totalFoodsCountFromFilter, foodsCountCurrent, foodsCountNextDay);
        mTvSummary.setText(summary);
        mBarChart.setContentDescription(summary);
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
            showCenterGuideline(true);
        } else {
            mTvListHeader.setText("");
            showCenterGuideline(false);
        }
    }

    /**
     * Helper for showing the center guideline for applicable layouts that have it
     *
     * @param show
     */
    private void showCenterGuideline(boolean show) {
        if (mGuidelineCenter != null) {
            mGuidelineCenter.setGuidelinePercent(show ? 0.5f : 1.0f);
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