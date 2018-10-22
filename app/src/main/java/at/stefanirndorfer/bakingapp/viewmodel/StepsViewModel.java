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

import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;
import timber.log.Timber;

public class StepsViewModel extends AndroidViewModel {

    //TODO: Check if LiveData make Observable values obsolete
    public final ObservableBoolean dataLoading = new ObservableBoolean(false);
    private MutableLiveData<List<Step>> mSteps = new MutableLiveData<>();


    private final RecipesRepository mRecipeRepository;
    private final Context mContext; /* Application Context! */

    public StepsViewModel(@NonNull Application application, RecipesRepository repository) {
        super(application);
        this.mContext = application.getApplicationContext();
        mRecipeRepository = repository;
    }

    /**
     * requestes the ingredients for the
     *
     * @param recipeId
     */
    public void start(int recipeId) {
        Timber.d("Requesting steps for recipe id " + recipeId + " from the data sources");
        loadSteps(recipeId, true);
    }

    private void loadSteps(int recipeId, boolean showLoadingUI) {
        if (showLoadingUI) {
            //TODO: come up with a loading-ui
            dataLoading.set(true);
        }
        mRecipeRepository.getStepsForRecipe(recipeId).observeForever(steps -> {
            Timber.d("Received " + steps.size() + " steps from repository.");
            mSteps.setValue(steps);
            dataLoading.set(false);
        });
    }

    public MutableLiveData<List<Step>> getSteps() {
        return mSteps;
    }

}
