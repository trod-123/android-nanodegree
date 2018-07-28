package com.zn.baking;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;
import com.zn.baking.ui.FragmentHost;
import com.zn.baking.ui.RecipeAdapter;
import com.zn.baking.ui.RecyclerViewMarginDecoration;
import com.zn.baking.util.Colors;
import com.zn.baking.util.HttpUtils;
import com.zn.baking.util.Toolbox;
import com.zn.baking.widget.IngredientsWidgetConfigurationActivity;
import com.zn.baking.widget.WidgetConfigurationResultCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import timber.log.Timber;

import static com.zn.baking.MainActivity.DEFAULT_VERTICAL_SCROLL_POSITION;

public class RecipeListFragment extends Fragment {

    public static final String RECIPE_PARCELABLE_EXTRA_KEY =
            "com.zn.baking.recipe_parcelable_extra_key";
    public static final String RECIPE_LIST_RECYCLERVIEW_POSITION_EXTRA_KEY =
            "com.zn.baking.recipe_list_recyclerview_position_extra_key";
    public static final String LAUNCHED_FROM_WIDGET_KEY = "com.zn.baking.launched_from_widget_key";
    public static final String RECIPE_LIST_KEY = "com.zn.baking.recipe_list_key";
    public static final String SELECTED_RECIPE_KEY = "com.zn.baking.selected_recipe_key";

    public static final String INSTANTIATED = "com.zn.baking.instantiated";

    @BindView(R.id.swiperefresh_recyclerview_recipe_list)
    SwipeRefreshLayout mSwipeRefresh_recipes;
    @BindView(R.id.recyclerview_recipe_list)
    RecyclerView mRecyclerView_recipes;
    RecipeAdapter mAdapter;
    ArrayList<Recipe> mRecipes;
    @BindView(R.id.text_recipe_list_status)
    TextView mTv_listStatus;
    Handler mMainThreadHandler;
    Runnable mPopulateRecipesIntoAdapterRunnable;

    boolean instantiated = false;
    boolean mLoadedFromWidgetConfig = false; // for passing back recipe to widget config
    boolean mLaunchedFromWidget = false;
    boolean mTabletMode;

    // for keeping track of which recipe was selected, for keeping its card highlighted
    int mSelectedRecipeIndex = Toolbox.NO_SELECTED_ID;
    // for keeping track of which recipe was selected from the widget screen
    int mSelectedRecipeId;

    public RecipeListFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        ButterKnife.bind(this, view);
        Timber.tag(MainActivity.class.getSimpleName());

        if (getArguments() != null) {
            // if loaded as widget. load fragment as widget configuration screen
            mLoadedFromWidgetConfig = getArguments()
                    .getBoolean(IngredientsWidgetConfigurationActivity.LOAD_FRAGMENT_FROM_CONFIG, false);
            // if loaded from widget, do not automatically load details for the first recipe
            mLaunchedFromWidget = getArguments().getBoolean(LAUNCHED_FROM_WIDGET_KEY, false);
            Recipe recipe = getArguments().getParcelable(RECIPE_PARCELABLE_EXTRA_KEY);
            if (recipe != null) mSelectedRecipeId = recipe.getId();
        }

        // handle the fragment differently if loaded from widget
        if (mLoadedFromWidgetConfig) {
            getActivity().setTitle(getString(R.string.widget_ingredients_config_name));
            mLaunchedFromWidget = true;
        } else {
            // normal implementation here
            mTabletMode = ((FragmentHost) getActivity()).isInTabletLayout();
            getActivity().setTitle(getString(R.string.app_name));
            ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(getActivity(), Colors.DEFAULT_APP_BAR_COLOR)));
        }

        // Needed for posting updates on main UI thread (used by the OkHttp callback)
        // From: https://stackoverflow.com/questions/33418232/okhttp-update-ui-from-enqueue-callback
        mMainThreadHandler = new Handler(Looper.getMainLooper());

        mPopulateRecipesIntoAdapterRunnable = new Runnable() {
            @Override
            public void run() {
                List<Recipe> recipes = JsonParser.getRecipeListFromUserCache(getContext());
                if (recipes != null) {
                    mRecipes = new ArrayList<>(recipes);
                    populateRecipesIntoAdapter(mRecipes);
                    Timber.d("Recipes loaded asynchronously");
                }
            }
        };

        setupRecipeAdapter();

        if (savedInstanceState != null) {
            mRecyclerView_recipes.setScrollY(savedInstanceState.getInt(RECIPE_LIST_RECYCLERVIEW_POSITION_EXTRA_KEY, DEFAULT_VERTICAL_SCROLL_POSITION));
            mRecipes = savedInstanceState.getParcelableArrayList(RECIPE_LIST_KEY);
            instantiated = savedInstanceState.getBoolean(INSTANTIATED, false);
            if (mRecipes != null && mTabletMode) {
                mSelectedRecipeIndex = savedInstanceState.getInt(SELECTED_RECIPE_KEY,
                        Toolbox.NO_SELECTED_ID);
            }
        }

        loadRecipeAdapterDataAndPopulateAdapter();

        return view;
    }

    /**
     * Sets up the recipe adapter
     */
    private void setupRecipeAdapter() {
        mRecyclerView_recipes.addItemDecoration(new RecyclerViewMarginDecoration(getContext()));
        mSwipeRefresh_recipes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRecipesFromOnline();
            }
        });

        mAdapter = new RecipeAdapter();
        mRecyclerView_recipes.setAdapter(mAdapter);
        mRecyclerView_recipes.setHasFixedSize(true);
    }

    /**
     * Loads recipe json data from user's file cache if it exists, or from savedInstance.
     * Otherwise, fetches data from online
     */
    private void loadRecipeAdapterDataAndPopulateAdapter() {
        if (mRecipes == null) {
            List<Recipe> recipes = JsonParser.getRecipeListFromUserCache(getContext());
            if (recipes != null) {
                mRecipes = new ArrayList<>(recipes);
                populateRecipesIntoAdapter(mRecipes);
                Timber.d("Recipes loaded from cache");
            } else
                fetchRecipesFromOnline();
        } else {
            populateRecipesIntoAdapter(mRecipes);
            Timber.d("No change in recipe list");
        }
    }

    /**
     * Fetches recipe json data from online and stores it in user's file cache
     */
    private void fetchRecipesFromOnline() {
        mSwipeRefresh_recipes.setRefreshing(true);

        HttpUtils.getStringResponseFromUrlAsynchronously(getActivity().getString(R.string.recipe_url), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Timber.e("There was a problem getting the recipes online: %s", e);
                // Toasts need to be shown on main thread
                mMainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toolbox.showToast(getActivity(), "There was a problem getting the recipes online. Please check for internet connectivity.");
                        mTv_listStatus.setText(R.string.empty_recipe_list_status);
                        mSwipeRefresh_recipes.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String stringResponse = response.body().string();
                if (stringResponse != null) {
                    Toolbox.writeToUserFileCache(getActivity(), stringResponse, getActivity().getString(R.string.recipe_file_name), true);
                    mMainThreadHandler.post(mPopulateRecipesIntoAdapterRunnable);
                } else {
                    Timber.e("There was a problem getting the recipes online. The response is null");
                    // Toasts need to be shown on main thread
                    mMainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toolbox.showToast(getActivity(), "There was a problem getting the recipes online");
                            mSwipeRefresh_recipes.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    /**
     * Inserts a list of recipe objects into the adapter
     *
     * @param recipes
     */
    private void populateRecipesIntoAdapter(List<Recipe> recipes) {
        mAdapter.initAdapter(recipes, new RecipeAdapter.OnClickHandler() {
            @Override
            public void onClick(Recipe recipe, ImageView iv_recipeImage) {
                if (mLoadedFromWidgetConfig) {
                    // if we're coming from widget, pass the clicked recipe back to the config
                    // activity
                    int appBarColor = getBackgroundCardColor(recipe);
                    ((WidgetConfigurationResultCallback) getActivity()).passRecipeBackToActivity(recipe, appBarColor);
                } else {
                    // normal implementation here
                    if (mTabletMode) {
                        int index = mAdapter.getRecipePosition(recipe);
                        if (mSelectedRecipeIndex != index) {
                            displayDetailFragment(recipe, R.id.detail_fragment_container,
                                    false, null);
                        }
                    } else {
                        displayDetailFragment(recipe, R.id.master_fragment_container,
                                true, iv_recipeImage);
                    }
                }
            }
        });
        if (recipes != null && recipes.size() > 0) {
            mTv_listStatus.setVisibility(View.GONE);
        }
        mSwipeRefresh_recipes.setRefreshing(false);

        if (mTabletMode) {
            if (!instantiated) {
                if (!mLaunchedFromWidget) {
                    // Only in tablet mode, Load the selected recipe once recyclerview is ready
                    Recipe recipe = mAdapter.getRecipeAtPosition(mSelectedRecipeIndex);
                    if (recipe != null) {
                        displayDetailFragment(recipe,
                                R.id.detail_fragment_container,
                                false, null);
                    }
                    instantiated = true;
                } else {
                    // If we're coming from widget, then we already loaded the detail fragment
                    // from MainActivity. so highlight the recipe card of the recipe selected
                    mSelectedRecipeIndex = mAdapter.setPositionSelectedFromId(mSelectedRecipeId);
                }
            } else {
                mAdapter.setPositionSelected(mSelectedRecipeIndex);
            }
        }
    }

    /**
     * Adds the detail fragment for the passed recipe, in the fragment container specified
     *
     * @param recipe
     * @param fragmentContainerId
     * @param addToBackstack
     */
    private void displayDetailFragment(Recipe recipe, int fragmentContainerId,
                                       boolean addToBackstack,
                                       @Nullable ImageView sharedImageTransition) {
        int backgroundCardColor = getBackgroundCardColor(recipe);

        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE_PARCELABLE_EXTRA_KEY, recipe);
        bundle.putInt(DetailRecipeFragment.RECIPE_DETAIL_APP_BAR_COLOR_EXTRA_KEY, backgroundCardColor);
        if (!mTabletMode)
            bundle.putString(DetailRecipeFragment.RECIPE_DETAIL_SHARED_IMAGE_TRANSITION,
                    ViewCompat.getTransitionName(sharedImageTransition));
        DetailRecipeFragment fragment = new DetailRecipeFragment();
        fragment.setArguments(bundle);

        ((FragmentHost) getActivity()).showFragment(fragment, fragmentContainerId, addToBackstack,
                sharedImageTransition, null, null,
                R.animator.slide_bottom_enter, R.animator.slide_bottom_exit,
                R.animator.slide_bottom_enter, R.animator.slide_bottom_exit);

        mSelectedRecipeIndex = mAdapter.setPositionSelectedFromId(recipe.getId());
    }

    /**
     * Extract the background card color of the provided recipe directly from the adapter
     *
     * @param recipe
     * @return
     */
    private int getBackgroundCardColor(Recipe recipe) {
        int position = mAdapter.getRecipePosition(recipe);
        // Find view by position: https://stackoverflow.com/questions/33784369/recyclerview-get-view-at-particular-position
        View recipeView = mRecyclerView_recipes.getLayoutManager().findViewByPosition(position);
        if (recipeView != null) {
            View recipeTitleContainer = recipeView.findViewById(R.id.container_recipe_title);
            // Get background color of a view: https://stackoverflow.com/questions/17224152/how-do-i-get-the-background-color-of-a-textview
            if (recipeTitleContainer.getBackground() instanceof ColorDrawable) {
                ColorDrawable cd = (ColorDrawable) recipeTitleContainer.getBackground();
                return cd.getColor();
            }
        }
        return ContextCompat.getColor(getActivity(), Colors.DEFAULT_APP_BAR_COLOR);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(RECIPE_LIST_RECYCLERVIEW_POSITION_EXTRA_KEY,
                mRecyclerView_recipes.getScrollY());
        outState.putParcelableArrayList(RECIPE_LIST_KEY, mRecipes);
        outState.putInt(SELECTED_RECIPE_KEY, mSelectedRecipeIndex);
        outState.putBoolean(INSTANTIATED, instantiated);
        super.onSaveInstanceState(outState);
    }
}
