package at.stefanirndorfer.bakingapp.viewmodel;


import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.VisibleForTesting;

import at.stefanirndorfer.bakingapp.data.Injection;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;

/**
 * A creator is used to inject the product ID into the ViewModel
 * <p>
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;

    private final RecipesRepository mRecipesRepository;

    public static ViewModelFactory getInstance(Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application,
                            Injection.provideRecipesRepository(application.getApplicationContext()));
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private ViewModelFactory(Application application, RecipesRepository repository) {
        mApplication = application;
        mRecipesRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            //noinspection unchecked
            return (T) new MainActivityViewModel(mApplication, mRecipesRepository);
        }
//        } else if (modelClass.isAssignableFrom(TaskDetailViewModel.class)) {
//            //noinspection unchecked
//            return (T) new TaskDetailViewModel(mApplication, mRecipesRepository);
//        } else if (modelClass.isAssignableFrom(AddEditTaskViewModel.class)) {
//            //noinspection unchecked
//            return (T) new AddEditTaskViewModel(mApplication, mRecipesRepository);
//        } else if (modelClass.isAssignableFrom(TasksViewModel.class)) {
//            //noinspection unchecked
//            return (T) new TasksViewModel(mApplication, mRecipesRepository);
//        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

}