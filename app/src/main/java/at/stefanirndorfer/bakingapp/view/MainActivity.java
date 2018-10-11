package at.stefanirndorfer.bakingapp.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.List;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;

    private boolean mIsTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setupViewFragment();
        mViewModel = obtainViewModel(this);
        doSubscriptions();
        mViewModel.start();

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.main_grid_fragment) != null) {

            mIsTablet = true;

            //if (savedInstanceState == null) {


//                // Creating a new grid fragment
//                MainGridFragment gridFragment = new MainGridFragment();
//                // Add the fragment to its container using a transaction
//                fragmentManager.beginTransaction()
//                        .add(R.id.head_container, headFragment)
//                        .commit();
            //}
        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
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
