package com.zn.baking;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RotationTest {

    private EspressoTestHelper mHelper;
    private Activity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void prepareRecipes() {
        mActivity = mMainActivityTestRule.getActivity();
        mHelper = new EspressoTestHelper(mActivity);
    }

    @After
    public void resetRotation() {
        mHelper.rotateToLandscape(false, mActivity);
    }

    @Test
    public void rotateRecipeList_checkIfDisplayed_recipeName() {
        mHelper.rotateToLandscape(true, mActivity);
        mHelper.checkIfDisplayed_recipeName_inRecipeList();
    }

    @Test
    public void rotateRecipeDetails_checkIfDisplayed_shortStepDescription() {
        mHelper.showDetailFragment();
        mHelper.rotateToLandscape(true, mActivity);
        mHelper.checkIfDisplayed_shortStepDescription_inRecipeDetails();
    }

    @Test
    public void rotateRecipeList_goToRecipeDetails_checkIfDisplayed() {
        mHelper.rotateToLandscape(true, mActivity);
        mHelper.showDetailFragment();
        mHelper.checkIfDisplayed_shortStepDescription_inRecipeDetails();
    }

    @Test
    public void rotateRecipeDetails_verifyRecipeName() {
        mHelper.showDetailFragment();
        mHelper.rotateToLandscape(true, mActivity);
        mHelper.verifyExpectedRecipeName_inRecipeDetails();
    }

    @Test
    public void rotateRecipeList_goToRecipeDetails_verifyRecipeName() {
        mHelper.rotateToLandscape(true, mActivity);
        mHelper.showDetailFragment();
        mHelper.verifyExpectedRecipeName_inRecipeDetails();
    }
}
