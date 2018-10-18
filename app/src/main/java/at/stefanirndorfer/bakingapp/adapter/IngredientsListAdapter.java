package at.stefanirndorfer.bakingapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.viewmodel.IngredientsViewModel;

public class IngredientsListAdapter extends BaseAdapter {

    private Context context;
    private final IngredientsViewModel mViewModel;
    private List<Ingredient> mIngredients;

    public IngredientsListAdapter(Context context, IngredientsViewModel viewModel) {
        this.context = context;
        this.mViewModel = viewModel;
        mIngredients = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
