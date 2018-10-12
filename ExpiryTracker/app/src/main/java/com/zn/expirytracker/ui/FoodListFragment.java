package com.zn.expirytracker.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.database.ValueEventListener;
import com.zn.expirytracker.R;
import com.zn.expirytracker.data.firebase.FirebaseDatabaseHelper;
import com.zn.expirytracker.data.firebase.UserMetrics;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.data.viewmodel.FoodViewModel;
import com.zn.expirytracker.ui.capture.CaptureActivity;
import com.zn.expirytracker.ui.dialog.AddItemInputPickerBottomSheet;
import com.zn.expirytracker.utils.Constants;
import com.zn.expirytracker.utils.DataToolbox;
import com.zn.expirytracker.utils.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FoodListFragment extends Fragment
        implements AddItemInputPickerBottomSheet.OnInputMethodSelectedListener {

    private static final String KEY_DRAWABLE_ID_INT = Toolbox.createStaticKeyString(
            FoodListFragment.class, "drawable_id_int");
    private static final int RESOURCE_ID_NOT_SET = -1;

    @BindView(R.id.container_list_fragment)
    View mRootView;
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

    private int mResourceId = RESOURCE_ID_NOT_SET;

    public FoodListFragment() {
        // Required empty public constructor
    }

    public static FoodListFragment newInstance() {
        FoodListFragment fragment = new FoodListFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_DRAWABLE_ID_INT, mResourceId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHostActivity = getActivity();
        mViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);

        if (savedInstanceState != null) {
            mResourceId = savedInstanceState.getInt(KEY_DRAWABLE_ID_INT, RESOURCE_ID_NOT_SET);
        }
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
                    if (!mDividersAdjusted) {
                        // Only adjust once
                        setRecyclerViewDividers(true);
                    }
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
        // Check if Firebase timestamp is different from that stored in device. If different,
        // then start listening for changes, and sync local and cloud databases
        if (mFoodTimestampValueEventListener != null) {
            Timber.d("FOOD_TIMESTAMP: Listening...");
            FirebaseDatabaseHelper.addValueEventListener_FoodTimestamp(mFoodTimestampValueEventListener);
        }
        if (mPrefsTimestampValueEventListener != null) {
            Timber.d("PREFS_TIMESTAMP: Listening...");
            FirebaseDatabaseHelper.addValueEventListener_PrefsTimestamp(
                    mPrefsTimestampValueEventListener);
        }

        if (mFoodChildEventListener != null) {
            Timber.d("FOOD_TIMESTAMP: Listening for foods..");
            FirebaseDatabaseHelper.addChildEventListener(mFoodChildEventListener);
        }
        if (mPrefsChildEventListener != null) {
            Timber.d("PREFS_TIMESTAMP: Listening for prefs...");
            FirebaseDatabaseHelper.addChildEventListener_Preferences(mPrefsChildEventListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFoodTimestampValueEventListener != null) {
            FirebaseDatabaseHelper.removeValueEventListener_FoodTimestamp(
                    mFoodTimestampValueEventListener);
        }
        if (mPrefsTimestampValueEventListener != null) {
            FirebaseDatabaseHelper.removeValueEventListener_PrefsTimestamp(
                    mPrefsTimestampValueEventListener);
        }

        // Remove the ChildEventListener until we need it again (i.e. when timestamp changes)
        if (mFoodChildEventListener != null) {
            FirebaseDatabaseHelper.removeChildEventListener(mFoodChildEventListener);
            mFoodChildEventListener = null;
        }
        if (mPrefsChildEventListener != null) {
            FirebaseDatabaseHelper.removeChildEventListener_Preferences(mPrefsChildEventListener);
            mPrefsChildEventListener = null;
        }
    }

    private void startListeningForFoodChanges() {
        if (mFoodChildEventListener == null) {
            Timber.d("FOOD_TIMESTAMP: Listening for food changes");
            mFoodChildEventListener = new FoodChildEventListener();
            // Only listen to changes to food_database/food_table/uid/{child}
            FirebaseDatabaseHelper.addChildEventListener(mFoodChildEventListener);
        }
    }

    private void startListeningForPrefsChanges() {
        if (mPrefsChildEventListener == null) {
            Timber.d("PREFS_TIMESTAMP: Listening for prefs changes");
            mPrefsChildEventListener = new PrefsChildEventListener();
            FirebaseDatabaseHelper.addChildEventListener_Preferences(mPrefsChildEventListener);
        }
    }

    // region RecyclerView setup

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mHostActivity,
                LinearLayoutManager.VERTICAL, false);
        mRvFoodList.setLayoutManager(layoutManager);
        mRvFoodList.setHasFixedSize(true);
        mListAdapter = new FoodListAdapter(mHostActivity, true);
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
                Toolbox.showSnackbarMessage(mRootView, getString(R.string.message_item_removed,
                        food.getFoodName()));
            }
        });
        helper.attachToRecyclerView(mRvFoodList);

        setRecyclerViewDividers(false);

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

    /**
     * Holds a reference to the latest divider set for the RecyclerView so it can be removed and
     * replaced
     */
    private DividerItemDecoration mLastDecor;

    /**
     * Keeps track of when dividers were adjusted successfully, so adjustment can only be done once
     */
    private boolean mDividersAdjusted;

    /**
     * Sets the item dividers for the RecyclerView, which can be adjusted to match the item's
     * drawn widths, if they do not already match their parent's (specify using {@code withCutOffs}
     * <p>
     * Source for adjusting margins: https://stackoverflow.com/questions/41546983/add-margins-to-divider-in-recyclerview
     * <p>
     * Getting view at position: https://stackoverflow.com/questions/33784369/recyclerview-get-view-at-particular-position
     *
     * @param withCutOffs {@code true} to adjust divider margins to match item parent margins
     */
    private void setRecyclerViewDividers(boolean withCutOffs) {
        RecyclerView.ViewHolder vh = mRvFoodList.findViewHolderForAdapterPosition(0);
        DividerItemDecoration decor = new DividerItemDecoration(mHostActivity, DividerItemDecoration.VERTICAL);
        if (withCutOffs && vh != null) {
            int marginSize =
                    vh.itemView.findViewById(R.id.guideline_list_start).getLeft(); // should be the same for marginEnd
            int[] attrs = new int[]{android.R.attr.listDivider};
            TypedArray a = mHostActivity.obtainStyledAttributes(attrs);
            Drawable divider = a.getDrawable(0);
            InsetDrawable insetDivider = new InsetDrawable(divider, marginSize, 0, marginSize, 0);
            a.recycle();
            decor.setDrawable(insetDivider);

            // mark if successful only to prevent adding decor again when list updates
            mDividersAdjusted = true;
        }
        mRvFoodList.removeItemDecoration(mLastDecor); // ensure only one item decoration is added
        mRvFoodList.addItemDecoration(mLastDecor = decor);
    }

    private void showEmptyView(boolean show) {
        Toolbox.showView(mEmptyView, show, false);
        if (show) {
            if (mResourceId == RESOURCE_ID_NOT_SET) {
                mResourceId = DataToolbox.getRandomAnimalDrawableId();
            }
            mIvEmpty.setImageResource(mResourceId);
            mIvEmpty.setContentDescription(
                    DataToolbox.getAnimalContentDescriptionById(mResourceId));
        }
    }

    // endregion

    // region Firebase RTD ChildEventListeners

    private FoodChildEventListener mFoodChildEventListener;

    /**
     * Listens to RTD food list change events
     */
    private class FoodChildEventListener implements ChildEventListener {
        private final String TAG = "FoodChildEventListener";

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
            Timber.e("%s/Cancelled error pulling from RTD: %s", TAG, databaseError.getMessage());
        }
    }

    private PrefsChildEventListener mPrefsChildEventListener;

    /**
     * One-way reading from Firebase RTD to get the freshest Preference values stored. Only takes
     * action from {@link ChildEventListener#onChildAdded(DataSnapshot, String)}. All Preference
     * changes are managed in {@link com.zn.expirytracker.settings.SettingsFragment}
     */
    private class PrefsChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Timber.d("Preference added from RTD: %s", dataSnapshot.getKey());
            updateSharedPreferencesFromFirebase(dataSnapshot);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Timber.d("Preference changed from RTD: %s", dataSnapshot.getKey());
            updateSharedPreferencesFromFirebase(dataSnapshot);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            // Not used here
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            // Not used here
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Timber.e("Error pulling Preference from RTD: %s", databaseError.getMessage());
        }
    }

    /**
     * Writes the updated Preference value from Firebase to SharedPreferences
     *
     * @param snapshot
     */
    private void updateSharedPreferencesFromFirebase(DataSnapshot snapshot) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mHostActivity);
        String key = snapshot.getKey();
        Object value = snapshot.getValue();

        if (key != null) {
            if (key.equals(getString(R.string.pref_notifications_receive_key)) ||
                    key.equals(getString(R.string.pref_capture_beep_key)) ||
                    key.equals(getString(R.string.pref_capture_vibrate_key)) ||
                    key.equals(getString(R.string.pref_capture_voice_input_key))) {
                // Handle booleans
                try {
                    sp.edit().putBoolean(key, (boolean) value).apply();
                } catch (ClassCastException e) {
                    Timber.e(e,
                            "RTD had preference value in wrong format. Preference: %s", key);
                }
            } else if (key.equals(getString(R.string.pref_notifications_days_key)) ||
                    key.equals(getString(R.string.pref_notifications_tod_key)) ||
                    key.equals(getString(R.string.pref_widget_num_days_key)) ||
                    key.equals(getString(R.string.pref_account_display_name_key))) {
                try {
                    sp.edit().putString(key, (String) value).apply();
                } catch (ClassCastException e) {
                    Timber.e(e,
                            "RTD had preference value in wrong format. Preference: %s", key);
                }
            }
        }
    }

    private TimestampValueEventListener mFoodTimestampValueEventListener =
            new TimestampValueEventListener(FirebaseDatabaseHelper.TimestampType.FOOD);

    private TimestampValueEventListener mPrefsTimestampValueEventListener =
            new TimestampValueEventListener(FirebaseDatabaseHelper.TimestampType.PREFS);

    /**
     * Listens to RTD food timestamp change events
     */
    private class TimestampValueEventListener implements ValueEventListener {
        private final String TAG = "TimestampValueEventListener";
        private FirebaseDatabaseHelper.TimestampType mType;

        TimestampValueEventListener(FirebaseDatabaseHelper.TimestampType type) {
            mType = type;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            checkTimestamp(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Timber.e("%s/Cancelled error pulling from RTD: %s", TAG, databaseError.getMessage());
        }

        /**
         * Compares the timestamp on RTD vs the SP, and starts {@link FoodChildEventListener} if
         * they're different, and then updating the SP timestamp with RTD's. Otherwise, does
         * nothing
         *
         * @param dataSnapshot
         */
        private void checkTimestamp(DataSnapshot dataSnapshot) {
            String type;
            String tag;
            switch (mType) {
                case FOOD:
                    type = Constants.FOOD_TIMESTAMP;
                    tag = "FOOD_TIMESTAMP";
                    break;
                case PREFS:
                    type = Constants.PREFS_TIMESTAMP;
                    tag = "PREFS_TIMESTAMP";
                    break;
                default:
                    throw new IllegalArgumentException(String.format(
                            "Invalid TimestampType passed: %s", mType));
            }
            Timber.d("%s: checking timestamp...", tag);
            try {
                Long timestamp_rtd = dataSnapshot.getValue(Long.class);
                if (timestamp_rtd != null) {
                    SharedPreferences sp = mHostActivity.getSharedPreferences(
                            Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                    long timestamp_sp = sp.getLong(type, -1);
                    Timber.d("SP: %s, RTD: %s", timestamp_sp, timestamp_rtd);
                    if (timestamp_rtd != timestamp_sp) {
                        // RTD has been updated, so sync and update the internal timestamp
                        Timber.d("%s: Didn't match, so started listening", tag);
                        switch (mType) {
                            case FOOD:
                                startListeningForFoodChanges();
                                break;
                            case PREFS:
                                startListeningForPrefsChanges();
                                break;
                        }
                        sp.edit().putLong(type, timestamp_rtd).apply();
                    } else {
                        // Don't sync if the timestamp is the same
                        Timber.d("%s: Matched, so not listening", tag);
                    }
                } else {
                    Timber.d("%s: Was null, so not listening", tag);
                }
            } catch (DatabaseException e) {
                Timber.e(e, "Timestamp in RTD was of wrong type. Not setting TimestampValueEventListener");
            }
        }
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
            Toolbox.showSnackbarMessage(mRootView, getString(R.string.message_camera_required));
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
