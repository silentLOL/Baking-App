package at.stefanirndorfer.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;
import timber.log.Timber;

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
        Timber.d("Requesting data from the data sources");
        loadRecipes(true);
    }

    /**
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadRecipes(boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        mRecipeRepository.getRecipes().observeForever(recipes -> mRecipesLiveData.setValue(recipes));
    }

    public MutableLiveData<List<Recipe>> getRecipesLiveData() {
        return mRecipesLiveData;
    }

    /**
     * TODO: Implement
     * @param recipe
     */
    public void navigateToDetailScreen(Recipe recipe) {

    }
}
