package com.zn.baking;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;
import com.zn.baking.util.HttpUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class JsonParserTest {

    private Context mAppContext;

    @Before
    public void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void checkRecipeJsonParsingFromString() {
        String content = "[\n" +
                "{\n" +
                "\"id\": 1,\n" +
                "\"name\": \"Nutella Pie\",\n" +
                "\"ingredients\": [\n" +
                "{\n" +
                "\"quantity\": 2,\n" +
                "\"measure\": \"CUP\",\n" +
                "\"ingredient\": \"Graham Cracker crumbs\"\n" +
                "},\n" +
                "{\n" +
                "\"quantity\": 6,\n" +
                "\"measure\": \"TBLSP\",\n" +
                "\"ingredient\": \"unsalted butter, melted\"\n" +
                "},\n" +
                "{\n" +
                "\"quantity\": 0.5,\n" +
                "\"measure\": \"CUP\",\n" +
                "\"ingredient\": \"granulated sugar\"\n" +
                "},\n" +
                "{\n" +
                "\"quantity\": 1.5,\n" +
                "\"measure\": \"TSP\",\n" +
                "\"ingredient\": \"salt\"\n" +
                "},\n" +
                "{\n" +
                "\"quantity\": 5,\n" +
                "\"measure\": \"TBLSP\",\n" +
                "\"ingredient\": \"vanilla\"\n" +
                "},\n" +
                "{\n" +
                "\"quantity\": 1,\n" +
                "\"measure\": \"K\",\n" +
                "\"ingredient\": \"Nutella or other chocolate-hazelnut spread\"\n" +
                "},\n" +
                "{\n" +
                "\"quantity\": 500,\n" +
                "\"measure\": \"G\",\n" +
                "\"ingredient\": \"Mascapone Cheese(room temperature)\"\n" +
                "},\n" +
                "{\n" +
                "\"quantity\": 1,\n" +
                "\"measure\": \"CUP\",\n" +
                "\"ingredient\": \"heavy cream(cold)\"\n" +
                "},\n" +
                "{\n" +
                "\"quantity\": 4,\n" +
                "\"measure\": \"OZ\",\n" +
                "\"ingredient\": \"cream cheese(softened)\"\n" +
                "}\n" +
                "],\n" +
                "\"steps\": [\n" +
                "{\n" +
                "\"id\": 0,\n" +
                "\"shortDescription\": \"Recipe Introduction\",\n" +
                "\"description\": \"Recipe Introduction\",\n" +
                "\"videoURL\": \"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4\",\n" +
                "\"thumbnailURL\": \"\"\n" +
                "},\n" +
                "{\n" +
                "\"id\": 1,\n" +
                "\"shortDescription\": \"Starting prep\",\n" +
                "\"description\": \"1. Preheat the oven to 350°F. Butter a 9\\\" deep dish pie pan.\",\n" +
                "\"videoURL\": \"\",\n" +
                "\"thumbnailURL\": \"\"\n" +
                "},\n" +
                "{\n" +
                "\"id\": 2,\n" +
                "\"shortDescription\": \"Prep the cookie crust.\",\n" +
                "\"description\": \"2. Whisk the graham cracker crumbs, 50 grams (1/4 cup) of sugar, and 1/2 teaspoon of salt together in a medium bowl. Pour the melted butter and 1 teaspoon of vanilla into the dry ingredients and stir together until evenly mixed.\",\n" +
                "\"videoURL\": \"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd9a6_2-mix-sugar-crackers-creampie/2-mix-sugar-crackers-creampie.mp4\",\n" +
                "\"thumbnailURL\": \"\"\n" +
                "},\n" +
                "{\n" +
                "\"id\": 3,\n" +
                "\"shortDescription\": \"Press the crust into baking form.\",\n" +
                "\"description\": \"3. Press the cookie crumb mixture into the prepared pie pan and bake for 12 minutes. Let crust cool to room temperature.\",\n" +
                "\"videoURL\": \"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd9cb_4-press-crumbs-in-pie-plate-creampie/4-press-crumbs-in-pie-plate-creampie.mp4\",\n" +
                "\"thumbnailURL\": \"\"\n" +
                "},\n" +
                "{\n" +
                "\"id\": 4,\n" +
                "\"shortDescription\": \"Start filling prep\",\n" +
                "\"description\": \"4. Beat together the nutella, mascarpone, 1 teaspoon of salt, and 1 tablespoon of vanilla on medium speed in a stand mixer or high speed with a hand mixer until fluffy.\",\n" +
                "\"videoURL\": \"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd97a_1-mix-marscapone-nutella-creampie/1-mix-marscapone-nutella-creampie.mp4\",\n" +
                "\"thumbnailURL\": \"\"\n" +
                "},\n" +
                "{\n" +
                "\"id\": 5,\n" +
                "\"shortDescription\": \"Finish filling prep\",\n" +
                "\"description\": \"5. Beat the cream cheese and 50 grams (1/4 cup) of sugar on medium speed in a stand mixer or high speed with a hand mixer for 3 minutes. Decrease the speed to medium-low and gradually add in the cold cream. Add in 2 teaspoons of vanilla and beat until stiff peaks form.\",\n" +
                "\"videoURL\": \"\",\n" +
                "\"thumbnailURL\": \"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffda20_7-add-cream-mix-creampie/7-add-cream-mix-creampie.mp4\"\n" +
                "},\n" +
                "{\n" +
                "\"id\": 6,\n" +
                "\"shortDescription\": \"Finishing Steps\",\n" +
                "\"description\": \"6. Pour the filling into the prepared crust and smooth the top. Spread the whipped cream over the filling. Refrigerate the pie for at least 2 hours. Then it's ready to serve!\",\n" +
                "\"videoURL\": \"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffda45_9-add-mixed-nutella-to-crust-creampie/9-add-mixed-nutella-to-crust-creampie.mp4\",\n" +
                "\"thumbnailURL\": \"\"\n" +
                "}\n" +
                "],\n" +
                "\"servings\": 8,\n" +
                "\"image\": \"\"\n" +
                "}]";
        checkRecipeJsonParsing(content);
    }

    @Test
    public void checkRecipeJsonParsingFromUrlSynchronously() {
        String content;
        try {
            content = HttpUtils.getStringResponseFromUrlSynchronously(mAppContext.getString(R.string.recipe_url));
            System.out.println(content);
            checkRecipeJsonParsing(content);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void checkRecipeJsonParsingFromUrlAsynchronously() {
        HttpUtils.getStringResponseFromUrlAsynchronously(mAppContext.getString(R.string.recipe_url), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                fail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String stringResponse = response.body().string();
                Log.d("TAG", stringResponse);
                checkRecipeJsonParsing(stringResponse);
            }
        });
    }


    private void checkRecipeJsonParsing(String recipeJson) {
        List<Recipe> recipes = JsonParser.parseRecipeListFromJson(recipeJson);
        if (recipes != null) {
            Recipe recipe = recipes.get(0);
            assertTrue(recipe.getId() == 1);
            assertTrue(recipe.getName().equals("Nutella Pie"));
            assertTrue(recipe.getIngredients().get(1).getQuantity() == 6.0);
            assertTrue(recipe.getSteps().get(3).getShortDescription().equals("Press the crust into baking form."));
            assertTrue(recipe.getServings() == 8);
        } else {
            fail();
        }
    }
}
