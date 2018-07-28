package com.zn.baking;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.zn.baking.ui.FragmentHost;
import com.zn.baking.util.Colors;
import com.zn.baking.util.Toolbox;

import timber.log.Timber;

public class StepActivity extends AppCompatActivity implements FragmentHost {

    public static final String TAG_CURRENT_FRAGMENT = "com.zn.baking.tag_current_fragment";

    private boolean mTabletLayout = false;
    private RecipeStepFragment mStepFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Timber.tag(MainActivity.class.getSimpleName());

        // Get if device is in tablet mode
        mTabletLayout = Toolbox.isInTabletLayout(this);

        Intent intent = getIntent();

        // set the app bar color here so fragments don't keep doing it
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                intent.getBundleExtra(RecipeStepFragment.BUNDLE_STEP_INTENT_EXTRA_KEY)
                        .getInt(DetailRecipeFragment.RECIPE_DETAIL_APP_BAR_COLOR_EXTRA_KEY,
                                Colors.DEFAULT_APP_BAR_COLOR)));

        if (savedInstanceState != null) {
            // load the reference to the current fragment that is displayed
            if (savedInstanceState.containsKey(TAG_CURRENT_FRAGMENT)) {
                mStepFragment = (RecipeStepFragment) getSupportFragmentManager()
                        .findFragmentByTag(savedInstanceState.getString(TAG_CURRENT_FRAGMENT));
            }
        } else {
            if (mTabletLayout) {
                // If in tablet mode, create a fragment with the list of steps
                CompactStepListFragment stepListFragment = createStepListFragment(intent);
                showFragment(stepListFragment, R.id.step_list_fragment_container,
                        false, null, null, null,
                        Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE,
                        Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE);
            }
            // create a fragment with the recipe step details
            mStepFragment = createRecipeStepFragment(intent);
            showFragment(mStepFragment, R.id.step_fragment_container,
                    false, null, null, null,
                    Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE,
                    Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE);
        }
    }

    /**
     * Used to navigate between fragments for a single-pane UI device layout configuration
     *
     * @param fragment
     * @param addToBackstack
     */
    @Override
    public void showFragment(Fragment fragment, int fragmentContainerId,
                             Boolean addToBackstack, ImageView sharedImageTransition,
                             Transition enterTransition, Transition exitTransition,
                             int enterAnimResource, int exitAnimResource,
                             int popEnterAnimResource, int popExitAnimResource) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainerId, fragment,
                        fragment.getClass().getSimpleName());
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mStepFragment != null) {
            // Keep a reference to the current fragment when device configuration changes
            outState.putString(TAG_CURRENT_FRAGMENT, mStepFragment.getTag());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // handle all up button presses from fragments here
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mTabletLayout && mStepFragment.isLandscape() && mStepFragment.isVideoLoaded()) {
            // Exit out of full screen mode when back button is pressed, only when video is loaded
            mStepFragment.forceChangeFullscreen();
        } else
            super.onBackPressed();
    }

    /**
     * Creates a step list fragment out of the passed data (used for dual-pane UI layouts)
     *
     * @param data
     * @return
     */
    private CompactStepListFragment createStepListFragment(Intent data) {
        Bundle bundle = data.getBundleExtra(RecipeStepFragment.BUNDLE_STEP_INTENT_EXTRA_KEY);
        CompactStepListFragment fragment = new CompactStepListFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * Creates a step fragment out of the passed data
     *
     * @param data
     * @return
     */
    private RecipeStepFragment createRecipeStepFragment(Intent data) {
        Bundle bundle = data.getBundleExtra(RecipeStepFragment.BUNDLE_STEP_INTENT_EXTRA_KEY);
        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public boolean isInTabletLayout() {
        return mTabletLayout;
    }
}
