package at.stefanirndorfer.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesDataSource;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;

/**
 * Exposes the data to be used in the main screen.
 * <p>
 * This ViewModel uses both {@link ObservableField}s ({@link ObservableBoolean}s in this case) and
 * {@link Bindable} getters. The values in {@link ObservableField}s are used directly in the layout,
 * whereas the {@link Bindable} getters allow us to add some logic to it. This is
 * preferable to having logic in the XML layout.
 */
public class MainActivityViewModel extends AndroidViewModel {

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);
    public final ObservableBoolean error = new ObservableBoolean(false);
    public final ObservableField<String> numberOfRecipes = new ObservableField<>();

    private int mNumberOfRecipes = 0;


    private final RecipesRepository mRecipesRepository;

    public MainActivityViewModel(@NonNull Application application, RecipesRepository recipesRepository) {
        super(application);
        mRecipesRepository = recipesRepository;
    }

    public void start() {
        loadStatistics();
    }

    public void loadStatistics() {
        dataLoading.set(true);

        mRecipesRepository.getRecipes(new RecipesDataSource.LoadRecipesCallback() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                error.set(false);
                // do something with the data
            }

            @Override
            public void onDataNotAvailable() {
                error.set(true);
                mNumberOfRecipes = 0;
            }
        });
    }
}
