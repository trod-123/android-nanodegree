package com.zn.baking.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.zn.baking.R;
import com.zn.baking.util.Toolbox;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

import timber.log.Timber;

public class JsonParser {

    public static final int RECIPE_INVALID_ID = -999;
    public static final int RECIPE_INVALID_COUNT = -999;
    public static final String RECIPE_INVALID_INGREDIENT = "INVALID INGREDIENT";
    public static final String RECIPE_INVALID_MEASURE = "INVALID MEASURE";

    /**
     * Extracts a list of Recipe objects from a json file stored in the user's file cache
     * @param context
     * @return
     */
    public static List<Recipe> getRecipeListFromUserCache(Context context) {
        String json = openFileFromUserCache(context, context.getString(R.string.recipe_file_name));
        if (json != null) {
            return parseRecipeListFromJson(json);
        } else {
            return null;
        }
    }

    /**
     * Return the recipe from a list if its passed id exists. Return null otherwise
     */
    public static Recipe getRecipeFromListById(Context context, int id) {
        List<Recipe> recipeList = getRecipeListFromUserCache(context);
        return getRecipeFromListByIdHelper(recipeList, id);
    }

    public static Recipe getRecipeFromListByIdHelper(List<Recipe> recipeList, int id) {
        if (recipeList != null) {
            for (Recipe recipe : recipeList) {
                if (recipe.getId() == id) {
                    return recipe;
                }
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
    public static List<Recipe> parseRecipeListFromJson(@NonNull String json) {
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

    /**
     * Helper method for opening a file from the user's file cache
     * From: https://stackoverflow.com/questions/14768191/how-do-i-read-the-file-content-from-the-internal-storage-android-app
     * @param context
     * @param fileName
     * @return
     */
    public static String openFileFromUserCache(Context context, String fileName) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            Timber.w(e, "File cache not found");
        }
        if (fis != null) {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ( (line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (IOException e) {
                Toolbox.showToast(context, "There was an issue reading the file cache");
                Timber.e(e, "There was an issue loading the file cache");
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    Timber.e(e, "There was an issue closing the file input stream");
                }
            }
        }
        return null;
    }
}
