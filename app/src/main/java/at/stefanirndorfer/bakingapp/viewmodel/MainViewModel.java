package at.stefanirndorfer.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesDataSource;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;

public class MainViewModel extends AndroidViewModel {

    private final Context mContext; // To avoid leaks, this must be an Application Context.
    private final RecipesRepository mRecipeRepository;

    private MutableLiveData<List<Recipe>> mRecipesLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application context, RecipesRepository repository) {
        super(context);
        this.mContext = context.getApplicationContext();
        mRecipeRepository = repository;
    }

    public void start() {
        loadRecipes();
    }

    /**
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadRecipes( boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        mRecipesLiveData = mRecipeRepository.getRecipes();
        mRecipeRepository.getRecipes(new RecipesDataSource().LoadTasksCallback() {
            @Override
            public void onTasksLoaded (List < Task > tasks) {
                List<Task> tasksToShow = new ArrayList<>();

                // We filter the tasks based on the requestType
                for (Task task : tasks) {
                    switch (mCurrentFiltering) {
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive()) {
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLETED_TASKS:
                            if (task.isCompleted()) {
                                tasksToShow.add(task);
                            }
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(tasksToShow);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable () {
                mIsDataLoadingError.set(true);
            }
        });
    }
}
