package at.stefanirndorfer.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import at.stefanirndorfer.bakingapp.data.Injection;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;

public class BakingAppWidgetService extends IntentService {

    private static final String ACTION_UPDATE_BAKING_APP_WIDGET = "at.stefanirndorfer.bakingapp.action.update_baking_app_widget";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BakingAppWidgetService() {
        super("BakingAppWidgetService");
    }

    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, BakingAppWidgetService.class);
        intent.setAction(ACTION_UPDATE_BAKING_APP_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_BAKING_APP_WIDGET.equals(action)) {
                updateBakingAppWidget(1); // TODO: Fix that magic number
            }
        }
    }

    private void updateBakingAppWidget(int recipeId) {
        RecipesRepository repository = Injection.provideRecipesRepository(getApplicationContext());
        repository.getDetailedRecipe(recipeId).observeForever(new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                updateWidgetProvider(recipe);
            }

            private void updateWidgetProvider(Recipe recipe) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), BakingAppWidgetProvider.class));
                //Now update all widgets
                BakingAppWidgetProvider.updateIngredientsWidget(getApplicationContext(), appWidgetManager, recipe, appWidgetIds);
            }
        });
    }
}
