package at.stefanirndorfer.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;

public class MainViewModel extends AndroidViewModel {

    // These observable fields will update Views automatically
    public final ObservableList<Recipe> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    private final Context mContext; // To avoid leaks, this must be an Application Context.

    private final RecipesRepository mRecipeRepository;

    private MutableLiveData<List<Recipe>> mRecipesLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application context, RecipesRepository repository) {
        super(context);
        this.mContext = context.getApplicationContext();
        mRecipeRepository = repository;
    }

    public void start() {
        loadRecipes(true);
    }

    /**
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadRecipes(boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        mRecipesLiveData = mRecipeRepository.getRecipes();
    }

    public MutableLiveData<List<Recipe>> getRecipesLiveData() {
        return mRecipesLiveData;
    }
}
