package com.thirdarm.sandwiches.utils;

import android.util.Log;

import com.thirdarm.sandwiches.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * For parsing Json strings
 */
public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    /**
     * Parses a string Json into a Sandwich object. Assumes that the passed Json matches the Sandwich object specifications
     * @param json The Json to convert into a Sandwich object
     * @return The Sandwich object
     */
    public static Sandwich parseSandwichJson(String json) {
        JSONObject jsonSandwich;
        Sandwich sandwich = null;
        try {
            jsonSandwich = new JSONObject(json);
            // grab the names
            JSONObject jsonSandwichName = jsonSandwich.getJSONObject("name");
            String mainName = jsonSandwichName.getString("mainName");
            List<String> alsoKnownAs = new ArrayList<>();
            JSONArray jsonAlsoKnownAs = jsonSandwichName.getJSONArray("alsoKnownAs");
            for (int i = 0; i < jsonAlsoKnownAs.length(); i++) {
                String alsoKnownAsName = jsonAlsoKnownAs.getString(i);
                alsoKnownAs.add(alsoKnownAsName);
            }

            // grab the ingredients
            List<String> ingredients = new ArrayList<>();
            JSONArray jsonIngredients = jsonSandwich.getJSONArray("ingredients");
            for (int i = 0; i < jsonIngredients.length(); i++) {
                String ingredient = jsonIngredients.getString(i);
                ingredients.add(ingredient);
            }

            // grab non-object items
            String placeOfOrigin = jsonSandwich.getString("placeOfOrigin");
            String description = jsonSandwich.getString("description");
            String image = jsonSandwich.getString("image");

            // create the sandwich
            sandwich = new Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, image, ingredients);

        } catch (JSONException e) {
            Log.e(TAG, "There was an error parsing the Sandwich Json");
            e.printStackTrace();
        }
        return sandwich;
    }
}
