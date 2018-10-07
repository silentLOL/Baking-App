package at.stefanirndorfer.bakingapp.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.Step;

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

    interface LoadStepsCallback {

        void onStepsLoaded(List<Step> steps);

        void onDataNotAvailable();
    }


    interface LoadIngredientsCallback {

        void onIngredientsLoaded(List<Ingredient> ingredients);

        void onDataNotAvailable();
    }


    //--------------------------------
    // Recipes
    //--------------------------------
    void getRecipes(@NonNull LoadRecipesCallback callback);

    void getRecipe(@NonNull String recipeId, @NonNull GetRecipeCallback callback);

    void saveRecipe(@NonNull Recipe recipe);

    void refreshRecipes();

    void deleteAllRecipes();

    void deleteRecipe(@NonNull String recipeId);

    //--------------------------------
    // Steps
    //--------------------------------

    void getStepsForRecipe(@NonNull String recipeId, @NonNull LoadStepsCallback callback);

    void deleteAllSteps();

    void deleteStepsForRecipe(final int recipeId);

    void saveStep(Step step);

    //--------------------------------
    // Ingredients
    //--------------------------------

    void getIngredientsForRecipe(@NonNull String recipeId, @NonNull LoadIngredientsCallback callback);

    void deleteAllIngredients();

    void deleteIngredientsForRecipe(final int recipeId);

    void saveIngredient(Ingredient ingredient);
}
