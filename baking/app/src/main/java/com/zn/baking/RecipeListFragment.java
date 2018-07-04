package com.zn.baking;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;
import com.zn.baking.ui.FragmentHost;
import com.zn.baking.ui.RecipeAdapter;
import com.zn.baking.ui.SpacesGridItemDecoration;
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
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            if (mTabletMode && !launchedFromWidget) {
                // if in tablet mode, automatically load details for first recipe in details layout
                Recipe recipe = mAdapter.getRecipeAtPosition(0);
                if (recipe != null) {
                    displayDetailFragment(mAdapter.getRecipeAtPosition(0),
                            R.id.detail_fragment_container, false );
                }
            }
        }
        return view;
    }

    /**
     * Sets up the recipe adapter
     */
    private void setupRecipeAdapter() {
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
        // TODO: Set spanCount dynamically based on width of screen
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView_recipes.setLayoutManager(gridLayoutManager);
        mRecyclerView_recipes.addItemDecoration(new SpacesGridItemDecoration(16)); // TODO: No magic numbers

        mRecyclerView_recipes.setAdapter(mAdapter);
        mRecyclerView_recipes.setHasFixedSize(true);
    }

    /**
     * Adds the detail fragment for the passed recipe, in the fragment container specified
     * @param recipe
     * @param fragmentContainerId
     * @param addToBackstack
     */
    private void displayDetailFragment(Recipe recipe, int fragmentContainerId, boolean addToBackstack) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(RECIPE_SERIALIZABLE_EXTRA_KEY, recipe);
        DetailRecipeFragment fragment = new DetailRecipeFragment();
        fragment.setArguments(bundle);

        ((FragmentHost) getActivity()).showFragment(fragment, fragmentContainerId, addToBackstack);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(RECIPE_LIST_RECYCLERVIEW_POSITION_EXTRA_KEY,
                mRecyclerView_recipes.getScrollY());
        super.onSaveInstanceState(outState);
    }
}
