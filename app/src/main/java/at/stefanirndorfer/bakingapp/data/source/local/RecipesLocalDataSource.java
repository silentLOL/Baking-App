package at.stefanirndorfer.bakingapp.data.source.local;

import android.support.annotation.NonNull;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesDataSource;
import at.stefanirndorfer.bakingapp.util.AppExecutors;


/**
 * Concrete implementation of a data source as a db.
 */
public class RecipesLocalDataSource implements RecipesDataSource {

    private static volatile RecipesLocalDataSource INSTANCE;

    private RecipesDao mRecipesDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private RecipesLocalDataSource(@NonNull AppExecutors appExecutors,
                                   @NonNull RecipesDao recipesDao) {
        mAppExecutors = appExecutors;
        mRecipesDao = recipesDao;
    }

    public static RecipesLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                     @NonNull RecipesDao recipesDao) {
        if (INSTANCE == null) {
            synchronized (RecipesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecipesLocalDataSource(appExecutors, recipesDao);
                }
            }
        }
        return INSTANCE;
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
