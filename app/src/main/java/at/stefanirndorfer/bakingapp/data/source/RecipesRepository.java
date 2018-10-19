package at.stefanirndorfer.bakingapp.data.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Object lock = new Object();

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Recipe> mCachedRecipes;
    final MutableLiveData<List<Recipe>> mRecipiesLiveData = new MutableLiveData<>();

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private boolean mRecipesCacheIsDirty = false;


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


    /**
     * this call returns a list of recipes. If the source of the data base
     * Ingredients and Steps are left null.
     * Use {@link #getDetailedRecipe(int)} to get a complete recipe object
     *
     * @return livedata to allow an observation of the resulting Recipes List
     */
    @Override
    public MutableLiveData<List<Recipe>> getRecipes() {
        if (mCachedRecipes != null && !mRecipesCacheIsDirty) {
            Timber.d("Recipe data are already cached.");
            mRecipiesLiveData.postValue(new ArrayList<>(mCachedRecipes.values()));
        }
        if (!mRecipesCacheIsDirty) {
            // lets ask the local source first ...
            mRecipesLocalDataSource.getRecipes().observeForever(recipes -> {
                if (recipes != null && !recipes.isEmpty()) {
                    cacheRecipes(recipes);
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
     * initializes {@link #mCachedRecipes} in case it is null
     * or clears it in case it is not null
     * and populates the HashMap with newly acquired data
     *
     * @param recipes
     */
    private void cacheRecipes(List<Recipe> recipes) {
        if (mCachedRecipes != null) {
            mCachedRecipes.clear();
        } else {
            mCachedRecipes = new HashMap<>();
        }
        cacheRecipeListAsMap(recipes);
        mRecipesCacheIsDirty = false;
    }

    private void cacheRecipeListAsMap(List<Recipe> recipes) {
        for (Recipe currRecipe : recipes) {
            mCachedRecipes.put(currRecipe.getId().toString(), currRecipe);
        }
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
                cacheRecipes(recipes);
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

    /**
     * this call ensures a complete Recipe object including Ingredients and Steps
     *
     * @param recipeId
     * @return
     */
    public LiveData<Recipe> getDetailedRecipe(int recipeId) {
        final MutableLiveData<Recipe> detailedRecipe = new MutableLiveData<>();
        Recipe resultingRecipe;
        String key = Integer.toString(recipeId);
        if (mCachedRecipes.containsKey(key)) {
            resultingRecipe = mCachedRecipes.get(key);
            Timber.d("Found recipe with id " + recipeId + "in the cached recipes");
            supplementSteps(recipeId, detailedRecipe, resultingRecipe, key);
            supplementIngredients(recipeId, detailedRecipe, resultingRecipe, key);

            if (resultingRecipe.getIngredients() != null && resultingRecipe.getSteps() != null) {
                detailedRecipe.postValue(resultingRecipe);
            }
        }
        return detailedRecipe;
    }

    private void supplementSteps(int recipeId, MutableLiveData<Recipe> detailedRecipe, Recipe resultingRecipe, String key) {
        if (resultingRecipe.getSteps() == null || resultingRecipe.getSteps().isEmpty()) {
            Timber.d("Requesting steps for recipe id: " + recipeId + " from database");
            mRecipesLocalDataSource.getStepsForRecipe(recipeId).observeForever(steps -> {
                resultingRecipe.setSteps(steps);
                if (steps != null && !steps.isEmpty()) {
                    saveReplaceRecipe(resultingRecipe, key);
                }
                detailedRecipe.postValue(resultingRecipe);
            });
        }
    }

    private void supplementIngredients(int recipeId, MutableLiveData<Recipe> detailedRecipe, Recipe resultingRecipe, String key) {
        if (resultingRecipe.getIngredients() == null || resultingRecipe.getIngredients().isEmpty()) {
            Timber.d("Requesting ingredients for recipe id: " + recipeId + " from database");
            mRecipesLocalDataSource.getIngredientsForRecipe(recipeId).observeForever(ingredients -> {
                resultingRecipe.setIngredients(ingredients);
                if (ingredients != null && !ingredients.isEmpty()) {
                    saveReplaceRecipe(resultingRecipe, key);
                }
                detailedRecipe.postValue(resultingRecipe);
            });
        }
    }

    private void saveReplaceRecipe(Recipe resultingRecipe, String key) {
        synchronized (lock) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mCachedRecipes.replace(key, resultingRecipe);
            } else {
                mCachedRecipes.remove(key); /* replace is not available on our api-level */
                mCachedRecipes.put(key, resultingRecipe);

            }
        }
    }

    @Override
    public MutableLiveData<Recipe> getRecipe(int recipeId) {
        return null;
    }

    @Override
    public void saveRecipe(@NonNull Recipe recipe) {

    }

    @Override
    public void deleteAllRecipes() {

    }

    @Override
    public void deleteRecipe(int recipeId) {

    }

    @Override
    public MutableLiveData<List<Step>> getStepsForRecipe(int recipeId) {
        final MutableLiveData<List<Step>> stepsLiveData = new MutableLiveData<>();
        String key = Integer.toString(recipeId);
        if (mCachedRecipes.containsKey(key)) {
            List<Step> steps = mCachedRecipes.get(key).getSteps();
            if (steps != null && !steps.isEmpty()) {
                stepsLiveData.postValue(steps);
            } else {
                mRecipesLocalDataSource.getStepsForRecipe(recipeId).observeForever(stepsReturningValue -> {
                    stepsLiveData.postValue(stepsReturningValue);
                    mCachedRecipes.get(key).setSteps(stepsReturningValue);
                });
            }
        }
        return stepsLiveData;
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
    public MutableLiveData<List<Ingredient>> getIngredientsForRecipe(int recipeId) {
        final MutableLiveData<List<Ingredient>> ingredientLiveData = new MutableLiveData<>();
        String key = Integer.toString(recipeId);
        if (mCachedRecipes.containsKey(key)) {
            List<Ingredient> ingredients = mCachedRecipes.get(key).getIngredients();
            if (ingredients != null && !ingredients.isEmpty()) {
                ingredientLiveData.postValue(ingredients);
            } else {
                mRecipesLocalDataSource.getIngredientsForRecipe(recipeId).observeForever(new Observer<List<Ingredient>>() {
                    @Override
                    public void onChanged(@Nullable List<Ingredient> ingredients) {
                        ingredientLiveData.postValue(ingredients);
                        mCachedRecipes.get(key).setIngredients(ingredients);
                    }
                });
            }
        }
        return ingredientLiveData;
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

    @Override
    public void loadImageForRecipe(Context context, ImageView target, String imageUrl, Callback callback) {
        mRecipesRemoteDataSource.loadImageForRecipe(context, target, imageUrl, callback);
    }

}
