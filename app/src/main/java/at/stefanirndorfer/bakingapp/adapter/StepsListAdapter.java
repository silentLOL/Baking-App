package at.stefanirndorfer.bakingapp.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.databinding.StepItemBinding;
import at.stefanirndorfer.bakingapp.view.input.StepItemUserActionListener;
import at.stefanirndorfer.bakingapp.viewmodel.StepsViewModel;
import timber.log.Timber;

public class StepsListAdapter extends BaseAdapter {

    private final StepsViewModel mViewModel;
    private List<Step> mSteps;

    public StepsListAdapter(StepsViewModel mViewModel) {
        this.mViewModel = mViewModel;
        mSteps = new ArrayList<>();
        subscribeOnStepData();
    }

    /**
     * LiveData-subscription on the StepList in the ViewModel
     */
    private void subscribeOnStepData() {
        mViewModel.getSteps().observeForever(steps -> {
            if (steps != null && !steps.isEmpty()) {
                Timber.d("Received list of steps from viewmodel. Length: " + steps.size());
                setSteps(steps);
            }
        });
    }

    private void setSteps(List<Step> steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mSteps != null ? mSteps.size() : 0;
    }

    @Override
    public Step getItem(int position) {
        return mSteps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StepItemBinding binding;
        Timber.d("Binding view for item: " + mSteps.get(position).getShortDescription());
        if (convertView == null) {
            //Inflate
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = StepItemBinding.inflate(inflater, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        StepItemUserActionListener userActionListener = mViewModel::navigateToStepFragment;
        binding.setStep(mSteps.get(position));

        binding.stepShortDescriptionTv.setText(mSteps.get(position).getDescription());
        binding.setListener(userActionListener);
        binding.executePendingBindings();

        return binding.getRoot();
    }
}
