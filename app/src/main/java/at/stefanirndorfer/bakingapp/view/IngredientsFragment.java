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
import at.stefanirndorfer.bakingapp.adapter.IngredientsListAdapter;
import at.stefanirndorfer.bakingapp.databinding.FragmentIngredientsBinding;
import at.stefanirndorfer.bakingapp.view.input.FragmentNavigationListener;
import at.stefanirndorfer.bakingapp.viewmodel.IngredientsViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;
import timber.log.Timber;

public class IngredientsFragment extends Fragment {

    private int mRecipeId;
    FragmentIngredientsBinding mFragmentBinding;
    private IngredientsViewModel mViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentBinding = FragmentIngredientsBinding.inflate(inflater, container, false);

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
        Timber.d("Setting up IngredientsListAdapter");
        // Get a reference to the ListView in the xml respective layout file
        ListView listView = (ListView) rootView.findViewById(R.id.ingredients_list_view);

        // Create the adapter
        // This adapter takes in the context and a reference of the veiwModel
        IngredientsListAdapter adapter = new IngredientsListAdapter(mViewModel);

        // Set the adapter on the ListView
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.getIngredients().removeObservers(this);
    }


    public static IngredientsViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        IngredientsViewModel viewModel = ViewModelProviders.of(activity, factory).get(IngredientsViewModel.class);
        return viewModel;
    }

}
