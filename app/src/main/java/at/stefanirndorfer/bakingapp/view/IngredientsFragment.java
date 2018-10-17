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
import at.stefanirndorfer.bakingapp.databinding.FragmentIngredientsBinding;
import at.stefanirndorfer.bakingapp.viewmodel.IngredientsFragmentViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;

public class IngredientsFragment extends Fragment {

    private IngredientsFragmentViewModel mViewModel;
    private int mRecipeId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        mViewModel = obtainViewModel(this.getActivity());

        // Inflate view and obtain an instance of the binding class.
        FragmentIngredientsBinding binding = DataBindingUtil.setContentView(getActivity(), R.layout.fragment_ingredients);
        // Assign the component to a property in the binding class.
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);


        setupAdapter(rootView);

        return rootView;
    }

    private void setupAdapter(View rootView) {
        // Get a reference to the ListView in the fragment_main_list xml layout file
        ListView listView = (ListView) rootView.findViewById(R.id.ingredients_list_view);

        // Create the adapter
        // This adapter takes in the context and a reference of the veiwModel
        IngredientsListAdapter adapter = new IngredientsListAdapter(getContext(), mViewModel);

        // Set the adapter on the GridView
        listView.setAdapter(adapter);
    }

    /**
     * called by the Activity when the Fragment is created
     *
     * @param recipeId
     */
    public void setmRecipeIdAndUpdateModel(int recipeId) {
        if (recipeId != recipeId) {
            this.mRecipeId = recipeId;
            mViewModel.start(recipeId);

        }
    }

    public static IngredientsFragmentViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        IngredientsFragmentViewModel viewModel = ViewModelProviders.of(activity, factory).get(IngredientsFragmentViewModel.class);
        return viewModel;
    }
}
