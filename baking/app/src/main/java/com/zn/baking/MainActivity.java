package com.zn.baking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.zn.baking.ui.FragmentHost;
import com.zn.baking.util.Toolbox;

import timber.log.Timber;

import static com.zn.baking.RecipeListFragment.RECIPE_SERIALIZABLE_EXTRA_KEY;

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
                startIntent.getExtras().containsKey(RECIPE_SERIALIZABLE_EXTRA_KEY)) {
            // if intent contains recipe, then it must have launched from widget
            handleLaunchedFromWidget(startIntent);
        } else {
            showFragment(new RecipeListFragment(), R.id.master_fragment_container,false);
        }
    }

    @Override
    protected void onResume() {
        // TODO: Calling activity from recents, even after having closed app, jumps to same widget
        // recipe page, if widget had launched activity
        // perhaps there needs to be something done here in onResume since none of the above code is called
        // This seems to be a bug in the Gmail app too...
        super.onResume();
    }

    @Override
    public void showFragment(Fragment fragment, int fragmentContainerId, Boolean addToBackstack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainerId, fragment);
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
        // necessary to put as first line when making changes to fragment state, otherwise you get
        // an error: "Can not perform this action after onSaveInstanceState"
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null &&
                intent.getExtras().containsKey(RECIPE_SERIALIZABLE_EXTRA_KEY))
            // handle widget launches here as onCreate() is not called again when activity returns
            // to the foreground.
            handleLaunchedFromWidget(intent);
    }

    /**
     * Support launching from widget. Removes all fragments from fragment backstack to prevent
     * duplicating, then loads a list fragment before loading the detail fragment on top
     *
     * @param startIntent
     */
    private void handleLaunchedFromWidget(Intent startIntent) {
        // Remove all fragments from the backstack to prevent duplicating
        // from https://stackoverflow.com/questions/6186433/clear-back-stack-using-fragments
        getSupportFragmentManager()
                .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // put the list fragment behind the detail fragment
        RecipeListFragment listFragment = new RecipeListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.master_fragment_container, listFragment).commit();

        // load up and launch the detail fragment
        DetailRecipeFragment fragment = new DetailRecipeFragment();
        fragment.setArguments(startIntent.getExtras());
        showFragment(fragment, R.id.master_fragment_container, true);
    }

    @Override
    public boolean isInTabletLayout() {
        return mTabletLayout;
    }
}