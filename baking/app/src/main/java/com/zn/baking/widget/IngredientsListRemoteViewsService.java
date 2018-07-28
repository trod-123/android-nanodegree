package com.zn.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.zn.baking.R;
import com.zn.baking.model.Ingredient;
import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;
import com.zn.baking.util.Toolbox;

import java.util.ArrayList;
import java.util.List;

public class IngredientsListRemoteViewsService extends RemoteViewsService {

    public static final String INTENT_EXTRA_RECIPE_ID =
            "com.zn.baking.intent_extra_recipe_ID";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        if (intent != null) {
            return new IngredientsRemoteViewsFactory(
                    this, intent.getIntExtra(INTENT_EXTRA_RECIPE_ID, JsonParser.RECIPE_INVALID_ID));
        } else {
            return null;
        }
    }

    class IngredientsRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private int mRecipeId;
        private List<Ingredient> mIngredientList = new ArrayList<>();

        IngredientsRemoteViewsFactory(Context context, int recipeId) {
            mContext = context;
            mRecipeId = recipeId;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mIngredientList != null) {
                mIngredientList.clear();
            }
            Recipe recipe = JsonParser.getRecipeFromListById(mContext, mRecipeId);
            if (recipe != null) {
                mIngredientList = recipe.getIngredients();
            }
        }

        @Override
        public void onDestroy() {
            if (mIngredientList != null) {
                mIngredientList.clear();
            }
        }

        @Override
        public int getCount() {
            return mIngredientList != null ? mIngredientList.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_ingredient);
            Ingredient ingredient = mIngredientList.get(i);

            views.setTextViewText(R.id.text_ingredient_name, ingredient.getIngredient());
            views.setTextViewText(R.id.text_ingredient_count,
                    Toolbox.generateIngredientUnitsString(
                            mContext, ingredient.getMeasure(), ingredient.getQuantity()));

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
