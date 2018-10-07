package at.stefanirndorfer.bakingapp.data.source.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import at.stefanirndorfer.bakingapp.data.Step;

@Dao
public interface StepsDao {

    @Query("SELECT * FROM step")
    LiveData<List<Step>> getSteps();

    @Query("SELECT * FROM step WHERE recipe_id=:recipeId")
    LiveData<List<Step>> getStepsForRecipe(final int recipeId);

    @Query("DELETE FROM step")
    void deleteAllStep();

    @Query("DELETE FROM step WHERE recipe_id=:recipeId")
    void deleteStepsForRecipe(final int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void instertStep(Step step);
}
