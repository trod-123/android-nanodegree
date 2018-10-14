package com.zn.expirytracker.utils;

import android.animation.Animator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
     * Retrieves the version name from the app level build.gradle file
     * <p>
     * https://stackoverflow.com/questions/4616095/how-to-get-the-build-version-number-of-your-android-application
     *
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static String getAppVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return pInfo.versionName;
    }

    /**
     * Returns true if device is currently in LTR layout
     * <p>
     * https://stackoverflow.com/questions/26549354/android-determine-if-device-is-in-right-to-left-language-layout
     *
     * @return
     */
    public static boolean isLeftToRightLayout() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR;
    }

    /**
     * Check if this device has a camera
     *
     * @param context
     * @return {@code true} if this device has a camera
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
    public static boolean deleteBitmapFromInternalStorage(Uri uriToDelete, String tag) {
        File file = new File(uriToDelete.getPath());
        boolean success = file.delete();
        if (success) {
            Timber.d("%s: local image delete success. uri: %s", tag, uriToDelete);
        } else {
            Timber.d("%s: local image delete failed. uri: %s", tag, uriToDelete);
        }
        return success;
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
     * Returns a cached path for a Uri obtained through {@code Intent.ACTION_PICK} so
     * it can be fed into new File(path). To make this work, a copy of the file is saved into a
     * cache, whose path is then returned
     * <p>
     * Source: https://stackoverflow.com/questions/43500164/getting-path-from-uri-from-google-photos-app
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getImagePathFromInputStreamUri(Context context, Uri uri) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri); // context needed
                File photoFile = createTemporalFileFrom(context, inputStream);
                filePath = photoFile.getPath();
            } catch (FileNotFoundException e) {
                Timber.e(e, "Error getting image path from input stream. uri: %s", uri);
            } catch (IOException e) {
                Timber.e(e, "Error getting image path from input stream. uri: %s", uri);
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }
        return filePath;
    }

    /**
     * Source: https://stackoverflow.com/questions/43500164/getting-path-from-uri-from-google-photos-app
     *
     * @param context
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static File createTemporalFileFrom(Context context, InputStream inputStream) throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];

            targetFile = createTemporalFile(context);
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        return targetFile;
    }

    /**
     * Source: https://stackoverflow.com/questions/43500164/getting-path-from-uri-from-google-photos-app
     *
     * @param context
     * @return
     */
    private static File createTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), "tempFile.jpg"); // context needed
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

    /**
     * Plays a beep sound
     * <p>
     * Sound source: https://freesound.org/people/zerolagtime/sounds/144418/
     * <p>
     * https://stackoverflow.com/questions/3289038/play-audio-file-from-the-assets-directory
     *
     * @param context
     * @throws IOException
     */
    public static void playBeep(Context context) throws IOException {
        AssetFileDescriptor afd = context.getAssets().openFd("beep.mp3");
        MediaPlayer mp = new MediaPlayer();
        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        afd.close();

        mp.prepare();
        mp.setVolume(0.25f, 0.25f);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    /**
     * Vibrates the device for a brief period
     *
     * @param context
     */
    public static void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(Constants.DEFAULT_VIBRATION_BUTTON_PRESS_LENGTH);
    }

}