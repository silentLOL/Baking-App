package at.stefanirndorfer.bakingapp.data.source.fake;

import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.Step;
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
    public void refreshRecipes() {

    }

    @Override
    public void deleteAllRecipes() {

    }

    @Override
    public void deleteRecipe(@NonNull String recipeId) {

    }

    @Override
    public void getStepsForRecipe(@NonNull String recipeId, @NonNull LoadStepsCallback callback) {

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
    public void getIngredientsForRecipe(@NonNull String recipeId, @NonNull LoadIngredientsCallback callback) {

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
