package at.stefanirndorfer.bakingapp.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;

@Dao
public interface RecipesDao {

    @Query("SELECT * FROM Recipes")
    LiveData<List<Recipe>> getRecipes();

    @Query("DELETE * FROM Recipes")
    void deleteAllRecipes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);
}
