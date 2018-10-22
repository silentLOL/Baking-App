package at.stefanirndorfer.bakingapp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.adapter.MainRecipesListAdapter;
import at.stefanirndorfer.bakingapp.view.input.RecipeItemUserActionListener;
import at.stefanirndorfer.bakingapp.view.input.StepItemUserActionListener;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;

public class MainListFragment extends Fragment {
    private MainViewModel mViewModel;
    private RecipeItemUserActionListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

        mViewModel = MainActivity.obtainViewModel(getActivity());

        // Get a reference to the ListView in the fragment_main_list xml layout file
        ListView listView = (ListView) rootView.findViewById(R.id.recipes_list_view);

        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        MainRecipesListAdapter adapter = new MainRecipesListAdapter(mListener, mViewModel);

        // Set the adapter on the GridView
        listView.setAdapter(adapter);

        return rootView;
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
