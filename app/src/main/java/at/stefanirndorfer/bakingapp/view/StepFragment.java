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

import java.util.Objects;

import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.databinding.FragmentStepBinding;
import at.stefanirndorfer.bakingapp.viewmodel.StepsViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;

public class StepFragment extends Fragment {

    private int mRecipeId;
    FragmentStepBinding mBinding;
    private StepsViewModel mViewModel;
    private Step mStep;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        mBinding = FragmentStepBinding.inflate(inflater, container, false);
        mViewModel = obtainViewModel(Objects.requireNonNull(this.getActivity()));
        mBinding.setViewModel(mViewModel);

        Bundle extras = getActivity().getIntent().getExtras();
        int recipeId = (int) extras.get(DetailActivity.RECIPE_ID_EXTRA);
        mViewModel.start(recipeId);
        mBinding.setStep(mStep);
        return mBinding.getRoot();
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

    public void setStep(Step step) {
        this.mStep = step;
    }
}
