package at.stefanirndorfer.bakingapp.data.source;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.Step;

/**
 * Main entry point for accessing Recipes data.
 */
public interface RecipesDataSource {


    //--------------------------------
    // Recipes
    //--------------------------------
    MutableLiveData<List<Recipe>> getRecipes();

    MutableLiveData<Recipe> getRecipe(int recipeId);

    void saveRecipe(@NonNull Recipe recipe);

    void deleteAllRecipes();

    void deleteRecipe(int recipeId);

    //--------------------------------
    // Steps
    //--------------------------------

    MutableLiveData<List<Step>> getStepsForRecipe(int recipeId);

    void deleteAllSteps();

    void deleteStepsForRecipe(final int recipeId);

    void saveStep(Step step);

    //--------------------------------
    // Ingredients
    //--------------------------------

    MutableLiveData<List<Ingredient>> getIngredientsForRecipe(int recipeId);

    void deleteAllIngredients();

    void deleteIngredientsForRecipe(final int recipeId);

    void saveIngredient(Ingredient ingredient);

    //--------------------------------
    // Media requests
    //--------------------------------

    void loadImageIntoView(Context context, ImageView target, String imageUrl, Callback callback);
}
