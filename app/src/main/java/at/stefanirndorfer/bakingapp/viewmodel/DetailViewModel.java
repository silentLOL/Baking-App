package at.stefanirndorfer.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import java.net.HttpCookie;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;
import timber.log.Timber;

public class DetailViewModel extends AndroidViewModel {

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    private final Context mContext; // To avoid leaks, this must be an Application Context.

    private final RecipesRepository mRecipeRepository;
    private MutableLiveData<Recipe> mDetailedRecipeLiveData = new MutableLiveData<>();

    public DetailViewModel(@NonNull Application context, RecipesRepository repository) {
        super(context);
        this.mContext = context.getApplicationContext();
        mRecipeRepository = repository;
    }

    public void start(int recipeId) {
        Timber.d("Requesting details for recipe id " + recipeId + " from the data sources");
        loadRecipeDetails(recipeId, true);
    }

    private void loadRecipeDetails(int recipeId, boolean showLoadingUI) {
        if (showLoadingUI) {
            //TODO: come up with a loading-ui
            dataLoading.set(true);
        }
        mRecipeRepository.getDetailedRecipe(recipeId).observeForever(recipe -> mDetailedRecipeLiveData.setValue(recipe));
    }

    public MutableLiveData<Recipe> getmDetailedRecipeLiveData() {
        return mDetailedRecipeLiveData;
    }
}

