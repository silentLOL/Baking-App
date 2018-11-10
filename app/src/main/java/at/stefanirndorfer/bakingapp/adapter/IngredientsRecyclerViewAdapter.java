package at.stefanirndorfer.bakingapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.databinding.IngredientItemBinding;
import at.stefanirndorfer.bakingapp.viewmodel.IngredientsViewModel;
import timber.log.Timber;

public class IngredientsRecyclerViewAdapter extends RecyclerView.Adapter<IngredientsRecyclerViewAdapter.IngerdientViewHolder> {

    private final IngredientsViewModel mViewModel;
    private List<Ingredient> mIngredients;

    public IngredientsRecyclerViewAdapter(IngredientsViewModel mViewModel) {
        this.mViewModel = mViewModel;
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

    @NonNull
    @Override
    public IngerdientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        IngredientItemBinding binding;

        Timber.d("Binding view for item: " + mIngredients.get(i).getIngredientName());
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        binding = IngredientItemBinding.inflate(inflater, viewGroup, false);

        binding.ingredientNameTv.setText(mIngredients.get(i).getIngredientName());

        return new IngerdientViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngerdientViewHolder ingerdientViewHolder, int i) {
        Timber.d("OnBindViewHolder");
        Ingredient ingredient = mIngredients.get(i);
        ingerdientViewHolder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return mIngredients != null ? mIngredients.size() : 0;
    }


    public class IngerdientViewHolder extends RecyclerView.ViewHolder {

        private final IngredientItemBinding binding;

        public IngerdientViewHolder(@NonNull IngredientItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Ingredient ingredient) {
            Timber.d("Binding ingredient instance");
            binding.setIngredient(ingredient);
            binding.executePendingBindings();
        }
    }
}
