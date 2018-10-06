package at.stefanirndorfer.bakingapp.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import at.stefanirndorfer.bakingapp.data.Recipe;

/**
 * The Room Database that contains the Recipe table.
 */
@Database(entities = {Recipe.class}, version = 1)
public abstract class RecipesDatabase extends RoomDatabase {
    private static RecipesDatabase INSTANCE;

    public abstract RecipesDao recipesDao();

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
