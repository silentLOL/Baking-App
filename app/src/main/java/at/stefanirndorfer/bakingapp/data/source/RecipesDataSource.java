package at.stefanirndorfer.bakingapp.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;

/**
 * Main entry point for accessing Recipes data.
 */
public interface RecipesDataSource {
    interface LoadRecipesCallback {

        void onRecipesLoaded(List<Recipe> recipes);

        void onDataNotAvailable();
    }

    interface GetRecipeCallback {

        void onRecipeLoaded(Recipe recipe);

        void onDataNotAvailable();
    }

    void getRecipes(@NonNull LoadRecipesCallback callback);

    void getRecipe(@NonNull String recipeId, @NonNull GetRecipeCallback callback);

    void saveRecipe(@NonNull Recipe recipe);

    void completeRecipe(@NonNull Recipe recipe);

    void completeRecipe(@NonNull String recipeId);

    void activateRecipe(@NonNull Recipe recipe);

    void activateRecipe(@NonNull String recipeId);

    void clearCompletedRecipes();

    void refreshRecipes();

    void deleteAllRecipes();

    void deleteRecipe(@NonNull String recipeId);
}
