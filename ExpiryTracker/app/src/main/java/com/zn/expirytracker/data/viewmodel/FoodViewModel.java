package com.zn.expirytracker.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.zn.expirytracker.data.FoodRepository;
import com.zn.expirytracker.data.model.Food;
import com.zn.expirytracker.utils.AuthToolbox;

import java.util.List;

import timber.log.Timber;

/**
 * ViewModels live beyond the activity and fragment lifecycle, so it can serve as an effective
 * holder for your data that would be presented to the users. Interact with your repository
 * exclusively through the ViewModel. Don't reference your repository outside of the ViewModel
 * to guarantee that your data live across configuration changes, and to ensure that the ViewModel
 * is singularly responsible for interacting with the repository
 * <p>
 * Use LiveData so the data in the ViewModel are automatically updated when the database changes
 * <p>
 * The AndroidViewModel is used if you need to reference the Application context. It is bad practice
 * to pass in and store references to Activity, Fragment, or View instances as these can be
 * destroyed and recreated many times, leading to references that point to the destroyed objects.
 * This poses risks for memory leaks, so use AndroidViewModel so you can reference your
 * Application's context
 */
public class FoodViewModel extends AndroidViewModel {
    private FoodRepository mRepository;

    public FoodViewModel(@NonNull Application application) {
        super(application);
        Timber.d("FoodViewModel/the listener - creating view model");
        mRepository = new FoodRepository(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.d("FoodViewModel/the listener - clearing view model");
        // Keep network data transfer to a minimum by removing the listeners
        if (AuthToolbox.isSignedIn()) mRepository.stopListeningForFoodChanges();
    }

    public void insert(boolean saveToCloud, Food food) {
        mRepository.insertFood(saveToCloud, food);
    }

    public void insert(boolean saveToCloud, Food... foods) {
        mRepository.insertFoods(saveToCloud, foods);
    }

    public void upload() {
        mRepository.uploadAllFoods();
    }

    public void update(boolean saveToCloud, Food food) {
        mRepository.updateFood(saveToCloud, food);
    }

    public void update(boolean saveToCloud, Food... foods) {
        mRepository.updateFoods(saveToCloud, foods);
    }

    public void delete(boolean wipeCloudStorage, Food food) {
        mRepository.deleteFood(wipeCloudStorage, food);
    }

    public void delete(boolean wipeCloudStorage, Food... foods) {
        mRepository.deleteFoods(wipeCloudStorage, foods);
    }

    public void deleteImages(boolean removeFromCloud, List<String> imageUris, long foodId) {
        mRepository.deleteImages(removeFromCloud, imageUris, foodId);
    }

//    public void deleteAllFoods(boolean wipeCloudStorage) {
//        mRepository.deleteAllFoods(wipeCloudStorage);
//    }

    public LiveData<Food> getSingleFoodById(long id, boolean summaryColumns) {
        return mRepository.getSingleFoodById(id, summaryColumns);
    }

    public LiveData<PagedList<Food>> getAllFoods(boolean summaryColumns) {
        return mRepository.getAllFoods(summaryColumns);
    }

    public LiveData<PagedList<Food>> getAllFoodsExpiringBeforeDate(long date, boolean summaryColumns) {
        return mRepository.getAllFoodsExpiringBeforeDate(date, summaryColumns);
    }

    /**
     * Needs to be called from a background thread
     */
    public List<Food> getAllFoods_List() {
        return mRepository.getAllFoods_List();
    }
}
