package com.zn.baking;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;

import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;

import java.util.List;
import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.internal.viewaction.NestedEnabledScrollToAction.nestedScrollToAction;
import static org.hamcrest.Matchers.not;

/**
 * Common Espresso actions to be used across tests
 */
public class EspressoTestHelper {

    private List<Recipe> mRecipes;
    private int mRecipeId;
    private int mStepId;
    private Recipe mRecipe;
    private String mExpectedRecipeName;
    private String mExpectedStepShortDescription;

    /**
     * Prepares the recipe content with random expected values
     */
    EspressoTestHelper(Context context) {
        mRecipes = JsonParser.getRecipeListFromAssets(context);
        mRecipeId = generateRandomRecipeId();
        mStepId = generateRandomStepId();
        mRecipe = mRecipes.get(mRecipeId);
        mExpectedRecipeName = mRecipe.getName();
        mExpectedStepShortDescription = mRecipe.getSteps().get(mStepId).getShortDescription();
    }

    /**
     * Re-initializes the helper with new random expected fields
     */
    public void reinitFields() {
        mRecipeId = generateRandomRecipeId();
        mStepId = generateRandomStepId();
        mRecipe = mRecipes.get(mRecipeId);
        mExpectedRecipeName = mRecipe.getName();
        mExpectedStepShortDescription = mRecipe.getSteps().get(mStepId).getShortDescription();
    }

    // region Getters and Setters

    public List<Recipe> getRecipes() {
        return mRecipes;
    }

    public int getRecipeId() {
        return mRecipeId;
    }

    public int getStepId() {
        return mStepId;
    }

    public Recipe getRecipe() {
        return mRecipe;
    }

    public String getExpectedRecipeName() {
        return mExpectedRecipeName;
    }

    public String getExpectedStepShortDescription() {
        return mExpectedStepShortDescription;
    }

    // endregion

    /**
     * Blocks all external intents. By default, Espresso intents do not stub any intents. Stubbing
     * needs to be set up before every test run.
     */
    public void stubAllExternalIntents() {
        intending(not(isInternal()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    public void showDetailFragment() {
        onView(withId(R.id.recyclerview_recipe_list))
                .perform(actionOnItemAtPosition(mRecipeId, click()));
    }

    public void showDetailFragment(int recipeId) {
        onView(withId(R.id.recyclerview_recipe_list))
                .perform(actionOnItemAtPosition(recipeId, click()));
    }

    /**
     * Assumes we're already in the details fragment
     */
    public void showStepFragment() {
        onView(withId(R.id.recyclerview_steps_list))
                .perform(
                        nestedScrollToAction(),
                        RecyclerViewActions.scrollToPosition(mStepId),
                        actionOnItemAtPosition(mStepId, click()));
    }

    /**
     * Assumes we're already in the details fragment
     *
     * @param stepId
     */
    public void showStepFragment(int stepId) {
        onView(withId(R.id.recyclerview_steps_list))
                .perform(
                        nestedScrollToAction(),
                        RecyclerViewActions.scrollToPosition(stepId),
                        actionOnItemAtPosition(stepId, click()));
    }

    /**
     * Assumes we're in the recipe list fragment
     */
    public void checkIfDisplayed_recipeName_inRecipeList() {
        onView(withId(R.id.recyclerview_recipe_list))
                .perform(RecyclerViewActions.scrollToPosition(mRecipeId));
        onView(withText(mExpectedRecipeName))
                .check(matches(isDisplayed()));
    }

    /**
     * Assumes we're already in the recipe details fragment
     */
    public void verifyExpectedRecipeName_inRecipeDetails() {
        onView(withId(R.id.text_details_recipe_name))
                .check(matches(withText(mExpectedRecipeName)));
    }

    /**
     * Assumes we're already in the step details fragment
     */
    public void verifyExpectedStepDescription_inStepDetails() {
        onView(withId(R.id.text_step_broad_instruction))
                .check(matches(withText(mExpectedStepShortDescription)));
    }

    /**
     * Assumes we're already in the recipe details fragment
     */
    public void checkIfDisplayed_shortStepDescription_inRecipeDetails() {
        onView(withId(R.id.recyclerview_steps_list))
                .perform(nestedScrollToAction(), RecyclerViewActions.scrollToPosition(mStepId));
        onView(withText(mExpectedStepShortDescription))
                .perform(nestedScrollToAction())
                .check(matches(isDisplayed()));
    }

    /**
     * Simulate pressing the back button
     */
    public void onBackButtonPressed() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    /**
     * Rotates the screen
     *
     * @param landscape
     * @param activity
     */
    public void rotateToLandscape(boolean landscape, Activity activity) {
        if (landscape)
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // region Private utility helpers

    /**
     * Generates a random recipe id based on the number of recipes. Assumes id numbering of recipes
     * is contiguous, without skipping
     * @return
     */
    private int generateRandomRecipeId() {
        Random random = new Random();
        return random.nextInt(mRecipes.size());
    }

    /**
     * Generates a random step id that exists across all recipes. Assumes id numbering of steps
     * is contiguous, without skipping
     *
     * @return
     */
    private int generateRandomStepId() {
        Random random = new Random();
        return random.nextInt(getMaxNumSteps());
    }

    /**
     * Gets the max number of steps that exists across all recipes
     * @return
     */
    private int getMaxNumSteps() {
        int minNumSteps = Integer.MAX_VALUE;
        for (Recipe recipe : mRecipes) {
            int numSteps = recipe.getSteps().size();
            if (numSteps < minNumSteps) minNumSteps = numSteps;
        }
        return minNumSteps;
    }

    // endregion
}
