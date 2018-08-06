package com.example.xyzreader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.example.xyzreader.GlideApp;
import com.example.xyzreader.GlideRequest;
import com.example.xyzreader.R;

/**
 * Just a class of neat convenient global helper methods
 */
public class Toolbox {

    private static Toast mToast;

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

//    /**
//     * Returns true if device is a tablet
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isInTabletLayout(Context context) {
//        String displayResources = context.getString(R.string.screensize);
//        return displayResources.equals(context.getString(R.string.tablet_layout));
//    }

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
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                // if this is RESOURCE, then we get java.io.FileNotFoundException(No content provider) in the main app AND also in widget (first time endless loading, second time loads broken image error).
                // if this is DATA then it works OK in the main app, but does not load in widget (if a listener is provided, onResourceReady never gets called..., and if no listener is provided, still does not load)
                // if this is AUTOMATIC then it works OK in the main app, but always crashes the widget (if no listener is provided, otherwise onResourceReady never gets called...)
                // if this is NONE, no images load anywhere
                .thumbnail(GLIDE_THUMBNAIL_MULTIPLIER) // ideally, this thumbnail request points to a low-res url of the same image
                .transition(BitmapTransitionOptions.withCrossFade());
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
