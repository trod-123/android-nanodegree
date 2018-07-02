package com.zn.baking.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.zn.baking.GlideApp;
import com.zn.baking.GlideRequest;
import com.zn.baking.R;
import com.zn.baking.model.Ingredient;
import com.zn.baking.model.Measure;
import com.zn.baking.model.Recipe;
import com.zn.baking.model.Step;

import java.util.List;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class Toolbox {
    private static Toast mToast;

    /**
     * Display toasts, ensuring they do not overlap with each other
     *
     * @param context
     * @param message
     */
    public static void showToast(@NonNull Context context, @NonNull String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * Returns true if device is a tablet
     * @param context
     * @return
     */
    public static boolean isInTabletLayout(Context context) {
        String displayResources = context.getString(R.string.screensize);
        return displayResources.equals( context.getString(R.string.tablet_layout));
    }

    /**
     * Loads a thumbnail from a video url into an imageview
     *
     * @param context
     * @param videoUrl
     * @param imageView
     */
    public static void loadThumbnailFromVideoUrl(@NonNull Context context, @NonNull String videoUrl,
                                                 @NonNull ImageView imageView, RequestListener<Bitmap> listener) {
        getGlideRequestForLoadingThumbnail(context, videoUrl, listener)
                .into(imageView);
    }

    /**
     * Loads a thumbnail from a video url into an imageview. Can only be called on the main thread
     * From: https://futurestud.io/tutorials/glide-loading-images-into-notifications-and-appwidgets
     *
     * @param context
     * @param videoUrl
     */
    public static void loadThumbnailFromVideoUrl(Context context, String videoUrl,
                                                 AppWidgetTarget appWidgetTarget) {
        getGlideRequestForLoadingThumbnail(context, videoUrl, null)
                .into(appWidgetTarget);
    }

    /**
     * Helper for preparing the Glide request
     *
     * @param context
     * @param videoUrl
     * @param listener
     * @return
     */
    private static GlideRequest getGlideRequestForLoadingThumbnail(Context context, String videoUrl, RequestListener<Bitmap> listener) {
        CircularProgressDrawable loadingSpinner = new CircularProgressDrawable(context);
        loadingSpinner.setCenterRadius(40f); // TODO: No magic numbers
        loadingSpinner.setStrokeWidth(12f);
        loadingSpinner.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorAccentDark),
                ContextCompat.getColor(context, R.color.colorAccentLight),
                ContextCompat.getColor(context, R.color.colorSecondaryDark),
                ContextCompat.getColor(context, R.color.colorSecondaryLight));
        loadingSpinner.start();

        return GlideApp.with(context).asBitmap()
                .load(videoUrl)
                .frame(0L)
                .listener(listener)
                .placeholder(loadingSpinner)
                .error(
                        GlideApp.with(context)
                                .asBitmap()
                                .load(R.drawable.ic_broken_image_white_24dp)
                                .override(0)
                                .fitCenter()
                )
                .fallback((ContextCompat.getDrawable(context, R.drawable.ic_broken_image_white_24dp)))
                .transition(BitmapTransitionOptions.withCrossFade())
                .centerCrop();
    }

    /**
     * Generates a Bitmap thumbnail from a video url. Can only be called on a background thread
     * From https://stackoverflow.com/questions/22954894/is-it-possible-to-generate-a-thumbnail-from-a-video-url-in-android
     *
     * @param context
     * @param videoUrl
     * @return
     * @throws Throwable
     */
    public static Bitmap getThumbnailFromVideoUrl(Context context, String videoUrl) {
        RequestOptions options = new RequestOptions().frame(0L);
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context).asBitmap()
                    .load(videoUrl)
                    .apply(options)
                    .submit()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Timber.e("There was an error getting the thumbnail from the video url: %s",
                    e.getMessage());
        }
        return bitmap;
    }

    /**
     * Return the last video url string from a Recipe's list of steps
     *
     * @param recipe
     * @return
     */
    public static String getLastVideoUrlFromRecipe(@NonNull Recipe recipe) {
        List<Step> steps = recipe.getSteps();
        for (int i = steps.size() - 1; i >= 0; i--) {
            String videoUrl = steps.get(i).getVideoURL();
            if (!videoUrl.isEmpty()) {
                return videoUrl;
            }
        }
        return null;
    }


    /**
     * Puts together a string list of readable Ingredient strings
     *
     * @param ingredients
     * @return
     */
    public static String generateIngredientsListString(Context context, List<Ingredient> ingredients) {
        StringBuilder builder = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            builder.append("\t\u2022 ");
            builder.append(generateIngredientString(context, ingredient));
            builder.append("\n");
        }
        String asString = builder.toString();
        return asString.substring(0, asString.length() - 1);
    }

    /**
     * Puts together a consistent number of servings string
     *
     * @param context
     * @param numServings
     * @return
     */
    public static String generateNumServingsString(Context context, int numServings) {
        return context.getResources()
                .getQuantityString(R.plurals.num_servings, numServings, numServings);
    }


    /**
     * Consolidates Ingredient attributes into a single readable string
     * (quantity + measure + ingredient)
     * <p>
     * Form: "[quantity] [measure] of [ingredient name]
     *
     * @param ingredient
     * @return
     */
    public static String generateIngredientString(Context context, Ingredient ingredient) {
        String unit = generateIngredientUnitsString(context, ingredient.getMeasure(), ingredient.getQuantity());
        return context.getString(R.string.ingredient_item, unit, ingredient.getIngredient());
    }

    /**
     * Helper method for generating a units string
     *
     * @param context
     * @param measure
     * @param quantity
     * @return
     */
    public static String generateIngredientUnitsString(Context context, Measure measure, double quantity) {
        switch (measure) {
            case CUP:
                return context.getResources()
                        .getQuantityString(R.plurals.num_cups, (int) quantity, quantity);
            case TBLSP:
                return context.getString(R.string.num_tablespoons_abbrev, quantity);
            case TSP:
                return context.getString(R.string.num_teaspoons_abbrev, quantity);
            case G:
                return context.getString(R.string.num_grams_abbrev, quantity);
            case OZ:
                return context.getString(R.string.num_ounces_abbrev, quantity);
            default:
                return Double.toString(quantity);
        }
    }

    /**
     * Gets the appropriate text color (dark or light) based on the background color
     *
     * @param context
     * @param backgroundColor
     * @return
     */
    public static int getTextColorFromBackgroundColor(Context context, int backgroundColor) {
        int red = Color.red(backgroundColor);
        int green = Color.green(backgroundColor);
        int blue = Color.blue(backgroundColor);

        if ((red * 0.299 + green * 0.587 + blue * 0.114) > 186)
            return ContextCompat.getColor(context, R.color.textColorDark);
        else return ContextCompat.getColor(context, R.color.textColorLight);
    }

    /**
     * Removes the step number from the instruction string
     *
     * @param instruction
     * @return
     */
    public static String generateReadableDetailedStepInstruction(String instruction) {
        int dotIndex = instruction.indexOf(".");
        return instruction.substring(dotIndex + 2);
    }
}
