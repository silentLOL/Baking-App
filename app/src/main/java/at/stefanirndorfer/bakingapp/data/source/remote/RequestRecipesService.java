package at.stefanirndorfer.bakingapp.data.source.remote;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestRecipesService {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();
}
