package at.stefanirndorfer.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Objects;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.adapter.StepsListAdapter;
import at.stefanirndorfer.bakingapp.databinding.FragmentRecipeDetailBinding;
import at.stefanirndorfer.bakingapp.viewmodel.StepsViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;
import timber.log.Timber;

/**
 * provides a button to view the ingredients and a list of the steps below
 */
public class RecipeDetailFragment extends Fragment {


    private int mRecipeId;
    FragmentRecipeDetailBinding mFragmentBinding;
    private StepsViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentBinding = FragmentRecipeDetailBinding.inflate(inflater, container, false);
        // createViewModel
        mViewModel = obtainViewModel(Objects.requireNonNull(this.getActivity()));
        mFragmentBinding.setViewModel(mViewModel);

        Bundle extras = getActivity().getIntent().getExtras();
        int recipeId = (int) extras.get(DetailActivity.RECIPE_ID_EXTRA);
        mViewModel.start(recipeId);

        return mFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupAdapter(mFragmentBinding.getRoot());
    }

    private void setupAdapter(View rootView) {
        Timber.d("Setting up StepsListAdapter");
        // Get a reference to the ListView in the respective xml layout file
        ListView listView = (ListView) rootView.findViewById(R.id.steps_list_view);

        // Create the adapter
        // This adapter takes in the context and a reference of the viewModel
        StepsListAdapter adapter = new StepsListAdapter(mViewModel);

        // Set the adapter on the ListView
        listView.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.getSteps().removeObservers(this);
    }

    public static StepsViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        StepsViewModel viewModel = ViewModelProviders.of(activity, factory).get(StepsViewModel.class);
        return viewModel;
    }
}
