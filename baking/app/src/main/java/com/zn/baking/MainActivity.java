package com.zn.baking;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.zn.baking.ui.FragmentHost;
import com.zn.baking.util.Toolbox;

import timber.log.Timber;

import static com.zn.baking.RecipeListFragment.RECIPE_PARCELABLE_EXTRA_KEY;

public class MainActivity extends AppCompatActivity implements FragmentHost {

    public static final int DEFAULT_VERTICAL_SCROLL_POSITION = 0;

//    public static final String TAG_CURRENT_FRAGMENT = "com.zn.baking.tag_current_fragment";
//    public static final String FRAGMENT_TAG_INCREMENTER_KEY =
//            "com.zn.baking.fragment_tag_incrementer_key";

    // See ARCHIVE note below regarding mCurrentFragment

//    Fragment mCurrentFragment; // for tracking and actioning accordingly

//    int mUniqueTagIncrementer = 0; // ensure all fragment tags are unique during the session

    private boolean mTabletLayout = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.tag(MainActivity.class.getSimpleName());

        // Get if device is in tablet mode
        mTabletLayout = Toolbox.isInTabletLayout(this);

        Intent startIntent = getIntent();

        if (savedInstanceState != null) {
            // Don't reload the fragment on configuration change

            // ARCHIVE: The below assumes that we still ran StepFragment within MainActivity. The
            // purpose of this was to track which fragment we're currently on when we press the
            // back button, without losing reference to the fragment even after configuration
            // changes
            //
            // As of 20180701 we had moved the StepFragment into its own activity. So it is probably
            // that none of the CurrentFragment or UniqueTagIncrementer may be needed

//            // load the reference to the current fragment that is displayed
//            if (savedInstanceState.containsKey(TAG_CURRENT_FRAGMENT)) {
//                mCurrentFragment = getSupportFragmentManager()
//                        .findFragmentByTag(savedInstanceState.getString(TAG_CURRENT_FRAGMENT));
//            }
//            mUniqueTagIncrementer = savedInstanceState.getInt(
//                    FRAGMENT_TAG_INCREMENTER_KEY, 0);
        } else if (startIntent != null && startIntent.getExtras() != null &&
                startIntent.getExtras().containsKey(RECIPE_PARCELABLE_EXTRA_KEY)) {
            // if intent contains recipe, then it must have launched from widget
            handleLaunchedFromWidget(startIntent);
        } else {
            showFragment(new RecipeListFragment(), R.id.master_fragment_container,
                    false, null, null, null,
                    Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE,
                    Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE);
        }
    }

    @Override
    public void showFragment(Fragment fragment, int fragmentContainerId,
                             Boolean addToBackstack, @Nullable ImageView sharedImageTransition,
                             @Nullable Transition enterTransition,
                             @Nullable Transition exitTransition,
                             int enterAnimResource, int exitAnimResource,
                             int popEnterAnimResource, int popExitAnimResource) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!mTabletLayout) {
                // Shared elements
//            if (sharedImageTransition != null) {
//                transaction.addSharedElement(sharedImageTransition,
//                        ViewCompat.getTransitionName(sharedImageTransition));
//            }

                // Set the enter and exit transitions
                if (enterTransition != null) {
                    fragment.setEnterTransition(enterTransition);
                }
                Fragment previousFragment = getSupportFragmentManager().findFragmentById(fragmentContainerId);
                if (previousFragment != null && exitTransition != null) {
                    previousFragment.setExitTransition(exitTransition);
                }
            }

            // Set the custom transition animations
            if (enterAnimResource != Toolbox.NO_ANIMATOR_RESOURCE &&
                    exitAnimResource != Toolbox.NO_ANIMATOR_RESOURCE) {
                if (popEnterAnimResource != Toolbox.NO_ANIMATOR_RESOURCE &&
                        popExitAnimResource != Toolbox.NO_ANIMATOR_RESOURCE) {
                    transaction.setCustomAnimations(enterAnimResource, exitAnimResource,
                            popEnterAnimResource, popExitAnimResource);
                } else {
                    transaction.setCustomAnimations(enterAnimResource, exitAnimResource);
                }
            }
        }

        transaction.replace(fragmentContainerId, fragment);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
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
    protected void onNewIntent(Intent intent) {
        // necessary to call super() as first line when making changes to fragment state, otherwise you get
        // an error: "Can not perform this action after onSaveInstanceState"
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null &&
                intent.getExtras().containsKey(RECIPE_PARCELABLE_EXTRA_KEY)) {
            // handle widget launches here as onCreate() is not called again when activity returns
            // to the foreground.
            handleLaunchedFromWidget(intent);
        }
    }

    /**
     * Support launching from widget. Removes all fragments from fragment backstack to prevent
     * duplicating, then loads a list fragment before loading the detail fragment on top
     *
     * @param startIntent
     */
    private void handleLaunchedFromWidget(Intent startIntent) {
        Bundle startIntentExtras = startIntent.getExtras();
        // Prepare the detail fragment
        DetailRecipeFragment detailRecipeFragment = new DetailRecipeFragment();
        detailRecipeFragment.setArguments(startIntentExtras);

        if (mTabletLayout) {
            // Prepare the recipe list fragment so that the first recipe does not load in details -
            // The user's selected recipe will display instead
            RecipeListFragment recipeListFragment = new RecipeListFragment();
            startIntentExtras.putBoolean(RecipeListFragment.LAUNCHED_FROM_WIDGET_KEY, true);
            recipeListFragment.setArguments(startIntentExtras);

            // if in tablet layout, show both recipe list fragment and detail fragment
            showFragment(recipeListFragment, R.id.master_fragment_container,
                    false, null, null, null,
                    Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE,
                    Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE);
            showFragment(detailRecipeFragment, R.id.detail_fragment_container,
                    false, null, null, null,
                    Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE,
                    Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE);
        } else {
            // Remove all fragments from the backstack to prevent duplicating
            // from https://stackoverflow.com/questions/6186433/clear-back-stack-using-fragments
            getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // put the list fragment behind the detail fragment
            RecipeListFragment listFragment = new RecipeListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_fragment_container, listFragment).commit();

            // launch the detail fragment
            showFragment(detailRecipeFragment, R.id.master_fragment_container,
                    true, null, null, null,
                    R.animator.slide_bottom_enter, R.animator.slide_bottom_exit,
                    R.animator.slide_bottom_enter, R.animator.slide_bottom_exit);
        }
    }

    @Override
    public boolean isInTabletLayout() {
        return mTabletLayout;
    }
}