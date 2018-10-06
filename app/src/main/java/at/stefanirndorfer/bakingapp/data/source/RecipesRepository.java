package at.stefanirndorfer.bakingapp.data.source;

import android.support.annotation.NonNull;

import java.util.Map;

import at.stefanirndorfer.bakingapp.data.Recipe;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation to load recipes from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 * <p>
 * //TODO: Implement this class using LiveData.
 */
public class RecipesRepository implements RecipesDataSource {


    private volatile static RecipesRepository INSTANCE = null;
    private final RecipesDataSource mRecipesRemoteDataSourec;
    private final RecipesDataSource mRecipesLocalDataSourec;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Recipe> mCachedRecipes;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private RecipesRepository(@NonNull RecipesDataSource recipesRemoteDataSource,
                              @NonNull RecipesDataSource recipesLocalDataSource) {
        mRecipesRemoteDataSourec = checkNotNull(recipesRemoteDataSource);
        mRecipesLocalDataSourec = checkNotNull(recipesLocalDataSource);
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
    public void getRecipes(@NonNull LoadRecipesCallback callback) {

    }

    @Override
    public void getRecipe(@NonNull String recipeId, @NonNull GetRecipeCallback callback) {

    }

    @Override
    public void saveRecipe(@NonNull Recipe recipe) {

    }

    @Override
    public void completeRecipe(@NonNull Recipe recipe) {

    }

    @Override
    public void completeRecipe(@NonNull String recipeId) {

    }

    @Override
    public void activateRecipe(@NonNull Recipe recipe) {

    }

    @Override
    public void activateRecipe(@NonNull String recipeId) {

    }

    @Override
    public void clearCompletedRecipes() {

    }

    @Override
    public void refreshRecipes() {

    }

    @Override
    public void deleteAllRecipes() {

    }

    @Override
    public void deleteRecipe(@NonNull String recipeId) {

    }
}
