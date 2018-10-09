package at.stefanirndorfer.bakingapp.data.source;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.Map;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.Step;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation to load recipes from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 * <p>
 */
public class RecipesRepository implements RecipesDataSource {


    private static final String TAG = RecipesRepository.class.getCanonicalName();
    private volatile static RecipesRepository INSTANCE = null;
    private final RecipesDataSource mRecipesRemoteDataSource;
    private final RecipesDataSource mRecipesLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    List<Recipe> mCachedRecipes;
    final MutableLiveData<List<Recipe>> mRecipiesLiveData = new MutableLiveData<>();
    Map<String, Step> mCachedSteps;
    Map<String, Ingredient> mCachedIngredients;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private boolean mRecipesCacheIsDirty = false;
    private boolean mStepsCacheIsDirty = false;
    private boolean mIngredientsCacheIsDirty = false;

    // Prevent direct instantiation.
    private RecipesRepository(@NonNull RecipesDataSource recipesRemoteDataSource,
                              @NonNull RecipesDataSource recipesLocalDataSource) {
        mRecipesRemoteDataSource = checkNotNull(recipesRemoteDataSource);
        mRecipesLocalDataSource = checkNotNull(recipesLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param recipesRemoteDataSource the backend data source
     * @param recipesLocalDataSource  the device storage data source
     * @return the {@link RecipesRepository} instance
     */
    public static RecipesRepository getInstance(RecipesDataSource recipesRemoteDataSource,
                                                RecipesDataSource recipesLocalDataSource) {
        if (INSTANCE == null) {
            synchronized (RecipesRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecipesRepository(recipesRemoteDataSource, recipesLocalDataSource);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(RecipesDataSource, RecipesDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public MutableLiveData<List<Recipe>> getRecipes() {
        final MutableLiveData<List<Recipe>> returningData = new MutableLiveData<>();
        if (mCachedRecipes != null && !mRecipesCacheIsDirty) {
            Timber.d("Recipe data are already cached.");
            returningData.setValue(mCachedRecipes);
        }
        if (!mRecipesCacheIsDirty) {
            // lets ask the local source first ...
            mRecipesLocalDataSource.getRecipes().observeForever(recipes -> {
                if (recipes != null && !recipes.isEmpty()) {
                    mCachedRecipes = recipes;
                    mRecipesCacheIsDirty = false;
                    mRecipiesLiveData.postValue(recipes);
                } else {
                    Timber.d("Empty or null result from database received.");
                    getRecipesFromRemoteDataSource();
                }
            });
        } else {
            // the local data need a refresh on the next request
            getRecipesFromRemoteDataSource();
        }
        return mRecipiesLiveData;
    }

    /**
     * fetches the full list of recipes including their nested Step and Ingredients from the network
     * and writes them into the db
     *
     * @return
     */
    private void getRecipesFromRemoteDataSource() {
        mRecipesRemoteDataSource.getRecipes().observeForever(recipes -> {
            if (recipes != null && !recipes.isEmpty()) {
                mCachedRecipes = recipes;
                processData(recipes);
                mRecipiesLiveData.setValue(recipes);
            }
        });
    }

    /**
     * caches and stores all the values
     *
     * @param recipes received from the network call
     *                the object is supposed to be fully blown and contains all steps and ingredients
     */
    private void processData(List<Recipe> recipes) {
        //TODO: Caching --> check if even needed
        Timber.d("Storing all data retrieved from the network source");
        writeNetworkResponseToDataBases(recipes);
    }

    /**
     * iterates over all the recipes and adds the recipeId value to the Ingredients and Steps
     * they will be the ForeignKeys in the DB and finally stores the data
     *
     * @param recipes
     */
    private void writeNetworkResponseToDataBases(List<Recipe> recipes) {
        for (Recipe currRecipe : recipes) {
            mRecipesLocalDataSource.saveRecipe(currRecipe);
            for (Step currStep : currRecipe.getSteps()) {
                currStep.setRecipeId(currRecipe.getId());
                mRecipesLocalDataSource.saveStep(currStep);
            }
            for (Ingredient currIngredient : currRecipe.getIngredients()) {
                currIngredient.setRecipeId(currRecipe.getId());
                mRecipesLocalDataSource.saveIngredient(currIngredient);
            }
        }
    }


    @Override
    public MutableLiveData<Recipe> getRecipe(@NonNull int recipeId) {
        return null;
    }

    @Override
    public void saveRecipe(@NonNull Recipe recipe) {

    }

    @Override
    public void deleteAllRecipes() {

    }

    @Override
    public void deleteRecipe(@NonNull String recipeId) {

    }

    @Override
    public MutableLiveData<List<Step>> getStepsForRecipe(@NonNull String recipeId) {
        return null;
    }

    @Override
    public void deleteAllSteps() {

    }

    @Override
    public void deleteStepsForRecipe(int recipeId) {

    }

    @Override
    public void saveStep(Step step) {

    }

    @Override
    public MutableLiveData<List<Ingredient>> getIngredientsForRecipe(@NonNull String recipeId) {
        return null;
    }

    @Override
    public void deleteAllIngredients() {

    }

    @Override
    public void deleteIngredientsForRecipe(int recipeId) {

    }

    @Override
    public void saveIngredient(Ingredient ingredient) {

    }
}
