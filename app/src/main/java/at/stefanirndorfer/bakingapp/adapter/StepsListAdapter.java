package at.stefanirndorfer.bakingapp.adapter;

import android.app.Application;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.databinding.StepItemBinding;
import at.stefanirndorfer.bakingapp.view.input.StepItemUserActionListener;
import at.stefanirndorfer.bakingapp.viewmodel.StepsViewModel;
import timber.log.Timber;

public class StepsListAdapter extends BaseAdapter {

    private final StepsViewModel mViewModel;
    private final StepItemUserActionListener mListener;
    private final Application mContext;
    private List<Step> mSteps;

    public StepsListAdapter(Application context, StepItemUserActionListener listener, StepsViewModel mViewModel) {
        this.mContext = context;
        this.mListener = listener;
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
        StepItemUserActionListener userActionListener = mListener::onStepClicked;
        binding.setStep(mSteps.get(position));

        binding.stepShortDescriptionTv.setText(mSteps.get(position).getDescription());

        //fetch image resource if existing
        String imageUrl = mSteps.get(position).getThumbnailURL();
        if (!TextUtils.isEmpty(imageUrl)) {
            mViewModel.loadImageIntoView(binding.stepImageIv, imageUrl,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Timber.d("success loading image for step: " + mSteps.get(position).getShortDescription());
                            binding.stepImagePb.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Timber.e("Error loading image for step: " + mSteps.get(position).getShortDescription());
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
        binding.executePendingBindings();

        return binding.getRoot();
    }
}
