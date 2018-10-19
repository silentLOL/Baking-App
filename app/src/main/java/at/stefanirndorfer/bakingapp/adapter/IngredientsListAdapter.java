package at.stefanirndorfer.bakingapp.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.databinding.IngredientItemBinding;
import at.stefanirndorfer.bakingapp.viewmodel.IngredientsViewModel;
import timber.log.Timber;

public class IngredientsListAdapter extends BaseAdapter {

    private final IngredientsViewModel mViewModel;
    private List<Ingredient> mIngredients;

    public IngredientsListAdapter(IngredientsViewModel viewModel) {
        this.mViewModel = viewModel;
        mIngredients = new ArrayList<>();
        subscribeOnIngredientsData();
    }

    private void subscribeOnIngredientsData() {
        mViewModel.getIngredients().observeForever(ingredients -> {
            if (ingredients != null && !ingredients.isEmpty()) {
                Timber.d("Received list of ingredients from viewmodel. Length: " + ingredients.size());
                setIngredients(ingredients);
            }
        });
    }

    private void setIngredients(List<Ingredient> ingredients) {
        this.mIngredients = ingredients;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mIngredients != null ? mIngredients.size() : 0;
    }

    @Override
    public Ingredient getItem(int position) {
        return mIngredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IngredientItemBinding binding;
        Timber.d("Binding view for item: " + mIngredients.get(position).getIngredientName());
        if (convertView == null) {
            //Inflate
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = IngredientItemBinding.inflate(inflater, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setIngredient(mIngredients.get(position));
        binding.ingredientNameTv.setText(mIngredients.get(position).getIngredientName());
        binding.executePendingBindings();
        return binding.getRoot();
    }
}
