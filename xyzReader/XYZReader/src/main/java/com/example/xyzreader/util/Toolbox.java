package com.example.xyzreader.util;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.example.xyzreader.GlideApp;
import com.example.xyzreader.GlideRequest;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

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

    /**
     * Returns a formatted HTML body with the specified characteristics
     *
     * @param context
     * @param body
     * @param fontFileName
     * @param fontSize
     * @param fontAlign
     * @param margins
     * @param padding
     * @param fontColor
     * @return
     */
    public static String getWebViewContent(Context context, String body, String fontFileName,
                                           float fontSize, String fontAlign, int margins,
                                           int padding, String fontColor) {
        return context.getString(R.string.details_body_html_prefix, fontFileName,
                fontSize, fontAlign, margins, padding, fontColor) +
                body +
                context.getString(R.string.details_body_html_suffix);
    }

    /**
     * Helper method for getting the hex color value string
     * Source https://stackoverflow.com/questions/5026995/android-get-color-as-string-value
     *
     * @param context
     * @param colorId
     * @return
     */
    public static String getHexColorString(Context context, int colorId) {
        return "#" + Integer.toHexString(ContextCompat.getColor(context, colorId));
    }

    /**
     * Gets the height of the status bar
     * Source: https://stackoverflow.com/questions/27856603/lollipop-draw-behind-statusbar-with-its-color-set-to-transparent
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Gets the height of the navigation bar
     * Source: https://stackoverflow.com/questions/25603718/android-navigation-bar-size-in-xml
     *
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Gets the height of the action bar
     * Source: https://stackoverflow.com/questions/12301510/how-to-get-the-actionbar-height
     *
     * @param context
     * @return
     */
    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    /**
     * Gets the height of the status bar and action bar combined
     *
     * @param context
     * @return
     */
    public static int getStatusBarWithActionBarHeight(Context context) {
        return getStatusBarHeight(context) + getActionBarHeight(context);
    }


    /**
     * Shows the options menu without needing an action bar
     * Source: https://stackoverflow.com/questions/30417223/how-to-add-menu-button-without-action-bar
     */
    public static void showMenuPopup(Context context, View view, int menuResId,
                                     PopupMenu.OnMenuItemClickListener listener) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(listener);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuResId, popup.getMenu());
        popup.show();
    }


    /**
     * Shows or hides views smoothly with an alpha transition. If button, hide completely to
     * negate potential touch events while it's not showing
     *
     * @param show
     */
    public static void showView(final View view, final boolean show, final boolean isButton) {
        view.animate()
                .alpha(show ? 1f : 0f)
                .setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (show)
                            view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!show && isButton)
                            view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (!show && isButton)
                            view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        if (show)
                            view.setVisibility(View.VISIBLE);
                    }
                });
    }
}
