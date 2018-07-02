package com.zn.baking.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.zn.baking.MainActivity;
import com.zn.baking.R;
import com.zn.baking.RecipeListFragment;
import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;
import com.zn.baking.util.Toolbox;

import timber.log.Timber;

public class IngredientsWidget extends AppWidgetProvider {

    // For accessing the SP file for this project
    public static final String SHARED_PREFERENCES_INGREDIENTS_WIDGET_NAME =
            "com.zn.baking.widget.ingredients.sp";

    // For storing mapping between appWidgetId and the recipe in the SP, to keep widgets properly updated
    public static final String SHARED_PREFERENCES_INGREDIENTS_WIDGET_PREFIX_ID =
            "com.zn.baking.widget.ingredients.sp.prefix_id_";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        UpdateWidgetService.startActionPopulateIngredientsWidget(context);
    }

    /**
     * Helper method for updating all app widgets
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds) {
        for (int i : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, i, null);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {
        // for caching appWidgetId with recipeId
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES_INGREDIENTS_WIDGET_NAME, Context.MODE_PRIVATE);

        if (recipe == null) {
            // if recipe is null, then we're updating. get the recipe linked with appWidgetId in sharedPreferences
            int recipeId = sp.getInt(getSharedPreferencesWidgetKey(appWidgetId), JsonParser.RECIPE_INVALID_ID);
            recipe = JsonParser.getRecipeFromListById(context, recipeId);
            // if recipe is still null, then we haven't set up the widget yet, so we can't do anything but finish
            if (recipe == null) return;
        } else {
            // store recipeId with appWidgetId for later use
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(getSharedPreferencesWidgetKey(appWidgetId), recipe.getId());
            editor.apply();
        }

        // pass in the parent widget layout here - in this case, the one containing the stackview
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients);

        // populate the imageview
        String videoUrl = Toolbox.getLastVideoUrlFromRecipe(recipe);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // if in main thread, run below
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.widget_image_recipe, views,
                    appWidgetId);
            Toolbox.loadThumbnailFromVideoUrl(context, videoUrl, appWidgetTarget);
        } else {
            // if coming through @update method, then we're in background thread
            Bitmap bitmap = null;
            try {
                bitmap = Toolbox.getThumbnailFromVideoUrl(context, videoUrl);
            } catch (Throwable t) {
                Timber.e(t);
            }
            if (bitmap != null) views.setImageViewBitmap(R.id.widget_image_recipe, bitmap);
        }

        // populate the textviews
        views.setTextViewText(R.id.widget_text_recipe_name, recipe.getName());
        views.setTextViewText(R.id.widget_text_num_servings,
                Toolbox.generateNumServingsString(context, recipe.getServings()));

        // bind the list adapter to the widget view
        Intent listViewIntent = new Intent(context, IngredientsListRemoteViewsService.class);
        listViewIntent.putExtra(IngredientsListRemoteViewsService.INTENT_EXTRA_RECIPE_ID, recipe.getId()); // Note: can't put recipe serializable in intent otherwise widget will not load, so we gotta do with just passing the recipe id for the list adapter...
        // set data to appWidgetId to ensure pending intent is unique per appWidgetId
        // Determine if two intents are the same for the purposes of intent resolution (filtering). That is, if their action, data, type, class, and categories are the same. This does not compare any extra data included in the intents. (source: https://stackoverflow.com/questions/11350287/ongetviewfactory-only-called-once-for-multiple-widgets)
        listViewIntent.setData(Uri.fromParts(AppWidgetManager.EXTRA_APPWIDGET_ID, String.valueOf(appWidgetId), null));
        views.setRemoteAdapter(R.id.widget_list_view_ingredients, listViewIntent);

        // set the pending intent to start the activity when clicked
        Intent detailIntent = new Intent(context, MainActivity.class);
        detailIntent.putExtra(RecipeListFragment.RECIPE_SERIALIZABLE_EXTRA_KEY, recipe);
        // set requestCode to appWidgetId to ensure pending intent is unique per appWidgetId
        PendingIntent detailPendingIntent = PendingIntent.getActivity(
                context, appWidgetId, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_click_container, detailPendingIntent);

        // necessary to call to update the app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private static String getSharedPreferencesWidgetKey(int appWidgetId) {
        return SHARED_PREFERENCES_INGREDIENTS_WIDGET_PREFIX_ID + appWidgetId;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int i : appWidgetIds) {
            deleteAppWidget(context, i);
        }
    }

    static void deleteAppWidget(Context context, int appWidgetId) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES_INGREDIENTS_WIDGET_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(getSharedPreferencesWidgetKey(appWidgetId)).apply();
    }
}
