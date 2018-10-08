package at.stefanirndorfer.bakingapp.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;

@Dao
public interface IngredientsDao {

    @Query("SELECT * FROM ingredient")
    List<Ingredient> getIngredients();

    @Query("SELECT * FROM ingredient WHERE recipe_id=:recipeId")
    List<Ingredient> getIngredientsForRecipe(final int recipeId);

    @Query("DELETE FROM ingredient")
    void deleteAllIngredients();

    @Query("DELETE FROM ingredient WHERE recipe_id=:recipeId")
    void deleteIngredientsforRecipe(final int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredient(Ingredient ingredient);
}
