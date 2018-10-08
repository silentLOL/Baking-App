package at.stefanirndorfer.bakingapp.data;

import android.content.Context;
import android.support.annotation.NonNull;

import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;
import at.stefanirndorfer.bakingapp.data.source.local.RecipesDatabase;
import at.stefanirndorfer.bakingapp.data.source.local.RecipesLocalDataSource;
import at.stefanirndorfer.bakingapp.data.source.remote.RecipesNetworkDataSource;
import at.stefanirndorfer.bakingapp.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link at.stefanirndorfer.bakingapp.data.source.RecipesDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static RecipesRepository provideRecipesRepository(@NonNull Context context) {
        checkNotNull(context);
        RecipesDatabase database = RecipesDatabase.getInstance(context);
        return RecipesRepository.getInstance(RecipesNetworkDataSource.getInstance(),
                RecipesLocalDataSource.getInstance(new AppExecutors(),
                        database.recipesDao(), database.stepsDao(), database.ingredientsDao()));
    }
}
