package com.zn.expirytracker.utils;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.rd.PageIndicatorView;
import com.zn.expirytracker.GlideApp;
import com.zn.expirytracker.GlideRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

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
     * Creates a uniformly structured key with the provided {@code name}, prepending the passed
     * class name.
     *
     * @param klass
     * @param name
     * @return
     */
    public static String createStaticKeyString(Class klass, String name) {
        return String.format("%s.%s", klass.getSimpleName(), name);
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
     * Generates a Bitmap thumbnail from a source url. Can only be called on a background thread
     *
     * @param context
     * @param sourceUrl
     * @return
     * @throws Throwable
     */
    public static Bitmap getThumbnailFromUrl(Context context, String sourceUrl) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context).asBitmap()
                    .load(sourceUrl)
                    .apply(options)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .thumbnail(Constants.GLIDE_THUMBNAIL_MULTIPLIER)
                    .submit()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Timber.e("There was an error getting the thumbnail from the ui: %s",
                    e.getMessage());
        }
        return bitmap;
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
     * Saves a bitmap into internal storage for this app
     * <p>
     * https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
     *
     * @param bitmap   {@code Bitmap} to be saved into the device's internal directory
     * @param filename
     * @param context
     * @return {@code String} representing the filepath of the stored file, which can safely be
     * used in Glide
     * @throws IOException
     */
    public static String saveBitmapToInternalStorage(Bitmap bitmap, String filename, Context context)
            throws IOException {
        ContextWrapper cw = new ContextWrapper(context);
        File path = getBitmapSavingFilePath(cw, filename);
        FileOutputStream fos;
        fos = new FileOutputStream(path);
        // Use the compress method on the Bitmap object to write image to fos
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.BITMAP_SAVING_QUALITY, fos);
        fos.close();
        return path.getAbsolutePath();
    }

    /**
     * Generates a file to be used for outputting a bitmap, with the following filename syntax:
     * {@code currentTime_filename.jpg}
     * <p>
     * Call File.getAbsolutePath() on the result to return a String that represents the filepath
     * of the file, which can safely be used in Glide
     * <p>
     * https://developer.android.com/training/camera/photobasics
     *
     * @param context
     * @param filename
     * @return
     */
    public static File getBitmapSavingFilePath(Context context, String filename) throws IOException {
        filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + filename;
        // filepath: Android/data/com.zn.expirytracker.free.debug/files/Pictures
        // (also defined in file_paths.xml)
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Timber.d("Image directory: %s", directory.getAbsolutePath());
        return File.createTempFile(filename, ".jpg", directory);
    }

    /**
     * Recursively deletes all files and folders
     * <p>
     * https://stackoverflow.com/questions/4943629/how-to-delete-a-whole-folder-and-content
     *
     * @param fileOrDirectory
     * @return {@code true} if the deletion was successful, false otherwise
     */
    private static boolean deleteRecursive(@NonNull File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        return fileOrDirectory.delete();
    }

    /**
     * Deletes the app's bitmap directory and all of its contents
     *
     * @param context
     */
    public static void deleteBitmapDirectory(Context context) {
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (directory != null) {
            deleteRecursive(directory);
        } else {
            Timber.e("There was an issue deleting the bitmap directory");
        }
    }

    /**
     * Deletes a single file, assuming it is in app's internal data
     *
     * @param uriToDelete
     * @return {@code true} if the file is successfully deleted, false otherwise
     */
    public static boolean deleteBitmapFromInternalStorage(Uri uriToDelete) {
        File file = new File(uriToDelete.getPath());
        return file.delete();
    }

    /**
     * Copies a file
     * <p>
     * https://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    /**
     * Returns the path for a Uri obtained through {@code Intent.ACTION_PICK} for the gallery so
     * it can be fed into new File(path)
     * <p>
     * The purpose of this method is to make a copy of an image from a user's library and save it
     * to the app's internal photo directory
     * <p>
     * http://stackoverflow.com/q/6935497/42619
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getGalleryUriPath(Activity context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        context.startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Returns a Uri for a given path (e.g. /storage/emulated/0/Android/data/) through making a temp
     * file
     * <p>
     * The purpose of this method is to allow local image uris to be uploaded to FBS
     *
     * @param imagePath
     * @return
     */
    public static Uri getUriFromImagePath(String imagePath) {
        return Uri.fromFile(new File(imagePath));
    }
}