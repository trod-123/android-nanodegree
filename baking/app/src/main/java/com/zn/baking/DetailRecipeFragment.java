package com.zn.baking;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zn.baking.model.Recipe;
import com.zn.baking.model.Step;
import com.zn.baking.ui.StepAdapter;
import com.zn.baking.util.Colors;
import com.zn.baking.util.Toolbox;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailRecipeFragment extends Fragment {

    public static final String RECIPE_DETAIL_SCROLLVIEW_POSITION_EXTRA_KEY =
            "com.zn.baking.recipe_detail_scrollview_position_extra_key";

    public static final String RECIPE_DETAIL_APP_BAR_COLOR_EXTRA_KEY =
            "com.zn.baking.recipe_detail_appbar_color_extra_key";

    @BindView(R.id.root_details)
    NestedScrollView mNestedScrollView_root;
    @BindView(R.id.image_details_recipe_photo)
    ImageView mIv_photo;
    @BindView(R.id.text_details_recipe_name)
    TextView mTv_name;
    @BindView(R.id.text_details_num_servings)
    TextView mTv_numServings;
    @BindView(R.id.text_details_ingredients_list)
    TextView mTv_ingredients;
    @BindView(R.id.recyclerview_steps_list)
    RecyclerView mRecyclerView_steps;
    StepAdapter mAdapter;

    int mAppBarColor;

    private Recipe mRecipe;
    String mLastVideoUrl;
    private boolean mInstantiated = false; // for making sure fragment is scrolled to top when loaded

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        ButterKnife.bind(this, view);
        Timber.tag(MainActivity.class.getSimpleName());

        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (!Toolbox.isInTabletLayout(getActivity())) {
            // Up navigation handled in hosting Activity. Only show if NOT in tablet layout
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Color the app bar accordingly
        mAppBarColor = getArguments().getInt(
                RECIPE_DETAIL_APP_BAR_COLOR_EXTRA_KEY,
                ContextCompat.getColor(getActivity(), Colors.DEFAULT_APP_BAR_COLOR));
        int appBarTextColor = Toolbox.getTextColorFromBackgroundColor(getActivity(), mAppBarColor);
        // from: https://stackoverflow.com/questions/8024706/how-do-i-change-the-background-color-of-the-actionbar-of-an-actionbaractivity-us
        supportActionBar.setBackgroundDrawable(new ColorDrawable(mAppBarColor));

        // Fill in recipe details
        mRecipe = (Recipe) getArguments().getSerializable(
                RecipeListFragment.RECIPE_SERIALIZABLE_EXTRA_KEY);
        if (mRecipe != null && !mRecipe.getName().isEmpty()) {
            // Set the title of the app bar, in the proper coloring based on app bar background
            String htmlTextColor = String.format(Locale.US, "#%06x",
                    (0xFFFFFF & Color.argb(0, Color.red(appBarTextColor),
                            Color.green(appBarTextColor), Color.blue(appBarTextColor))));
            getActivity().setTitle(Html.fromHtml("<font color=\"" + htmlTextColor + "\">" + mRecipe.getName() + "</font>"));
        }

        setupUI();

        if (savedInstanceState != null) {
            final int scrollPosY = savedInstanceState.getInt(RECIPE_DETAIL_SCROLLVIEW_POSITION_EXTRA_KEY, MainActivity.DEFAULT_VERTICAL_SCROLL_POSITION);
            mNestedScrollView_root.post(new Runnable() {
                @Override
                public void run() {
                    mNestedScrollView_root.setScrollY(scrollPosY);
                }
            });
        } else if (!mInstantiated) {
            // Ensures that the fragment is loaded scrolled to the top
            // (loading via widget would otherwise load fragment scrolled miwday down the page)
            // from: https://stackoverflow.com/questions/6438061/can-i-scroll-a-scrollview-programmatically-in-android
            mNestedScrollView_root.post(new Runnable() {
                @Override
                public void run() {
                    mNestedScrollView_root.setScrollY(MainActivity.DEFAULT_VERTICAL_SCROLL_POSITION);
                }
            });
            mInstantiated = true;
        }

        return view;
    }

    /**
     * Helper method for binding views
     */
    private void setupUI() {
        // set imageview
        mLastVideoUrl = Toolbox.getLastVideoUrlFromRecipe(mRecipe);
        if (mLastVideoUrl != null && !mLastVideoUrl.isEmpty())
            Toolbox.loadThumbnailFromVideoUrl(getContext(), mLastVideoUrl, mIv_photo, null);

        // set textviews
        mTv_name.setText(mRecipe.getName());
        mTv_numServings.setText(getContext().getResources()
                .getQuantityString(R.plurals.num_servings, mRecipe.getServings(),
                        mRecipe.getServings()));
        mTv_ingredients.setText(
                Toolbox.generateIngredientsListString(getContext(), mRecipe.getIngredients()));

        // set up steps recyclerview
        mAdapter = new StepAdapter(mRecipe.getSteps(), R.layout.item_step,
                new StepAdapter.OnClickHandler() {
                    @Override
                    public void onClick(Step step) {
                        int position = mAdapter.getPositionOfItem(step);
                        runRecipeStepActivity(step, position, mRecipe.getName(),
                                mRecipe.getSteps().size());
                    }
                });
        mRecyclerView_steps.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView_steps.setAdapter(mAdapter);
        mRecyclerView_steps.setHasFixedSize(true);
    }

    /**
     * Helper to launch the step activity for the selected step
     *
     * @param step
     * @param position
     * @param recipeName
     * @param numSteps
     */
    private void runRecipeStepActivity(Step step, int position, String recipeName, int numSteps) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(RecipeListFragment.RECIPE_SERIALIZABLE_EXTRA_KEY, mRecipe);
        bundle.putSerializable(RecipeStepFragment.STEP_SERIALIZABLE_EXTRA_KEY, step);
        bundle.putInt(RecipeStepFragment.STEP_POSITION_EXTRA_KEY, position);
        bundle.putString(RecipeStepFragment.RECIPE_NAME_EXTRA_KEY, recipeName);
        bundle.putInt(RecipeStepFragment.NUM_STEPS_EXTRA_KEY, numSteps);
        bundle.putInt(DetailRecipeFragment.RECIPE_DETAIL_APP_BAR_COLOR_EXTRA_KEY, mAppBarColor);

        Intent intent = new Intent(getActivity(), StepActivity.class);
        intent.putExtra(RecipeStepFragment.BUNDLE_STEP_INTENT_EXTRA_KEY, bundle);

        getActivity().startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(RECIPE_DETAIL_SCROLLVIEW_POSITION_EXTRA_KEY,
                mNestedScrollView_root.getScrollY());
        super.onSaveInstanceState(outState);
    }
}
