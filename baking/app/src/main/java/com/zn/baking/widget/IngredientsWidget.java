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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.Target;
import com.zn.baking.DetailRecipeFragment;
import com.zn.baking.MainActivity;
import com.zn.baking.R;
import com.zn.baking.RecipeListFragment;
import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Recipe;
import com.zn.baking.util.Colors;
import com.zn.baking.util.Toolbox;

import timber.log.Timber;

public class IngredientsWidget extends AppWidgetProvider {

    // For accessing the SP file for this project
    public static final String SHARED_PREFERENCES_INGREDIENTS_WIDGET_NAME =
            "com.zn.baking.widget.ingredients.sp";

    // For storing mapping between appWidgetId and the recipe in the SP,
    // to keep widgets properly updated
    public static final String SHARED_PREFERENCES_INGREDIENTS_WIDGET_PREFIX_RECIPE_ID =
            "com.zn.baking.widget.ingredients.sp.prefix_id";

    // For storing mapping between appWidgetId and the app bar color in the SP,
    // to keep widgets properly updated
    public static final String SHARED_PREFERENCES_INGREDIENTS_WIDGET_PREFIX_COLOR_ID =
            "com.zn.baking.widget.ingredients.sp.prefix_color_id";

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
            updateAppWidget(context, appWidgetManager, i, null,
                    context.getResources().getColor(Colors.DEFAULT_APP_BAR_COLOR));
        }
    }

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe, int appBarColor) {
        // for caching appWidgetId with recipeId
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES_INGREDIENTS_WIDGET_NAME, Context.MODE_PRIVATE);

        if (recipe == null) {
            // if recipe is null, then we're updating. get the recipe linked with appWidgetId in sharedPreferences
            int recipeId = sp.getInt(getSharedPreferencesWidgetRecipeIdKey(appWidgetId),
                    JsonParser.RECIPE_INVALID_ID);
            appBarColor = sp.getInt(getSharedPreferencesWidgetColorKey(appWidgetId),
                    context.getResources().getColor(Colors.DEFAULT_APP_BAR_COLOR));
            recipe = JsonParser.getRecipeFromListById(context, recipeId);
            // if recipe is still null, then we haven't set up the widget yet, so we can't do anything but finish
            if (recipe == null) return;
        } else {
            // store recipeId with appWidgetId for later use
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(getSharedPreferencesWidgetRecipeIdKey(appWidgetId), recipe.getId());
            editor.putInt(getSharedPreferencesWidgetColorKey(appWidgetId), appBarColor);
            editor.apply();
        }

        // pass in the parent widget layout here - in this case, the one containing the stackview
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        // populate the imageview
        views.setViewVisibility(R.id.widget_loading_spinner, View.VISIBLE);
        String videoUrl = Toolbox.getLastVideoUrlFromRecipe(recipe);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // if in main thread, run below
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context,
                    R.id.widget_image_recipe, views, appWidgetId);
            RequestListener<Bitmap> listener = new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    Timber.e(e != null ? e.getMessage() : "Exception message returned null",
                            "There was an issue loading the widget item thumbnail: %s");
                    hideLoadingSpinner(views, context);
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    hideLoadingSpinner(views, context);
                    // Glide's override method does not properly resize the incoming bitmap when it
                    // is loaded to the widget for the first time. Take the extra step to resize
                    // the image to adhere to RemoteView's bitmap constraints:
                    //
                    // The total Bitmap memory used by the RemoteViews object cannot exceed that
                    // required to fill the screen 1.5 times, ie.
                    // (screen width x screen height x 4 x 1.5) bytes.
                    //
                    // If a bitmap that exceeds this limit is loaded to the widget, the app will
                    // crash with the following error:
                    // "RemoteViews for widget update exceeds max bitmap memory usage error"
                    //
                    // This is more evident after testing devices with lower screen densities
                    //
                    // Solution concept from: https://stackoverflow.com/questions/13494898/remoteviews-for-widget-update-exceeds-max-bitmap-memory-usage-error/15238375
                    //
                    // This only needs to be done the first time. On subsequent loads, Glide's
                    // default implementation handles fine
                    int resizeDims = context.getResources().getInteger(R.integer.widget_image_size);
                    int actualBytes = resource.getAllocationByteCount();
                    double maxBytes = Toolbox.getMaxRemoteViewsBitmapMemory(context);
                    if (resizeDims != 0 && actualBytes > maxBytes) {
                        resource = Toolbox.scaleDown(resource, resizeDims, false);
                        target.onResourceReady(resource, null);
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            Toolbox.loadThumbnailFromUrl(context, videoUrl, appWidgetTarget, listener);
        } else {
            // if coming through @update method, then we're in background thread
            Bitmap bitmap = null;
            try {
                bitmap = Toolbox.getThumbnailFromVideoUrl(context, videoUrl);
            } catch (Throwable t) {
                Timber.e(t);
            }
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.widget_image_recipe, bitmap);
                hideLoadingSpinner(views, context);
            }
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
        detailIntent.putExtra(RecipeListFragment.RECIPE_PARCELABLE_EXTRA_KEY, recipe);
        // launching from widget clears the task stack and assumes a new instance of the app
        // (similar to Gmail app)
        detailIntent.putExtra(DetailRecipeFragment.RECIPE_DETAIL_APP_BAR_COLOR_EXTRA_KEY,
                appBarColor);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // set requestCode to appWidgetId to ensure pending intent is unique per appWidgetId
        PendingIntent detailPendingIntent = PendingIntent.getActivity(
                context, appWidgetId, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_click_container, detailPendingIntent);

        // necessary to call to update the app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void hideLoadingSpinner(RemoteViews views, Context context) {
        views.setViewVisibility(R.id.widget_loading_spinner, View.GONE);
        views.setTextColor(R.id.widget_text_recipe_name, context.getResources().getColor(R.color.textColorLight));
        views.setTextColor(R.id.widget_text_num_servings, context.getResources().getColor(R.color.textColorLight));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private static String getSharedPreferencesWidgetRecipeIdKey(int appWidgetId) {
        return SHARED_PREFERENCES_INGREDIENTS_WIDGET_PREFIX_RECIPE_ID + appWidgetId;
    }

    private static String getSharedPreferencesWidgetColorKey(int appWidgetId) {
        return SHARED_PREFERENCES_INGREDIENTS_WIDGET_PREFIX_COLOR_ID + appWidgetId;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int i : appWidgetIds) {
            deleteAppWidget(context, i);
        }
    }

    static void deleteAppWidget(Context context, int appWidgetId) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES_INGREDIENTS_WIDGET_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .remove(getSharedPreferencesWidgetRecipeIdKey(appWidgetId))
                .remove(getSharedPreferencesWidgetColorKey(appWidgetId))
                .apply();
    }
}
