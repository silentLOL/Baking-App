package at.stefanirndorfer.bakingapp.data.source.remote;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.data.source.RecipesDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is where we use our Retrofit client
 */
public class RecipesNetworkDataSource implements RecipesDataSource {
    private static final String TAG = RecipesNetworkDataSource.class.getCanonicalName();

    private static RecipesNetworkDataSource instance;

    private RecipesNetworkDataSource() {
    }

    public static RecipesNetworkDataSource getInstance() {
        if (instance == null) {
            instance = new RecipesNetworkDataSource();
        }
        return instance;
    }


    @Override
    public MutableLiveData<List<Recipe>> getRecipes() {
        final MutableLiveData<List<Recipe>> returningData = new MutableLiveData<>();

        RequestRecipesService service = RetrofitClient.getRetrofitInstance().create(RequestRecipesService.class);
        Call<List<Recipe>> call = service.getRecipes();
        Log.d(TAG, call.request().toString());
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Log.d(TAG, "Received response");
                if (response.body() != null) {
                    List<Recipe> result = response.body();
                    if (!result.isEmpty()) {
                        returningData.setValue(result);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Error calling for recipes: " + t.getMessage());
                returningData.setValue(Collections.emptyList());
            }
        });
        return returningData;
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
    public void deleteRecipe(@NonNull String recipeId) {

    }

    @Override
    public MutableLiveData<List<Step>> getStepsForRecipe(@NonNull String recipeId) {
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
    public MutableLiveData<List<Ingredient>> getIngredientsForRecipe(@NonNull String recipeId) {
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
}
