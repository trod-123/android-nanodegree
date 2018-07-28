package com.zn.baking;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FragmentTransitionTest {

    private EspressoTestHelper mHelper;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void prepareRecipes() {
        mHelper = new EspressoTestHelper(mMainActivityTestRule.getActivity());
    }

    @Test
    public void goToRecipeDetails_verifyRecipeName() {
        mHelper.showDetailFragment();
        mHelper.verifyExpectedRecipeName_inRecipeDetails();
    }

    @Test
    public void goToRecipeDetails_goToStepDetails_verifyStepDescription() {
        mHelper.showDetailFragment();
        mHelper.showStepFragment();
        mHelper.verifyExpectedStepDescription_inStepDetails();
    }

    @Test
    public void goToRecipeDetails_thenBack_verifyRecipeName() {
        if (!mHelper.isInTabletMode()) {
            mHelper.showDetailFragment();
            mHelper.onBackButtonPressed();
            mHelper.checkIfDisplayed_recipeName_inRecipeList();
        }
    }

    @Test
    public void goToRecipeDetails_goToStepDetails_thenBack_verifyRecipeNameInDetails() {
        mHelper.showDetailFragment();
        mHelper.showStepFragment();
        mHelper.onBackButtonPressed();
        mHelper.verifyExpectedRecipeName_inRecipeDetails();
    }
}
