package com.zn.baking.model;

import android.content.Context;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.zn.baking.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

import timber.log.Timber;

public class JsonParser {

    public static final int RECIPE_INVALID_ID = -1;
    public static final int RECIPE_INVALID_COUNT = -1;

    /**
     * Extracts a list of Recipe objects from a json file stored in the project's
     * app/src/main/assets folder
     *
     * @param context
     * @return
     */
    public static List<Recipe> getRecipeListFromAssets(Context context) {
        String json = openFileFromAssets(context, context.getString(R.string.recipe_file_name));
        return parseRecipeFromJson(json);
    }

    /**
     * Return the recipe from a list if its passed id exists. Return null otherwise
     */
    public static Recipe getRecipeFromListById(Context context, int id) {
        List<Recipe> recipeList = getRecipeListFromAssets(context);
        for (Recipe recipe : recipeList) {
            if (recipe.getId() == id) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Extracts a list of Recipe objects from json input. Input must be formatted proper to a
     * Recipe object
     *
     * @param json The json to parse.
     * @return The list of Recipe objects
     */
    public static List<Recipe> parseRecipeFromJson(String json) {
        Moshi moshi = new Moshi.Builder()
                .build();

        Type recipeList = Types.newParameterizedType(List.class, Recipe.class);
        JsonAdapter<List<Recipe>> ingredientsAdapter = moshi.adapter(recipeList);
        try {
            return ingredientsAdapter.fromJson(json);
        } catch (IOException e) {
            Timber.e("There was an error while parsing Json: %s", e.getMessage());
        }
        return null;
    }

    /**
     * Helper method for opening a file from the Assets folder, located in the project's
     * app/src/main/assets folder
     *
     * @param context
     * @param fileName The name of the file to access
     * @return All lines of a file as a String
     */
    public static String openFileFromAssets(Context context, String fileName) {
        String content = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            content = new String(buffer, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Timber.e("There was an error loading the File from the assets folder: %s", e.getMessage());
        }
        return content;
    }
}
