package at.stefanirndorfer.bakingapp.adapter;

import android.app.Application;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.databinding.StepItemBinding;
import at.stefanirndorfer.bakingapp.view.input.StepItemUserActionListener;
import at.stefanirndorfer.bakingapp.viewmodel.StepsViewModel;
import timber.log.Timber;

public class StepsRecyclerViewAdapter extends RecyclerView.Adapter<StepsRecyclerViewAdapter.StepViewHolder> {

    private final StepsViewModel mViewModel;
    private final StepItemUserActionListener mListener;
    private final Application mContext;
    private List<Step> mSteps;

    public StepsRecyclerViewAdapter(StepsViewModel mViewModel, StepItemUserActionListener mListener, Application mContext) {
        this.mViewModel = mViewModel;
        this.mListener = mListener;
        this.mContext = mContext;
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

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        StepItemBinding binding;
        Timber.d("onCreateViewHolder for item: " + mSteps.get(i).getShortDescription());
        //Inflate
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        binding = StepItemBinding.inflate(inflater, viewGroup, false);

        StepItemUserActionListener userActionListener = mListener::onStepClicked;
        //binding.setStep(mSteps.get(i)); /* this is done in the viewholder */

        binding.stepShortDescriptionTv.setText(mSteps.get(i).getDescription());

        //fetch image resource if existing
        String imageUrl = mSteps.get(i).getThumbnailURL();
        if (!TextUtils.isEmpty(imageUrl)) {
            mViewModel.loadImageIntoView(binding.stepImageIv, imageUrl,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Timber.d("success loading image for step: " + mSteps.get(i).getShortDescription());
                            binding.stepImagePb.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Timber.e("Error loading image for step: " + mSteps.get(i).getShortDescription());
                            binding.stepImageIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toertchen));
                            binding.stepImageIv.setVisibility(View.VISIBLE);
                            binding.stepImagePb.setVisibility(View.GONE);
                        }
                    }
            );
            binding.stepImageIv.setVisibility(View.GONE);
            binding.stepImagePb.setVisibility(View.VISIBLE);
        } else {
            binding.stepImageIv.setVisibility(View.VISIBLE);
            binding.stepImagePb.setVisibility(View.GONE);
        }

        binding.setListener(userActionListener);

        return new StepViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder stepViewHolder, int i) {
        Step step = mSteps.get(i);
        stepViewHolder.bind(step);
    }

    @Override
    public int getItemCount() {
        return mSteps != null ? mSteps.size() : 0;
    }

    public class StepViewHolder extends RecyclerView.ViewHolder {

        private final StepItemBinding binding;

        public StepViewHolder(@NonNull StepItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Step step) {
            binding.setStep(step);
            binding.executePendingBindings();
        }
    }
}
