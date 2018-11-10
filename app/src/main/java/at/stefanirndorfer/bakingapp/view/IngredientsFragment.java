package at.stefanirndorfer.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import at.stefanirndorfer.bakingapp.adapter.IngredientsRecyclerViewAdapter;
import at.stefanirndorfer.bakingapp.databinding.FragmentIngredientsBinding;
import at.stefanirndorfer.bakingapp.viewmodel.IngredientsViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;
import timber.log.Timber;

public class IngredientsFragment extends Fragment {

    private int mRecipeId;
    FragmentIngredientsBinding mFragmentBinding;
    private IngredientsViewModel mViewModel;
    private RecyclerView mRecyclerViewIngredients;
    private LinearLayoutManager mLinearLayoutManagerIngredients;
    private IngredientsRecyclerViewAdapter mIngredientsRecyclerViewAdapter;


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
        setupIngredientRecyclerViewAdapter();
    }

    private void setupIngredientRecyclerViewAdapter() {
        Timber.d( "Setting up TrailerRecyclerView");
        mRecyclerViewIngredients = mFragmentBinding.recyclerViewIngredientsRv;
        mLinearLayoutManagerIngredients = new LinearLayoutManager(this.getActivity());
        mRecyclerViewIngredients.setLayoutManager(mLinearLayoutManagerIngredients);
        mRecyclerViewIngredients.setHasFixedSize(true);
        mIngredientsRecyclerViewAdapter = new IngredientsRecyclerViewAdapter(mViewModel);
        mRecyclerViewIngredients.setAdapter(mIngredientsRecyclerViewAdapter);
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
