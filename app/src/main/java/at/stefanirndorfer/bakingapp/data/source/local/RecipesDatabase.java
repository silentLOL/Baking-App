package at.stefanirndorfer.bakingapp.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.Step;

/**
 * The Room Database that contains the Recipe table.
 */
@Database(entities = {Recipe.class, Step.class, Ingredient.class}, version = 1, exportSchema = false)
@TypeConverters(DataConverter.class)
public abstract class RecipesDatabase extends RoomDatabase {
    private static RecipesDatabase INSTANCE;

    public abstract RecipesDao recipesDao();
    public abstract StepsDao stepsDao();
    public abstract IngredientsDao ingredientsDao();

    private static final Object sLock = new Object();

    public static RecipesDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        RecipesDatabase.class,
                        "Recipes.db")
                        .build();
            }
            return INSTANCE;
        }
    }
}
