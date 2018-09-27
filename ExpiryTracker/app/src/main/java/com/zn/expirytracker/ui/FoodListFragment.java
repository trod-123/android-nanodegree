package com.zn.expirytracker.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.firebase.FirebaseDatabaseHelper;
import com.zn.expirytracker.data.firebase.UserMetrics;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.capture.CaptureActivity;
import com.zn.expirytracker.ui.dialog.AddItemInputPickerBottomSheet;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FoodListFragment extends Fragment
        implements AddItemInputPickerBottomSheet.OnInputMethodSelectedListener,
        ChildEventListener {

    @BindView(R.id.container_list_fragment)
    View mRootview;
    @BindView(R.id.tv_food_list_empty)
    View mEmptyView;
    @BindView(R.id.iv_food_list_empty_animal)
    ImageView mIvEmpty;
    @BindView(R.id.sr_food_list)
    SwipeRefreshLayout mSrFoodList;
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
                    showEmptyView(foods.size() == 0);
                } else {
                    Timber.e("ListFragment ViewModel foods list was null. Showing empty view...");
                    showEmptyView(true);
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

    @Override
    public void onResume() {
        super.onResume();

        // Only listen to changes to food_database/food_table/uid/{child}
        FirebaseDatabaseHelper.addChildEventListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseDatabaseHelper.removeChildEventListener(this);
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
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Food food = mListAdapter.getFoodAtPosition(position);
                mViewModel.delete(true, food);
                Toolbox.showSnackbarMessage(mRootview, getString(R.string.message_item_removed,
                        food.getFoodName()));
            }
        });
        helper.attachToRecyclerView(mRvFoodList);
        mRvFoodList.addItemDecoration(
                new DividerItemDecoration(mHostActivity, DividerItemDecoration.VERTICAL));

        // TODO: Disable swipes for now
//        mSrFoodList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Toolbox.showToast(mHostActivity, "This will refresh the list!");
//                mSrFoodList.setRefreshing(false);
//            }
//        });
        mSrFoodList.setEnabled(false);
    }

    private void showEmptyView(boolean show) {
        Toolbox.showView(mEmptyView, show, false);
        if (show) {
            mIvEmpty.setImageResource(DataToolbox.getRandomAnimalDrawableId());
        }
    }

    // region Firebase RTD ChildEventListener

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        try {
            Food food = dataSnapshot.getValue(Food.class);
            if (food != null) {
                Timber.d("Food added from RTD: id_%s", food.get_id());
                mViewModel.insert(false, food); // don't save to cloud to avoid infinite loop
            } else {
                Timber.e("Food added from RTD was null. Not updating DB...");
            }
        } catch (DatabaseException e) {
            Timber.e(e, "Food added from RTD contained child of wrong type. Not updating DB..");
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        try {
            Food food = dataSnapshot.getValue(Food.class);
            if (food != null) {
                Timber.d("Food changed from RTD: id_%s", food.get_id());
                mViewModel.update(false, food); // don't save to cloud to avoid infinite loop
            } else {
                Timber.e("Food changed from RTD was null. Not updating DB...");
            }
        } catch (DatabaseException e) {
            Timber.e(e, "Food changed from RTD contained child of wrong type. Not updating DB..");
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        try {
            Food food = dataSnapshot.getValue(Food.class);
            if (food != null) {
                Timber.d("Food removed from RTD: id_%s", food.get_id());
                mViewModel.delete(false, food); // don't save to cloud to avoid infinite loop
            } else {
                Timber.e("Food removed from RTD was null. Not updating DB...");
            }
        } catch (DatabaseException e) {
            Timber.e(e, "Food removed from RTD contained child of wrong type. Not updating DB..");
        }
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        // Not used here
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Timber.e("Cancelled error pulling from RTD: %s", databaseError.getMessage());
    }

    // endregion

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
        // Ensure device has camera activity to handle this first
        if (Toolbox.checkCameraHardware(mHostActivity)) {
            Intent intent = new Intent(mHostActivity, CaptureActivity.class);
            startActivity(intent);
        } else {
            Timber.d("Attempted to start Capture, but device does not have a camera");
            Toolbox.showSnackbarMessage(mRootview, "Your device needs a camera to do this");
        }
    }

    @Override
    public void onTextInputSelected() {
        UserMetrics.incrementUserTextOnlyInputCount();
        Intent intent = new Intent(mHostActivity, AddActivity.class);
        startActivity(intent);
    }

    // endregion
}
