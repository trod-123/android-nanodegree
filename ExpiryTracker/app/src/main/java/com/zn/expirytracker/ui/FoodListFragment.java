package com.zn.expirytracker.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FoodListFragment extends Fragment {

    @BindView(R.id.rv_food_list)
    RecyclerView mRvFoodList;
    @BindView(R.id.fab_food_list_add)
    FloatingActionButton mFabAdd;

    private Activity mHostActivity;

    private FoodListAdapter mListAdapter;
    private FoodViewModel mViewModel;

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
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_food_list, container, false);
        Timber.tag(FoodListFragment.class.getSimpleName());
        ButterKnife.bind(this, rootView);

        setupRecyclerView();

        mViewModel.getAllFoods(true).observe(this, new Observer<PagedList<Food>>() {
            @Override
            public void onChanged(@Nullable PagedList<Food> foods) {
                mListAdapter.submitList(foods);
            }
        });

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Include scanning and image picker
                startAddActivity();
            }
        });

        return rootView;
    }

    private void setupRecyclerView() {
        mRvFoodList.setLayoutManager(new LinearLayoutManager(mHostActivity,
                LinearLayoutManager.VERTICAL, false));
        mRvFoodList.setHasFixedSize(true);
        mListAdapter = new FoodListAdapter(mHostActivity);
        mRvFoodList.setAdapter(mListAdapter);
    }

    private void startAddActivity() {
        Intent intent = new Intent(mHostActivity, AddActivity.class);
        startActivity(intent);
    }
}
