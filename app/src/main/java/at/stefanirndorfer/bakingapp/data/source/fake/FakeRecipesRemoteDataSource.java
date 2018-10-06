package at.stefanirndorfer.bakingapp.data.source.fake;

import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesDataSource;

public class FakeRecipesRemoteDataSource implements RecipesDataSource {

    private static FakeRecipesRemoteDataSource INSTANCE;

    private static final Map<String, Recipe> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeRecipesRemoteDataSource() {}

    public static FakeRecipesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeRecipesRemoteDataSource();
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
