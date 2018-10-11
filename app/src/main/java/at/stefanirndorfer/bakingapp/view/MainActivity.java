package at.stefanirndorfer.bakingapp.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.util.List;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.util.ActivityUtils;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setupViewFragment();
        mViewModel = obtainViewModel(this);
        doSubscriptions();
        mViewModel.start();

        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.recipe_detail_linear_layout) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;

            // Change the GridView to space out the images more on tablet
            GridView gridView = (GridView) findViewById(R.id.recipes_grid_view);
            gridView.setNumColumns(2);


            if (savedInstanceState == null) {
                // In two-pane mode, add initial BodyPartFragments to the screen
                FragmentManager fragmentManager = getSupportFragmentManager();

                // Creating a new head fragment
                RecipeDetailFragment headFragment = new BodyPartFragment();
                headFragment.setImageIds(AndroidImageAssets.getHeads());
                // Add the fragment to its container using a transaction
                fragmentManager.beginTransaction()
                        .add(R.id.head_container, headFragment)
                        .commit();
            }
        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;
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

    //    private void setupViewFragment() {
    //        MainFragment mainFragment =
    //                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainContentFrame);
    //        if (mainFragment == null) {
    //            // Create the fragment
    //            mainFragment = MainFragment.newInstance();
    //            ActivityUtils.replaceFragmentInActivity(
    //                    getSupportFragmentManager(), mainFragment, R.id.mainContentFrame);
    //        }
    //    }

    public static MainViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        MainViewModel viewModel = ViewModelProviders.of(activity, factory).get(MainViewModel.class);
        return viewModel;
    }
}
