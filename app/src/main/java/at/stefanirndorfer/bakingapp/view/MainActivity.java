package at.stefanirndorfer.bakingapp.view;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import at.stefanirndorfer.bakingapp.BakingAppWidgetProvider;
import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.view.input.RecipeItemUserActionListener;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeItemUserActionListener {

    private MainViewModel mViewModel;

    private boolean mIsTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = obtainViewModel(this);
        doSubscriptions();
        mViewModel.start();

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.main_grid_fragment) != null) {
            mIsTablet = true;
        } else {
            mIsTablet = false;
        }

    }

    private void doSubscriptions() {
        subscribeOnRecipes();
    }

    private void subscribeOnRecipes() {
        Timber.d("Subscribing on Recipe data.");
        final Observer<List<Recipe>> recipesObserver = recipes -> {
            if (recipes != null && !recipes.isEmpty()) {
                StringBuilder sb = new StringBuilder("Received the following recipies via live data: \n");
                for (Recipe currElement : recipes) {
                    sb.append(currElement.toString());
                    sb.append("\n");
                }
                Timber.d(sb.toString());
            } else {
                Timber.d("No recipes received from view model");
            }
        };
        mViewModel.getRecipesLiveData().observe(this, recipesObserver);
    }

    public static MainViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        MainViewModel viewModel = ViewModelProviders.of(activity, factory).get(MainViewModel.class);
        return viewModel;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
        mViewModel.getRecipesLiveData().removeObservers(this);
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        navigateToDetailScreen(recipe);
    }

    /**
     * Navigates to the DetailActivity
     * and puts the recipes id as extra
     *
     * @param recipe
     */
    public void navigateToDetailScreen(Recipe recipe) {
        updateSharedPreferences(recipe.getId());
        triggerWidgetUpdate(recipe);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.RECIPE_ID_EXTRA, recipe.getId());
        intent.putExtra(DetailActivity.RECIPE_NAME_EXTRA, recipe.getName());
        startActivity(intent);
    }

    /**
     * triggers a data update on the widget
     *
     * @param recipe
     */
    private void triggerWidgetUpdate(Recipe recipe) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingAppWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_lv);
        //Now update all widgets
        BakingAppWidgetProvider.updateIngredientsWidget(this, appWidgetManager, recipe, appWidgetIds);
    }


    /**
     * stores the id of the recipe latest chosen by the user to the shared prefs
     *
     * @param id
     */
    private void updateSharedPreferences(Integer id) {
        Timber.d("Storing last seen recipe id to Shared Preferences: " + id);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.last_viewed_recipe_id_key), id);
        editor.apply();
    }
}
