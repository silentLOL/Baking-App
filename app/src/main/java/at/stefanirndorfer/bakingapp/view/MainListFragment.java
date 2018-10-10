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
import android.widget.GridView;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.adapter.MainRecipesListAdapter;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;

public class MainListFragment extends Fragment {
    private MainViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

        mViewModel = MainActivity.obtainViewModel(getActivity());

        // Get a reference to the GridView in the fragment_main_list xml layout file
        GridView gridView = (GridView) rootView.findViewById(R.id.recipes_grid_view);

        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        MainRecipesListAdapter adapter = new MainRecipesListAdapter(getContext(), mViewModel);

        // Set the adapter on the GridView
        gridView.setAdapter(adapter);


        return rootView;
    }

}
