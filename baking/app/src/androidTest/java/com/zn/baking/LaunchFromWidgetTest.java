package com.zn.baking;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests UI after simulating loading from a widget.
 * Each test assumes we begin in the DetailRecipeFragment
 */
@RunWith(AndroidJUnit4.class)
public class LaunchFromWidgetTest {

    private EspressoTestHelper mHelper;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void prepareRecipes() {
        mHelper = new EspressoTestHelper(mMainActivityTestRule.getActivity());
    }

    @Before
    public void setWidgetIntent() {
        setWidgetIntentHelper(false);
    }

    @Test
    public void loadFromWidget_checkIfDisplayed_shortStepDescription_inRecipeDetails() {
        mHelper.checkIfDisplayed_shortStepDescription_inRecipeDetails();
    }

    @Test
    public void loadFromWidget_verifyExpectedRecipeName_inRecipeDetails() {
        mHelper.verifyExpectedRecipeName_inRecipeDetails();
    }

    @Test
    public void loadFromWidget_pressBackFromDetails_checkIfDisplayed_recipeName_inRecipeList() {
        mHelper.onBackButtonPressed();
        mHelper.checkIfDisplayed_recipeName_inRecipeList();
    }

    @Test
    public void loadFromWidget_pressBackFromDetails_loadFromDifferentWidget_checkIfDisplayed_shortStepDescription_inRecipeDetails() {
        mHelper.onBackButtonPressed();
        setWidgetIntentHelper(true);
        mHelper.checkIfDisplayed_shortStepDescription_inRecipeDetails();
    }

    /**
     * For simulating launching the app via widget
     */
    private void setWidgetIntentHelper(boolean reinitEspressoHelper) {
        Intent detailIntent = new Intent();
        if (reinitEspressoHelper) mHelper.reinitFields();
        detailIntent.putExtra(RecipeListFragment.RECIPE_SERIALIZABLE_EXTRA_KEY, mHelper.getRecipe());
        // simulate widget launch via MainActivity's overridden onNewIntent() method
        mMainActivityTestRule.getActivity().onNewIntent(detailIntent);
    }
}