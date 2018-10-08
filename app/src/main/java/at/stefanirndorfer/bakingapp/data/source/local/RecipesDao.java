package at.stefanirndorfer.bakingapp.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;

@Dao
public interface RecipesDao {

    @Query("SELECT * FROM recipe")
    List<Recipe> getRecipes();

    @Query("SELECT * FROM recipe WHERE recipe_id = :id")
    Recipe getRecipesById(final int id);

    @Query("DELETE FROM recipe")
    void deleteAllRecipes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);
}
