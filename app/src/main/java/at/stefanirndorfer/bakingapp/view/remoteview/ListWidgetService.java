package at.stefanirndorfer.bakingapp.view.remoteview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Injection;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;
import timber.log.Timber;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private final RecipesRepository mRepository;
    private List<Ingredient> mIngredients;


    public ListRemoteViewsFactory(Context context) {
        mContext = context;
        mRepository = Injection.provideRecipesRepository(context.getApplicationContext());
        mIngredients = new ArrayList<>();
        Timber.d("ListRemoteViewsFactory created.");
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Timber.d("onDataSetChanged called");
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.last_viewed_recipe_id_file_name), Context.MODE_PRIVATE);
        int defaultValue = mContext.getResources().getInteger(R.integer.default_last_viewed_recipe_id);
        int recipeId = sharedPref.getInt(mContext.getString(R.string.last_viewed_recipe_id_key), defaultValue);
        Timber.d("recipeId from SharedPreferences: " + recipeId);
        mRepository.getIngredientsForRecipe(recipeId).observeForever(ingredients -> {
            if (ingredients == null || ingredients.isEmpty()) {
                Timber.d("List of ingredients updated on widget was empty or null");
            }
            Timber.d("List of ingredients updated on widget. Size: " + ingredients.size());
            mIngredients = ingredients;

        });
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mIngredients == null || mIngredients.isEmpty()) {
            return null;
        }
        Ingredient ingredient = mIngredients.get(position);
        //TODO: handle missing values
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget);
        views.setTextViewText(R.id.quantity_widget_tv, ingredient.getQuantity().toString());
        views.setTextViewText(R.id.measure_widget_tv, ingredient.getMeasure());
        views.setTextViewText(R.id.ingredient_name_widget_tv, ingredient.getIngredientName());
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}


