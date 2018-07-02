package com.zn.baking;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DisplayTest {

    private EspressoTestHelper mHelper;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void prepareRecipes() {
        mHelper = new EspressoTestHelper(mMainActivityTestRule.getActivity());
    }

    @Test
    public void checkIfDisplayed_recipeName_inRecipeList() {
        mHelper.checkIfDisplayed_recipeName_inRecipeList();
    }

    @Test
    public void goToRecipeDetails_checkIfDisplayed_shortStepDescription() {
        mHelper.showDetailFragment();
        mHelper.checkIfDisplayed_shortStepDescription_inRecipeDetails();
    }
}
