package com.example.xyzreader.util;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.example.xyzreader.GlideApp;
import com.example.xyzreader.GlideRequest;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.ui.ArticleActionsMenuOnClickListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import timber.log.Timber;

/**
 * Just a class of neat convenient global helper methods
 */
public class Toolbox {

    private static Toast mToast;

    public static int DEFAULT_TOAST_LENGTH = Toast.LENGTH_SHORT;
    public static int DEFAULT_SNACKBAR_LENGTH = Snackbar.LENGTH_LONG;

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
        mToast = Toast.makeText(context, message, DEFAULT_TOAST_LENGTH);
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
     * Loads an image from an image url into an imageview with the requested resize dimensions
     * <p>
     * Underlying mechanism is provided by Picasso
     *
     * @param sourceUrl
     * @param imageView
     * @param resizeWidth
     * @param resizeHeight
     * @param callback
     */
    public static void loadSharedElementsImageFromUrl(
            @NonNull String sourceUrl, @NonNull ImageView imageView, int resizeWidth,
            int resizeHeight, Callback callback) {
        Picasso.get()
                .load(sourceUrl)
                .resize(resizeWidth, resizeHeight)
                .centerCrop()
                .noFade()
                .into(imageView, callback);
    }

    /**
     * Loads an image from an image url into a target with the requested resize dimensions
     * <p>
     * Underlying mechanism is provided by Picasso
     *
     * @param sourceUrl
     * @param resizeWidth
     * @param resizeHeight
     * @param target
     */
    public static void loadSharedElementsImageFromUrlWithTargetCallbacks(
            @NonNull String sourceUrl, int resizeWidth, int resizeHeight, Target target) {
        Picasso.get()
                .load(sourceUrl)
                .resize(resizeWidth, resizeHeight)
                .centerCrop()
                .noFade()
                .into(target);
    }

    /**
     * Loads an image from an image or video url into an imageview. For images that will be used
     * as shared elements, see {@code Toolbox.loadSharedElementsImageFromUrl()}
     * <p>
     * Underlying mechanism is provided by Glide
     *
     * @param context
     * @param sourceUrl
     * @param imageView
     */
    public static void loadImageFromUrl(@NonNull Context context, @NonNull String sourceUrl,
                                        @NonNull ImageView imageView, RequestListener<Bitmap> listener) {
        getGlideRequestForLoadingImage(context, sourceUrl, listener)
                .into(imageView);
    }

    /**
     * Loads an image from an image or video url into an imageview. This method is tailored
     * to images used for shared elements transitions.
     * <p>
     * This is just like {@code Toolbox.loadImageFromUrl()}, but with animations
     * disabled, no thumbnails, and an overriding parameter that takes in pixel size
     * {@code resizePixels}. Note {@code resizePixels} must be the same for both source and target
     * images for there to be a "cache hit", which should help loading times
     * <p>
     * Underlying mechanism is provided by Glide
     *
     * @param context
     * @param sourceUrl
     * @param imageView
     * @param resizePixels
     * @param listener
     */
    public static void loadSharedElementsImageFromUrl(
            @NonNull Context context, @NonNull String sourceUrl, @NonNull ImageView imageView,
            int resizePixels, RequestListener<Bitmap> listener) {
        getGlideRequestForLoadingSharedElementsImage(context, sourceUrl, resizePixels, listener)
                .into(imageView);
    }

    /**
     * Helper for preparing the Glide request. For images that will be used as shared elements,
     * see {@code Toolbox.getGlideRequestForLoadingSharedElementsImage()}
     *
     * @param context
     * @param sourceUrl
     * @param listener
     * @return
     */
    private static GlideRequest getGlideRequestForLoadingImage(Context context, String sourceUrl,
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
     * Helper for preparing a Glide request tailored to images used for shared elements transitions.
     * This is just like {@code Toolbox.getGlideRequestForLoadingImage()}, but with animations
     * disabled, no thumbnails, and an overriding parameter that takes in pixel size
     * {@code resizePixels}. Note {@code resizePixels} must be the same for both source and target
     * images for there to be a "cache hit", which should help loading times
     * <p>
     * From: https://github.com/bumptech/glide/issues/502
     *
     * @param context
     * @param sourceUrl
     * @param resizePixels
     * @param listener
     * @return
     */
    private static GlideRequest getGlideRequestForLoadingSharedElementsImage(
            Context context, String sourceUrl, int resizePixels, RequestListener<Bitmap> listener) {
        return GlideApp.with(context).asBitmap()
                .load(sourceUrl)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .override(resizePixels);
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
     * Formats body by adding line breaks and removing excess spacing. Can format for both webview
     * and textview
     *
     * @param body
     * @param forWebView
     * @return
     */
    public static String formatArticleBodyString(String body, boolean forWebView) {
        String r = forWebView ? "<br />" : "\n";
        if (forWebView) {
            return body.replaceAll("(\r\n {11,})|(\n {11,})", r + r + r)
                    .replaceAll("(\r\n {5,10})|(\n {5,10})", r + r)
                    .replaceAll(" {4}", "")
                    .replaceAll("(\r\n\r\n\r\n)|(\n\n\n)", r + r + r)
                    .replaceAll("(\r\n\r\n)|(\n\n)", r + r)
                    .replaceAll("(\r\n )|(\n )", "")
                    .replaceAll("(\r\n)|(\n)", " ");
        } else {
            return body.replaceAll("(\r\n {11,})|(\n {11,})", r + r + r)
                    .replaceAll("(\r\n {5,10})|(\n {5,10})", r + r)
                    .replaceAll(" {4}", "")
                    .replaceAll("\r\n\r\n\r\n", r + r + r)
                    .replaceAll("\r\n\r\n", r + r)
                    .replaceAll("\r\n ", "")
                    .replaceAll("\r\n", " ");
        }
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
     * Gets the height of the status bar, in pixels
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
     * Gets the height of the navigation bar, in pixels
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
     * Gets the height of the action bar, in pixels
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
     * Gets the height of the status bar and action bar combined, in pixels
     *
     * @param context
     * @return
     */
    public static int getStatusBarWithActionBarHeight(Context context) {
        return getStatusBarHeight(context) + getActionBarHeight(context);
    }


    /**
     * General utility method that shows the options menu without needing an action bar
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
     * Shows the article actions options menu
     *
     * @param context
     * @param view
     * @param cursor
     * @param position
     */
    public static void showArticleActionsMenuPopup(Context context, View view, Cursor cursor, int position) {
        showMenuPopup(context, view, R.menu.menu_article_actions,
                new ArticleActionsMenuOnClickListener(context, cursor, position, view));
    }

    /**
     * General utility method that shows or hides views smoothly with an alpha transition.
     * If button, hide completely to negate potential touch events while it's not showing
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


    /**
     * Generates a title and author string used to set a share intent. If there is an error, show
     * a snackbar
     */
    public static void shareArticle(Context context, Cursor cursor, int position,
                                    @NonNull View parentView) {
        if (cursor != null) {
            cursor.moveToPosition(position);
            String title = cursor.getString(ArticleLoader.Query.TITLE);
            String author = cursor.getString(ArticleLoader.Query.AUTHOR);
            String shareString = context.getString(R.string.share_message, title, author);

            Intent shareIntent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, shareString)
                    .setType("text/plain");
            context.startActivity(Intent.createChooser(
                    shareIntent, context.getString(R.string.share_title, title)));
        } else {
            Snackbar.make(parentView, R.string.error_message_share, DEFAULT_SNACKBAR_LENGTH)
                    .show();
            Timber.e(context.getString(R.string.log_error_share));
            //throw new NullPointerException("Cursor is null");
        }
    }

    /**
     * Utility method for getting a float value from resources
     * https://stackoverflow.com/questions/3282390/add-floating-point-value-to-android-resources-values
     *
     * @param resources
     * @param resId
     * @return
     */
    public static float getFloatFromResources(Resources resources, int resId) {
        TypedValue outValue = new TypedValue();
        resources.getValue(resId, outValue, true);
        return outValue.getFloat();
    }

    public static float decelerateInterpolator(float value) {
        return 1f - (value * value);
    }

    public static float accelerateInterpolator(float value) {
        return (1f - value) * (1f - value);
    }

    public static float linearInterpolator(float value) {
        return 1f - value;
    }

    /**
     * Enable touch responses
     *
     * @param window
     * @param enable
     */
    public static void enableTouchResponse(Window window, boolean enable) {
        if (!enable) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
