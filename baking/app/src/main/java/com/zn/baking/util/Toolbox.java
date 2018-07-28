package com.zn.baking.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.zn.baking.GlideApp;
import com.zn.baking.GlideRequest;
import com.zn.baking.R;
import com.zn.baking.model.Ingredient;
import com.zn.baking.model.JsonParser;
import com.zn.baking.model.Measure;
import com.zn.baking.model.Recipe;
import com.zn.baking.model.Step;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

/**
 * Just a class of neat convenient global helper methods
 */
public class Toolbox {
    private static final int STEP_DESCRIPTION_DOT_THRESHOLD = 5;

    private static Toast mToast;
    public static final int NO_ANIMATOR_RESOURCE = -1;
    public static final int NO_SELECTED_ID = 0;

    private static final float GLIDE_THUMBNAIL_MULTIPLIER = 0.1f;

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
     *
     * @param context
     * @return
     */
    public static boolean isInTabletLayout(Context context) {
        String displayResources = context.getString(R.string.screensize);
        return displayResources.equals(context.getString(R.string.tablet_layout));
    }

    /**
     * Loads a thumbnail from an image or video url into an imageview. Loads broken image thumbnail
     * if error
     *
     * @param context
     * @param sourceUrl
     * @param imageView
     */
    public static void loadThumbnailFromUrl(@NonNull Context context, @NonNull String sourceUrl,
                                            @NonNull ImageView imageView, RequestListener<Bitmap> listener) {
        getGlideRequestForLoadingThumbnail(context, sourceUrl, listener)
                .into(imageView);
    }

    /**
     * Loads a thumbnail from a image or video url into an AppWidgetTarget. Can only be called on the main thread
     * From: https://futurestud.io/tutorials/glide-loading-images-into-notifications-and-appwidgets
     *
     * @param context
     * @param sourceUrl
     */
    public static void loadThumbnailFromUrl(Context context, String sourceUrl,
                                            AppWidgetTarget appWidgetTarget, RequestListener<Bitmap> listener) {
        getGlideRequestForLoadingThumbnail(context, sourceUrl, listener)
                .override(context.getResources().getInteger(R.integer.widget_image_size))
                .into(appWidgetTarget);
    }

    /**
     * Helper for preparing the Glide request
     *
     * @param context
     * @param sourceUrl
     * @param listener
     * @return
     */
    private static GlideRequest getGlideRequestForLoadingThumbnail(Context context, String sourceUrl,
                                                                   RequestListener<Bitmap> listener) {
        return GlideApp.with(context).asBitmap()
                .load(sourceUrl)
                .frame(0L)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                // if this is RESOURCE, then we get java.io.FileNotFoundException(No content provider) in the main app AND also in widget (first time endless loading, second time loads broken image error).
                // if this is DATA then it works OK in the main app, but does not load in widget (if a listener is provided, onResourceReady never gets called..., and if no listener is provided, still does not load)
                // if this is AUTOMATIC then it works OK in the main app, but always crashes the widget (if no listener is provided, otherwise onResourceReady never gets called...)
                // if this is NONE, no images load anywhere
                .thumbnail(GLIDE_THUMBNAIL_MULTIPLIER) // ideally, this thumbnail request points to a low-res url of the same image
                .transition(BitmapTransitionOptions.withCrossFade())
                .error(
                        GlideApp.with(context)
                                .asBitmap()
                                .load(R.drawable.ic_broken_image_white_24dp)
                )
                .fallback((ContextCompat.getDrawable(context, R.drawable.ic_broken_image_white_24dp)));
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
        RequestOptions options = new RequestOptions()
                .override(context.getResources().getInteger(R.integer.widget_image_size))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .frame(0L);
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context).asBitmap()
                    .load(videoUrl)
                    .apply(options)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .thumbnail(GLIDE_THUMBNAIL_MULTIPLIER)
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
        if (steps != null) {
            for (int i = steps.size() - 1; i >= 0; i--) {
                Step step = steps.get(i);
                if (step != null) {
                    String videoUrl = step.getVideoURL();
                    if (videoUrl != null && !videoUrl.isEmpty()) {
                        return videoUrl;
                    }
                }
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
        if (ingredients == null) {
            return null;
        }
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
     * Puts together a consistent number of steps string
     *
     * @param context
     * @param numSteps
     * @return
     */
    public static String generateNumStepsString(Context context, int numSteps) {
        return context.getResources()
                .getQuantityString(R.plurals.num_steps, numSteps, numSteps);
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
    private static String generateIngredientString(Context context, Ingredient ingredient) {
        if (ingredient != null) {
            String unit = generateIngredientUnitsString(context, ingredient.getMeasure(), ingredient.getQuantity());
            return context.getString(R.string.ingredient_item, unit, ingredient.getIngredient());
        }
        return null;
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
        if (measure != null) {
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
                case INVALID:
                    return JsonParser.RECIPE_INVALID_MEASURE;
                default:
                    return Double.toString(quantity);
            }
        }
        return null;
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
        return instruction.substring(dotIndex == -1 || dotIndex > STEP_DESCRIPTION_DOT_THRESHOLD ?
                0 : dotIndex + 2);
    }

    /**
     * Writes a string into a file. Returns true if the operation was successful
     *
     * @param string
     * @param filename
     * @param overwrite
     */
    public static boolean writeToUserFileCache(Context context, String string, String filename, boolean overwrite) {
        FileOutputStream fOut;
        try {
            File file = new File(string);
            boolean exists = file.exists();
            fOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
            if (!exists || overwrite) {
                // only write if file does not already exist, or if overwrite is true
                fOut.write(string.getBytes());
                return true;
            } else {
                Timber.d("Data not written to file as it already exists, but overwrite was false");
            }
        } catch (IOException e) {
            Toolbox.showToast(context, "There was an error writing to the file cache");
            Timber.e(e, "There was an error writing to the file cache");
        }
        return false;
    }


    /**
     * Resizes a bitmap. From: https://stackoverflow.com/questions/8471226/how-to-resize-image-bitmap-to-a-given-size/8471294
     *
     * @param realImage
     * @param maxImageSize
     * @param filter
     * @return
     */
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    /**
     * Determines the max size of bitmaps that RemoteViews can support
     *
     * @param context
     * @return
     */
    public static double getMaxRemoteViewsBitmapMemory(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        return screenWidth * screenHeight * 4 * 1.5;
    }

    /**
     * Determines whether the provided path is to an image file.
     * From: https://stackoverflow.com/questions/17618118/check-if-a-file-is-an-image-or-a-video
     *
     * @param path
     * @return
     */
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    /**
     * Determines whether the provided path is to a video file.
     * From: https://stackoverflow.com/questions/17618118/check-if-a-file-is-an-image-or-a-video
     *
     * @param path
     * @return
     */
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    /**
     * Gets a color from the passed bitmap image (source: https://developer.android.com/training/material/palette-colors)
     *
     * @param bitmap
     * @param defaultColor
     * @return
     */
    public static int getBackgroundColor(Bitmap bitmap, PaletteSwatch paletteSwatch,
                                         int defaultColor) {
        Palette palette = Palette.from(bitmap).generate();
        switch (paletteSwatch) {
            case VIBRANT:
                return palette.getVibrantColor(defaultColor);
            case VIBRANT_DARK:
                return palette.getDarkVibrantColor(defaultColor);
            case VIBRANT_LIGHT:
                return palette.getLightVibrantColor(defaultColor);
            case MUTED:
                return palette.getMutedColor(defaultColor);
            case MUTED_DARK:
                return palette.getDarkMutedColor(defaultColor);
            case MUTED_LIGHT:
                return palette.getLightMutedColor(defaultColor);
            default:
                return defaultColor;
        }
    }

    public enum PaletteSwatch {
        VIBRANT, VIBRANT_DARK, VIBRANT_LIGHT, MUTED, MUTED_DARK, MUTED_LIGHT
    }
}
