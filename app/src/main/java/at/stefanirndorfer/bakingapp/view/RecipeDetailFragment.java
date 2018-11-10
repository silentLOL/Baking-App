package at.stefanirndorfer.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.adapter.StepsRecyclerViewAdapter;
import at.stefanirndorfer.bakingapp.databinding.FragmentRecipeDetailBinding;
import at.stefanirndorfer.bakingapp.view.input.StepItemUserActionListener;
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
    private StepItemUserActionListener mListener;
    private RecyclerView mRecyclerViewSteps;
    private LinearLayoutManager mLinearLayoutManagerSteps;
    private StepsRecyclerViewAdapter mStepsRecyclerViewAdapter;

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

        // hide ingredients button on tablets
        if (getResources().getBoolean(R.bool.isTablet)){
            mFragmentBinding.ingredientsBt.setVisibility(View.GONE);
        }

        return mFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupStepRecyclerViewAdapter();
    }

    private void setupStepRecyclerViewAdapter() {
        Timber.d( "Setting up RecyclerView");
        mRecyclerViewSteps = mFragmentBinding.recyclerViewStepsRv;
        mLinearLayoutManagerSteps = new LinearLayoutManager(this.getActivity());
        mRecyclerViewSteps.setLayoutManager(mLinearLayoutManagerSteps);
        mRecyclerViewSteps.setHasFixedSize(true);
        mStepsRecyclerViewAdapter = new StepsRecyclerViewAdapter(mViewModel, mListener, this.getActivity().getApplication());
        mRecyclerViewSteps.setAdapter(mStepsRecyclerViewAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (StepItemUserActionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement StepItemUserActionListener");
        }
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
