package at.stefanirndorfer.bakingapp.data.source.local;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.data.source.RecipesDataSource;
import at.stefanirndorfer.bakingapp.util.AppExecutors;
import timber.log.Timber;


/**
 * Concrete implementation of a data source as a db.
 */
public class RecipesLocalDataSource implements RecipesDataSource {

    private static volatile RecipesLocalDataSource INSTANCE;

    private RecipesDao mRecipesDao;
    private StepsDao mStepsDao;
    private IngredientsDao mIngredientsDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private RecipesLocalDataSource(@NonNull AppExecutors appExecutors,
                                   @NonNull RecipesDao recipesDao,
                                   @NonNull StepsDao stepsDao,
                                   @NonNull IngredientsDao ingredientsDao) {
        mAppExecutors = appExecutors;
        mRecipesDao = recipesDao;
        mStepsDao = stepsDao;
        mIngredientsDao = ingredientsDao;
    }

    public static RecipesLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                     @NonNull RecipesDao recipesDao,
                                                     @NonNull StepsDao stepsDao,
                                                     @NonNull IngredientsDao ingredientsDao) {
        if (INSTANCE == null) {
            synchronized (RecipesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecipesLocalDataSource(appExecutors, recipesDao, stepsDao, ingredientsDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public MutableLiveData<List<Recipe>> getRecipes() {
        MutableLiveData<List<Recipe>> returningValue = new MutableLiveData<>();
        Runnable runnable = () -> returningValue.postValue(mRecipesDao.getRecipes());
        mAppExecutors.diskIO().execute(runnable);
        return returningValue;
    }

    @Override
    public MutableLiveData<Recipe> getRecipe(int recipeId) {
        MutableLiveData<Recipe> returningValue = new MutableLiveData<>();
        Runnable runnable = () -> returningValue.postValue(mRecipesDao.getRecipesById(recipeId));
        mAppExecutors.diskIO().execute(runnable);
        return returningValue;
    }

    @Override
    public void saveRecipe(@NonNull Recipe recipe) {
        Runnable runnable = () -> {
            Timber.d("inserting recipe: " + recipe.getName() + " into db.");
            mRecipesDao.insertRecipe(recipe);
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllRecipes() {
        Runnable runnable = () -> {
            Timber.d("Deleting all recipes from DB.");
            mRecipesDao.deleteAllRecipes();
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteRecipe(int recipeId) {

    }

    @Override
    public MutableLiveData<List<Step>> getStepsForRecipe(int recipeId) {
        MutableLiveData<List<Step>> steps = new MutableLiveData<>();
        Runnable runnable = () -> {
            List<Step> stepsForRecipe = mStepsDao.getStepsForRecipe(recipeId);
            Timber.d("Received " + stepsForRecipe.size() + " steps from Database");
            steps.postValue(stepsForRecipe);
        };
        mAppExecutors.diskIO().execute(runnable);
        return steps;
    }


    @Override
    public void deleteAllSteps() {

    }

    @Override
    public void deleteStepsForRecipe(int recipeId) {

    }

    @Override
    public void saveStep(@NonNull Step step) {
        Runnable runnable = () -> {
            Timber.d("inserting step: " + step.getShortDescription() + " for recipe id: " + step.getRecipeId() + " into db.");
            mStepsDao.insertStep(step);
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public MutableLiveData<List<Ingredient>> getIngredientsForRecipe(int recipeId) {
        MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();
        Runnable runnable = () -> {
            List<Ingredient> ingredientsForRecipe = mIngredientsDao.getIngredientsForRecipe(recipeId);
            Timber.d("Received " + ingredientsForRecipe.size() + " ingredients from Database");
            ingredients.postValue(ingredientsForRecipe);
        };
        mAppExecutors.diskIO().execute(runnable);
        return ingredients;
    }


    @Override
    public void deleteAllIngredients() {

    }

    @Override
    public void deleteIngredientsForRecipe(int recipeId) {

    }

    @Override
    public void saveIngredient(Ingredient ingredient) {
        Runnable runnable = () -> {
            Timber.d("inserting ingredient: " + ingredient.getIngredientName() + " for recipe id: " + ingredient.getRecipeId() + " into db.");
            mIngredientsDao.insertIngredient(ingredient);
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void loadImageForRecipe(Context context, ImageView target, String imageUrl, Callback callback) {
        // noting to do locally
    }
}
