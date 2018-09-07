package com.zn.expirytracker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.TestDataGen;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FoodListFragment extends Fragment {

    @BindView(R.id.rv_food_list)
    RecyclerView mRvFoodList;

    private Activity mHostActivity;

    private FoodListAdapter mListAdapter;
    private TestDataGen mDataGenerator;

    public FoodListFragment() {
        // Required empty public constructor
    }

    /**
     * TODO: Temp, for if we need to pass any arguments
     *
     * @return
     */
    public static FoodListFragment newInstance() {
        FoodListFragment fragment = new FoodListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHostActivity = getActivity();
        mDataGenerator = TestDataGen.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_food_list, container, false);
        Timber.tag(FoodListFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        setupRecyclerView();

        return rootView;
    }

    private void setupRecyclerView() {
        mRvFoodList.setLayoutManager(new LinearLayoutManager(mHostActivity,
                LinearLayoutManager.VERTICAL, false));
        mRvFoodList.setHasFixedSize(true);
        // TODO: Implement under LiveData
        mListAdapter = new FoodListAdapter(mDataGenerator.getFoodNames(),
                mDataGenerator.getExpiryDates(), mDataGenerator.getCounts(),
                mDataGenerator.getLocs());
        mRvFoodList.setAdapter(mListAdapter);
    }
}
