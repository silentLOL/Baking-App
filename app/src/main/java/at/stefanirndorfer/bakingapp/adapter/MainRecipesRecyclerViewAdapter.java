package at.stefanirndorfer.bakingapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.databinding.RecipeItemBinding;
import at.stefanirndorfer.bakingapp.view.input.RecipeItemUserActionListener;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;
import timber.log.Timber;

public class MainRecipesRecyclerViewAdapter extends RecyclerView.Adapter<MainRecipesRecyclerViewAdapter.RecipeViewHolder> {

    private final RecipeItemUserActionListener mListener;
    private final MainViewModel mViewModel;
    private List<Recipe> mRecipes;

    public MainRecipesRecyclerViewAdapter(RecipeItemUserActionListener mListener, MainViewModel mViewModel) {
        this.mListener = mListener;
        this.mViewModel = mViewModel;
        mRecipes = new ArrayList<>();
        subscribeOnRecipeData();
    }

    /**
     * LiveData-subscription on the RecipeData in the ViewModel
     */
    private void subscribeOnRecipeData() {
        mViewModel.getRecipesLiveData().observeForever(recipes -> {
            if (recipes != null && !recipes.isEmpty()) {
                Timber.d("received list of recipes from viewmodel. Length: " + recipes.size());
                setRecipes(recipes);
            }
        });
    }

    private void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Timber.d("Binding view for item: " + mRecipes.get(i).getName());
        RecipeItemBinding binding;

        //Inflate
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        binding = RecipeItemBinding.inflate(inflater, viewGroup, false);

        RecipeItemUserActionListener userActionListener = recipe -> mListener.onRecipeClicked(recipe);

        //fetch image resource if existing
        String imageUrl = mRecipes.get(i).getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            mViewModel.loadRecipeImage(binding.recipeImageIv, imageUrl,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            Timber.d("success loading image for recipe: " + mRecipes.get(i).getName());
                            binding.recipeImageIv.setVisibility(View.VISIBLE);
                            binding.recipePlaceholderIv.setVisibility(View.GONE);
                            binding.recipeImagePb.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Timber.e("Error loading image for recipe: " + mRecipes.get(i).getName());
                            binding.recipeImageIv.setVisibility(View.GONE);
                            binding.recipePlaceholderIv.setVisibility(View.VISIBLE);
                            binding.recipeImagePb.setVisibility(View.GONE);
                        }
                    }
            );
            binding.recipeImagePb.setVisibility(View.VISIBLE);
        }
        binding.setListener(userActionListener);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i) {
        Recipe recipe = mRecipes.get(i);
        recipeViewHolder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return mRecipes != null ? mRecipes.size() : 0;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final RecipeItemBinding binding;

        public RecipeViewHolder(@NonNull RecipeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Recipe recipe) {
            binding.setRecipe(recipe);
            binding.executePendingBindings();
        }
    }

}
