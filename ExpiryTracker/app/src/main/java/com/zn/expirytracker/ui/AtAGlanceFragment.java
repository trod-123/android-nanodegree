package com.zn.expirytracker.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.TestDataGen;

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

    public AtAGlanceFragment() {
        // Required empty public constructor
    }

    /**
     * TODO: Temp, for if we need to pass any arguments
     * @return
     */
    public static AtAGlanceFragment newInstance() {
        AtAGlanceFragment fragment = new AtAGlanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_at_a_glance, container, false);
        Timber.tag(AtAGlanceFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        setupBarChart();

        return rootView;
    }

    /**
     * Helper for setting up the bar chart
     */
    private void setupBarChart() {
        // Data elements
        List<BarEntry> entries = TestDataGen.getTestChartValues();
        BarDataSet dataSet = new BarDataSet(entries, "TEST");
        dataSet.setColor(R.color.colorPrimary);
        dataSet.setDrawValues(false);
        BarData barData = new BarData(dataSet);
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
        mBarChart.getAxisLeft().setAxisMinimum(0);
        mBarChart.getXAxis().setDrawGridLines(false);
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mBarChart.invalidate();
    }
}
