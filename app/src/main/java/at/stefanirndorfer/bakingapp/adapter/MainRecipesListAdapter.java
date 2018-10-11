package at.stefanirndorfer.bakingapp.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.databinding.RecipeItemBinding;
import at.stefanirndorfer.bakingapp.view.input.RecipeItemUserActionListener;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;
import timber.log.Timber;

public class MainRecipesListAdapter extends BaseAdapter {

    private final Context mContext;
    private final MainViewModel mViewModel;
    private List<Recipe> mRecipes;

    public MainRecipesListAdapter(Context context, MainViewModel viewModel) {
        this.mContext = context;
        mViewModel = viewModel;
        mRecipes = new ArrayList<>();
        subscribeOnRecipeData();
    }

    /**
     * LiveData-subscription on the RecipeData in the ViewModel
     */
    private void subscribeOnRecipeData() {
        mViewModel.getRecipesLiveData().observe((LifecycleOwner) mContext, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null && !recipes.isEmpty()) {
                    Timber.d("received list of recipes from viewmodel. Length: " + recipes.size());
                    setRecipes(recipes);
                }
            }
        });
    }

    private void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mRecipes != null ? mRecipes.size() : 0;
    }

    @Override
    public Recipe getItem(int i) {
        return mRecipes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        RecipeItemBinding binding;
        if (view == null) {
            //Inflate
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            binding = RecipeItemBinding.inflate(inflater, viewGroup, false);
        } else {
            binding = DataBindingUtil.getBinding(view);
        }
        RecipeItemUserActionListener userActionListener = new RecipeItemUserActionListener() {
            @Override
            public void onRecipeClicked(Recipe recipe) {
                mViewModel.navigateToDetailScreen(recipe);
            }
        };

        binding.setRecipe(mRecipes.get(i));
        binding.setListener(userActionListener);
        binding.executePendingBindings();
        return binding.getRoot();
    }
}
