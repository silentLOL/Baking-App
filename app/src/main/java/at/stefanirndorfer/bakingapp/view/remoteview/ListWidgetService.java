package at.stefanirndorfer.bakingapp.view.remoteview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Ingredient;
import at.stefanirndorfer.bakingapp.data.Injection;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;
import at.stefanirndorfer.bakingapp.view.DetailActivity;
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
    private int mCurrentrecipeId;


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
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int defaultValue = mContext.getResources().getInteger(R.integer.default_last_viewed_recipe_id);
        mCurrentrecipeId = sharedPref.getInt(mContext.getString(R.string.last_viewed_recipe_id_key), defaultValue);
        Timber.d("recipeId from SharedPreferences: " + mCurrentrecipeId);
        mRepository.getIngredientsForRecipe(mCurrentrecipeId).observeForever(ingredients -> {
            if (ingredients == null || ingredients.isEmpty()) {
                Timber.d("List of ingredients updated on widget was empty or null");
            }
            Timber.d("List of ingredients updated on widget. Size: " + ingredients.size());
            mIngredients = ingredients;
        });

        // This is ugly. But the async live data response and the end of this method
        // have a race condition.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        RecipesRepository.destroyInstance();
    }

    @Override
    public int getCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mIngredients == null || mIngredients.isEmpty()) {
            Timber.d("Ingredients is Empty or null!");
            return null;
        }
        Ingredient ingredient = mIngredients.get(position);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget);
        views.setTextViewText(R.id.quantity_widget_tv, ingredient.getQuantity().toString());
        views.setTextViewText(R.id.measure_widget_tv, ingredient.getMeasure()); //TODO: create Enumeration
        views.setTextViewText(R.id.ingredient_name_widget_tv, ingredient.getIngredientName());


        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putInt(DetailActivity.RECIPE_ID_EXTRA, mCurrentrecipeId);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.ingredients_list_item_ll, fillInIntent);

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


