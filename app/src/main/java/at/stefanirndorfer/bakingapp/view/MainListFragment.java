package at.stefanirndorfer.bakingapp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.stefanirndorfer.bakingapp.adapter.MainRecipesRecyclerViewAdapter;
import at.stefanirndorfer.bakingapp.databinding.FragmentMainGridBinding;
import at.stefanirndorfer.bakingapp.databinding.FragmentMainListBinding;
import at.stefanirndorfer.bakingapp.view.input.RecipeItemUserActionListener;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;
import timber.log.Timber;

public class MainListFragment extends Fragment {
    FragmentMainListBinding mFragmentBinding;
    private MainViewModel mViewModel;
    private RecipeItemUserActionListener mListener;
    private MainRecipesRecyclerViewAdapter mRecipeRecyclerViewAdapter;
    private RecyclerView mRecyclerViewRecipes;
    private LinearLayoutManager mGridLayoutManagerRecipes;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentBinding = FragmentMainListBinding.inflate(inflater, container, false);
        mViewModel = MainActivity.obtainViewModel(getActivity());
        mFragmentBinding.setViewModel(mViewModel);
        return mFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpRecipesRecyclerViewAdapter();
    }


    private void setUpRecipesRecyclerViewAdapter() {
        Timber.d("Setting up RecyclerView");
        mRecyclerViewRecipes = mFragmentBinding.recyclerViewRecipesListRv;
        mGridLayoutManagerRecipes = new LinearLayoutManager(this.getActivity());
        mRecyclerViewRecipes.setLayoutManager(mGridLayoutManagerRecipes);
        mRecyclerViewRecipes.setHasFixedSize(true);
        mRecipeRecyclerViewAdapter = new MainRecipesRecyclerViewAdapter(mListener, mViewModel);
        mRecyclerViewRecipes.setAdapter(mRecipeRecyclerViewAdapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (RecipeItemUserActionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RecipeItemUserActionListener");
        }

    }


}
