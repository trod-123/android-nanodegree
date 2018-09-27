package com.zn.expirytracker.data.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.URLUtil;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zn.expirytracker.data.contracts.DatabaseContract;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.utils.AuthToolbox;
import com.zn.expirytracker.utils.Toolbox;

import java.util.List;

import timber.log.Timber;

/**
 * Set of functions used to interface with Firebase Storage
 */
public class FirebaseStorageHelper {

    private static StorageReference mStorage = FirebaseStorage.getInstance()
            .getReference(DatabaseContract.DATABASE_NAME + "/" +
                    DatabaseContract.COLUMN_IMAGES);

    /**
     * Replaces local uris with Firebase Storage uris by iterating for local uris in the
     * {@link Food}'s image list, uploading those images, and then replacing the original
     * uris with Firebase Storage download uris. Does nothing to Web uris.
     * <p>
     * Updates Firebase RTD once all images have been uploaded
     * <p>
     * Images are stored in FS with the following path syntax: {@code root/userId/foodId/image}
     * <p>
     * This design assumes local uris are loaded at the end of the list. This should be fine
     * for the purposes of this app, since local images are always at the end of image lists,
     * and images never get rearranged
     *
     * @param food
     */
    public static void uploadAllLocalUrisToFirebaseStorage(final Food food) {
        final List<String> imagesUris = food.getImages();
        final String foodId = String.valueOf(food.get_id());
        boolean isThereLocal = false;
        for (int i = 0; i < imagesUris.size(); i++) {
            String imageUriString = imagesUris.get(i);
            // Check form
            UriType type = getImageUriType(imageUriString);
            if (type == UriType.LOCAL) {
                isThereLocal = true;
                // Only take action on uris referencing internal storage. Get the Uri form. This
                // only works with local file paths
                // https://firebase.google.com/docs/storage/android/upload-files
                Uri file = Toolbox.getUriFromImagePath(imageUriString);

                // Get the user id, to serve as first child
                String uid = AuthToolbox.getUserId();
                final StorageReference ref = mStorage.child(uid).child(foodId)
                        .child(file.getLastPathSegment());
                final int index = i;
                ref.putFile(file)
                        .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Timber.e(task.getException(), "firebase/fs: upload failed");
                                }
                                // Continue with the task to get the download URL
                                return ref.getDownloadUrl();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    imagesUris.set(index, downloadUri.toString());
                                } else {
                                    Timber.e(task.getException(), "firebase/fs: upload failed");
                                }
                                if (index == imagesUris.size() - 1) {
                                    // Once we reach the end, update the db, regardless if successful
                                    updateFoodImagesToFirebaseRTD(food, imagesUris);
                                }
                            }
                        });
            }
        }
        if (!isThereLocal) {
            // If there are no images that need to be uploaded to FS, just write to RTD. Since we've
            // only called RTD through the listeners, this line is needed to get to RTD
            FirebaseDatabaseHelper.write(food);
        }
    }

    /**
     * Deletes all of the images in Firebase Storage for the provided Food item
     * <p>
     * Preferably this method should be called after completion of the Food's deletion in Firebase
     * RTD so there are no dangling image references
     * (Concept: https://stackoverflow.com/questions/48527169/firebase-when-deleting-from-storage-and-database-should-the-storage-deletion-b)
     * <p>
     * Note: There is currently no way to delete a directory directly, so we will need to delete
     * each of the contents individually
     *
     * @param food
     */
    public static void delete(Food food) {
        final List<String> imagesUris = food.getImages();
        String foodId = String.valueOf(food.get_id());
        for (int i = 0; i < imagesUris.size(); i++) {
            final String imageUriString = imagesUris.get(i);
            // Check form
            UriType type = getImageUriType(imageUriString);
            if (type == UriType.FBS) {
                // Get the filename
                // https://stackoverflow.com/questions/11575943/parse-file-name-from-url-before-downloading-the-file
                String name = URLUtil.guessFileName(imageUriString, null, null);
                // Get the user id, to serve as first child
                String uid = AuthToolbox.getUserId();
                final StorageReference ref = mStorage.child(uid).child(foodId)
                        .child(name);
                ref.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Timber.d("firebase/fs: delete successful");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Timber.e(e, "firebase/fs: delete failed");
                            }
                        });
            }
        }
    }

    /**
     * Updates a food object with the provided list of image uris, and then sends the update to RTD
     *
     * @param food
     * @param imageUris
     */
    private static void updateFoodImagesToFirebaseRTD(Food food, List<String> imageUris) {
        food.setImages(imageUris);
        FirebaseDatabaseHelper.write(food);
    }

    // region Uri Matching

    public enum UriType {
        LOCAL, WEB, FBS
    }

    // We can be confident that Firebase Storage and Web uris will always start with the following
    // There is a chance the Local directory may be different across devices
    private static final String URI_FBS_PREFIX = "https://firebasestorage.googleapis.com/";
    private static final String URI_WEB_PREFIX = "http";

    private static UriType getImageUriType(@NonNull String imageUriString) {
        if (imageUriString.startsWith(URI_FBS_PREFIX)) return UriType.FBS;
        if (imageUriString.startsWith(URI_WEB_PREFIX)) return UriType.WEB;
        return UriType.LOCAL;
    }

    // endregion

}
