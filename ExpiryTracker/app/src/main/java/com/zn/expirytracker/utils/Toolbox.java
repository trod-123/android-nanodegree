package com.zn.expirytracker.utils;

import android.animation.Animator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.rd.PageIndicatorView;
import com.zn.expirytracker.GlideApp;
import com.zn.expirytracker.GlideRequest;
import com.zn.expirytracker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Just a class of neat convenient global helper methods
 */
public class Toolbox {

    private static Toast mToast;
    private static Snackbar mSnackbar;
    private static String mSnackbarMessage;

    /**
     * Check if this device has a camera
     *
     * @param context
     * @return
     */
    public static boolean checkCameraHardware(Context context) {
        return (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
    }

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
        mToast = Toast.makeText(context, message, Constants.DEFAULT_TOAST_LENGTH);
        mToast.show();
    }

    /**
     * Helper that shows a {@link Snackbar} at a default duration, ensuring that they do not overlap
     * with each other if the message is the same
     *
     * @param view
     * @param message
     */
    public static void showSnackbarMessage(View view, String message) {
        if ((mSnackbar != null && (!mSnackbar.isShown() || !mSnackbarMessage.equals(message))) || mSnackbar == null) {
            // Refresh currently shown snackbar with different message, otherwise don't do
            // anything
            mSnackbarMessage = message;
            mSnackbar = Snackbar.make(view, mSnackbarMessage, Constants.DEFAULT_SNACKBAR_LENGTH);
            mSnackbar.show();
        }
    }

    /**
     * Helper that vibrates the device for a specified amount of time, in millis
     * <p>
     * https://stackoverflow.com/questions/9079632/android-vibrate-on-touch
     *
     * @param context
     * @param duration
     */
    public static void vibrate(Context context, long duration) {
        Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vb != null) {
            vb.vibrate(duration);
        }
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
     * Creates a uniformly structured key with the provided {@code name}, prepending the app's
     * package name. Package name explicitly declared here to allow this method to be called outside
     * the target's scope
     *
     * @param name
     * @return
     */
    public static String createStaticKeyString(String name) {
        return String.format("com.zn.expirytracker.%s", name);
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

    /**
     * Checks if user has internet connectivity
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                                        @NonNull ImageView imageView,
                                        RequestListener<Bitmap> listener) {
        getGlideRequestForLoadingImage(context, sourceUrl, listener)
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
                .thumbnail(Constants.GLIDE_THUMBNAIL_MULTIPLIER) // ideally, this thumbnail request points to a low-res url of the same image
                .transition(BitmapTransitionOptions.withCrossFade());
    }


    /**
     * Helper to show or hide the page indicator
     *
     * @param show
     */
    public static void showPageIndicator(boolean show, ImageView imageScrim,
                                         PageIndicatorView pageIndicatorView) {
        if (show) {
            imageScrim.animate().setStartDelay(Constants.PAGE_INDICATOR_FADE_IN_DELAY)
                    .setDuration(Constants.PAGE_INDICATOR_FADE_IN_DURATION).alpha(1f);
            pageIndicatorView.animate().setStartDelay(Constants.PAGE_INDICATOR_FADE_IN_DELAY)
                    .setDuration(Constants.PAGE_INDICATOR_FADE_IN_DURATION).alpha(1f);
        } else {
            imageScrim.animate().setStartDelay(Constants.PAGE_INDICATOR_FADE_OUT_DELAY)
                    .setDuration(Constants.PAGE_INDICATOR_FADE_OUT_DURATION).alpha(0f);
            pageIndicatorView.animate().setStartDelay(Constants.PAGE_INDICATOR_FADE_OUT_DELAY)
                    .setDuration(Constants.PAGE_INDICATOR_FADE_OUT_DURATION).alpha(0f);
        }
    }

    /**
     * Converts a string input into title case
     * <p>
     * https://stackoverflow.com/questions/1086123/string-conversion-to-title-case
     *
     * @param input
     * @return
     */
    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    /**
     * Saves a bitmap into internal storage for this app, with the following filename syntax:
     * {@code currentTimeInMillis_filename.jpg}
     * <p>
     * https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
     *
     * @param bitmap   {@code Bitmap} to be saved into the device's internal directory
     * @param filename
     * @param context
     * @return {@code String} representing the filepath of the stored file
     * @throws IOException
     */
    public static String saveBitmapToInternalStorage(Bitmap bitmap, String filename, Context context)
            throws IOException {
        ContextWrapper cw = new ContextWrapper(context);
        filename = System.currentTimeMillis() + "_" + filename + ".jpg";
        // path to /data/data/{yourapp}/{app_data}/scannedImagesDir
        File directory = cw.getDir(Constants.BITMAP_SAVING_DIRECTORY, Context.MODE_PRIVATE);
        File path = new File(directory, filename);
        FileOutputStream fos;
        fos = new FileOutputStream(path);
        // Use the compress method on the Bitmap object to write image to fos
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.BITMAP_SAVING_QUALITY, fos);
        fos.close();
        return path.getAbsolutePath();
    }

    public static void deleteBitmapFromInternalStorage(Uri uri) {
        // TODO: Implement and send to FoodViewModel
    }

    /**
     * Returns a path prefixed with {@code "file://"}
     *
     * @param path
     * @param context
     * @return
     */
    public static String getInternalStorageUriString(String path, Context context) {
        return context.getString(R.string.internal_storage_uri_path, path);
    }
}