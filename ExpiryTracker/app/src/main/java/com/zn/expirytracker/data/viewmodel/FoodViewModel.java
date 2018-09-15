package com.zn.expirytracker.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.zn.expirytracker.data.FoodRepository;
import com.zn.expirytracker.data.model.Food;

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
        mRepository = new FoodRepository(application);
    }

    public void insert(Food food) {
        mRepository.insertFood(food);
    }

    public void insert(Food... foods) {
        mRepository.insertFoods(foods);
    }

    public void update(Food food) {
        mRepository.updateFood(food);
    }

    public void update(Food... foods) {
        mRepository.updateFoods(foods);
    }

    // TODO: When deleting, delete all user-submitted pictures too

    public void delete(long id) {
        mRepository.deleteFoodById(id);
    }

    public void delete(Long... ids) {
        mRepository.deleteFoodsByIds(ids);
    }

    public void deleteAllFoods() {
        mRepository.deleteAllFoods();
    }

    public LiveData<Food> getSingleFoodById(long id, boolean summaryColumns) {
        return mRepository.getSingleFoodById(id, summaryColumns);
    }

    public LiveData<PagedList<Food>> getAllFoods(boolean summaryColumns) {
        return mRepository.getAllFoods(summaryColumns);
    }

    public LiveData<PagedList<Food>> getAllFoodsExpiringBeforeDate(long date, boolean summaryColumns) {
        return mRepository.getAllFoodsExpiringBeforeDate(date, summaryColumns);
    }
}
