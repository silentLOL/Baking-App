package at.stefanirndorfer.bakingapp.data.source.fake;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import java.util.LinkedHashMap;
import java.util.List;
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
    public MutableLiveData<List<Recipe>> getRecipes() {
        return null;
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
    public void deleteRecipe(int recipeId) {

    }

    @Override
    public MutableLiveData<List<Step>> getStepsForRecipe(int recipeId) {
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
    public MutableLiveData<List<Ingredient>> getIngredientsForRecipe(int recipeId) {
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

    @Override
    public void loadImageForRecipe(Context context, ImageView target, String imageUrl, Callback callback) {

    }
}
