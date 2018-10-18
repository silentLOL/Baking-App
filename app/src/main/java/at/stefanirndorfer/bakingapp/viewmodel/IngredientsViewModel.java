package at.stefanirndorfer.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;
import timber.log.Timber;

public class IngredientsViewModel extends AndroidViewModel {

    //TODO: Check if LiveData make Observable values obsolete
    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    private MutableLiveData<List<Ingredient>> mIngredients = new MutableLiveData<List<Ingredient>>();
    private final RecipesRepository mRecipeRepository;
    private final Context mContext; /* Application Context! */

    public IngredientsViewModel(@NonNull Application application, RecipesRepository repository) {
        super(application);
        this.mContext = application.getApplicationContext();
        mRecipeRepository = repository;
    }

    /**
     * requestes the ingredients for the
     * @param recipeId
     */
    public void start(int recipeId) {
        Timber.d("Requesting ingredients for recipe id " + recipeId + " from the data sources");
        loadIngredients(recipeId, true);
    }

    private void loadIngredients(int recipeId, boolean showLoadingUI) {
        if (showLoadingUI) {
            //TODO: come up with a loading-ui
            dataLoading.set(true);
        }
        mRecipeRepository.getIngredientsForRecipe(recipeId).observeForever(new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                Timber.d("Received " + ingredients.size() + " ingredients from repository.");
                mIngredients.setValue(ingredients);
                dataLoading.set(false);
            }
        });
    }

    public MutableLiveData<List<Ingredient>> getIngredients() {
        return mIngredients;
    }
}
