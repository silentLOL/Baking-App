package at.stefanirndorfer.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.adapter.IngredientsListAdapter;
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
    private StepsViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // createViewModel
        mViewModel = obtainViewModel(this.getActivity());

        // Inflate view and obtain an instance of the binding class.
        FragmentRecipeDetailBinding binding = DataBindingUtil.setContentView(getActivity(), R.layout.fragment_recipe_detail);
        // Assign the component to a property in the binding class.
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        setupAdapter(rootView);

        return rootView;
    }

    private void setupAdapter(View rootView) {
        // Get a reference to the ListView in the fragment_main_list xml layout file
        ListView listView = (ListView) rootView.findViewById(R.id.steps_list_view);

        // Create the adapter
        // This adapter takes in the context and a reference of the veiwModel
        StepsListAdapter adapter = new StepsListAdapter(getContext(), mViewModel);

        // Set the adapter on the GridView
        listView.setAdapter(adapter);
    }

    /**
     * called by the Activity when the Fragment is created
     *
     * @param recipeId
     */
    public void setRecipeIdAndUpdateModel(int recipeId) {
        if (recipeId != recipeId) {
            Timber.d("Setting recipe Id to : " + recipeId);
            this.mRecipeId = recipeId;
            mViewModel.start(recipeId);

        }
    }

    public static StepsViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        StepsViewModel viewModel = ViewModelProviders.of(activity, factory).get(StepsViewModel.class);
        return viewModel;
    }
}
