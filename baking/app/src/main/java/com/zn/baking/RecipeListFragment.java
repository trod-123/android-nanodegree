package com.zn.baking;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;
import com.zn.baking.ui.FragmentHost;
import com.zn.baking.ui.RecipeAdapter;
import com.zn.baking.ui.SpacesGridItemDecoration;
import com.zn.baking.util.Colors;
import com.zn.baking.widget.IngredientsWidgetConfigurationActivity;
import com.zn.baking.widget.WidgetConfigurationResultCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.zn.baking.MainActivity.DEFAULT_VERTICAL_SCROLL_POSITION;

public class RecipeListFragment extends Fragment {

    public static final String RECIPE_SERIALIZABLE_EXTRA_KEY =
            "com.zn.baking.recipe_serializable_extra_key";
    public static final String RECIPE_LIST_RECYCLERVIEW_POSITION_EXTRA_KEY =
            "com.zn.baking.recipe_list_recyclerview_position_extra_key";
    public static final String LAUNCHED_FROM_WIDGET_KEY = "com.zn.baking.launched_from_widget_key";

    @BindView(R.id.recyclerview_recipe_list)
    RecyclerView mRecyclerView_recipes;
    RecipeAdapter mAdapter;

    boolean loadedFromWidgetConfig = false; // for passing back recipe to widget config
    boolean launchedFromWidget = false;
    boolean mTabletMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        ButterKnife.bind(this, view);
        Timber.tag(MainActivity.class.getSimpleName());

        setupRecipeAdapter();

        if (savedInstanceState != null) {
            mRecyclerView_recipes.setScrollY(savedInstanceState.getInt(RECIPE_LIST_RECYCLERVIEW_POSITION_EXTRA_KEY, DEFAULT_VERTICAL_SCROLL_POSITION));
        } else if (getArguments() != null) {
            // if loaded as widget. load fragment as widget configuration screen
            loadedFromWidgetConfig = getArguments()
                    .getBoolean(IngredientsWidgetConfigurationActivity.LOAD_FRAGMENT_FROM_CONFIG, false);
            // if loaded from widget, do not automatically load details for the first recipe
            launchedFromWidget = getArguments().getBoolean(LAUNCHED_FROM_WIDGET_KEY, false);
        }

        // handle the fragment differently if loaded from widget
        if (loadedFromWidgetConfig)
            getActivity().setTitle(getString(R.string.widget_ingredients_config_name));
        else {
            // normal implementation here
            mTabletMode = ((FragmentHost) getActivity()).isInTabletLayout();

            getActivity().setTitle(getString(R.string.app_name));
            ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(getActivity(), Colors.DEFAULT_APP_BAR_COLOR)));
        }
        return view;
    }

    /**
     * Sets up the recipe adapter
     */
    private void setupRecipeAdapter() {
        // TODO: Set spanCount dynamically based on width of screen
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView_recipes.setLayoutManager(gridLayoutManager);
        mRecyclerView_recipes.addItemDecoration(new SpacesGridItemDecoration(16)); // TODO: No magic numbers
        // Add listener for when view is visible to color the action bar accordingly
        // From: https://stackoverflow.com/questions/44177903/listener-to-detect-whether-a-view-is-at-the-front
        mRecyclerView_recipes.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean b) {
                if (mTabletMode && !launchedFromWidget) {
                    // if in tablet mode, automatically load details for first recipe in details layout
                    Recipe recipe = mAdapter.getRecipeAtPosition(0);
                    if (recipe != null) {
                        displayDetailFragment(recipe,
                                R.id.detail_fragment_container, false);
                    }
                }
            }
        });

        List<Recipe> recipes = JsonParser.getRecipeListFromAssets(getContext());
        mAdapter = new RecipeAdapter(recipes, new RecipeAdapter.OnClickHandler() {
            @Override
            public void onClick(Recipe recipe) {
                if (loadedFromWidgetConfig) {
                    // if we're coming from widget, pass the clicked recipe back to the config
                    // activity
                    ((WidgetConfigurationResultCallback) getActivity()).passRecipeBackToActivity(recipe);
                } else {
                    // normal implementation here
                    if (mTabletMode) {
                        displayDetailFragment(recipe, R.id.detail_fragment_container, false);
                    } else {
                        displayDetailFragment(recipe, R.id.master_fragment_container, true);
                    }
                }
            }
        });
        mRecyclerView_recipes.setAdapter(mAdapter);
        mRecyclerView_recipes.setHasFixedSize(true);
    }

    /**
     * Adds the detail fragment for the passed recipe, in the fragment container specified
     *
     * @param recipe
     * @param fragmentContainerId
     * @param addToBackstack
     */
    private void displayDetailFragment(Recipe recipe, int fragmentContainerId, boolean addToBackstack) {
        int backgroundCardColor = getBackgroundCardColor(recipe);

        Bundle bundle = new Bundle();
        bundle.putSerializable(RECIPE_SERIALIZABLE_EXTRA_KEY, recipe);
        bundle.putInt(DetailRecipeFragment.RECIPE_DETAIL_APP_BAR_COLOR_EXTRA_KEY, backgroundCardColor);
        DetailRecipeFragment fragment = new DetailRecipeFragment();
        fragment.setArguments(bundle);

        ((FragmentHost) getActivity()).showFragment(fragment, fragmentContainerId, addToBackstack);
    }

    /**
     * Extract the background card color of the provided recipe
     *
     * @param recipe
     * @return
     */
    private int getBackgroundCardColor(Recipe recipe) {
        int position = mAdapter.getRecipePosition(recipe);
        // Find view by position: https://stackoverflow.com/questions/33784369/recyclerview-get-view-at-particular-position
        View recipeTitleContainer = mRecyclerView_recipes.getLayoutManager().findViewByPosition(position).findViewById(R.id.container_recipe_title);
        // Get background color of a view: https://stackoverflow.com/questions/17224152/how-do-i-get-the-background-color-of-a-textview
        if (recipeTitleContainer.getBackground() instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) recipeTitleContainer.getBackground();
            return cd.getColor();
        } else return ContextCompat.getColor(getActivity(), Colors.DEFAULT_APP_BAR_COLOR);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(RECIPE_LIST_RECYCLERVIEW_POSITION_EXTRA_KEY,
                mRecyclerView_recipes.getScrollY());
        super.onSaveInstanceState(outState);
    }
}
