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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zn.expirytracker.R;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.capture.CaptureActivity;
import com.zn.expirytracker.ui.dialog.AddItemInputPickerBottomSheet;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FoodListFragment extends Fragment
        implements AddItemInputPickerBottomSheet.OnInputMethodSelectedListener {

    @BindView(R.id.container_list_fragment)
    View mRootview;
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
                if (foods != null) {
                    mListAdapter.submitList(foods);
                }
            }
        });

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputTypePickerDialog();
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
        mRvFoodList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // view scrolls up (user scrolls down)
                    mFabAdd.hide();
                } else {
                    // view scrolls down (user scrolls up)
                    mFabAdd.show();
                }
            }
        });

        // Remove items by swipe
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Food food = mListAdapter.getFoodAtPosition(position);
                mViewModel.delete(food.get_id());
                Toolbox.showSnackbarMessage(mRootview, getString(R.string.message_item_removed,
                        food.getFoodName()));
            }
        });
        helper.attachToRecyclerView(mRvFoodList);
        mRvFoodList.addItemDecoration(new DividerItemDecoration(mHostActivity, DividerItemDecoration.VERTICAL));
    }

    // region Input type picker dialog

    private void showInputTypePickerDialog() {
        // Show the bottom sheet
        AddItemInputPickerBottomSheet bottomSheet = new AddItemInputPickerBottomSheet();
        bottomSheet.setTargetFragment(this, 0);
        bottomSheet.show(getFragmentManager(),
                AddItemInputPickerBottomSheet.class.getSimpleName());
    }

    @Override
    public void onCameraInputSelected() {
        Intent intent = new Intent(mHostActivity, CaptureActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTextInputSelected() {
        Intent intent = new Intent(mHostActivity, AddActivity.class);
        startActivity(intent);
    }

    // endregion
}
