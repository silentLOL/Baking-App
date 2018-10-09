package at.stefanirndorfer.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.util.ActivityUtils;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewFragment();
        mViewModel = obtainViewModel(this);
    }

    private void setupViewFragment() {
        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainContentFrame);
        if (mainFragment == null) {
            // Create the fragment
            mainFragment = MainFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), mainFragment, R.id.mainContentFrame);
        }
    }

    public static MainViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        MainViewModel viewModel = ViewModelProviders.of(activity, factory).get(MainViewModel.class);
        return viewModel;
    }
}
