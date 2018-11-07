package at.stefanirndorfer.bakingapp;

import android.app.IntentService;
import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;

import at.stefanirndorfer.bakingapp.data.Injection;
import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.data.source.RecipesRepository;
import timber.log.Timber;

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

        // Android O workaround part 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Android O workaround part 2
        startForeground(1,new Notification());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_BAKING_APP_WIDGET.equals(action)) {
                updateBakingAppWidget();
            }
        }
    }

    private void updateBakingAppWidget() {
        Timber.d("Updateing Baking App Widget");
        Context applicationContext = getApplicationContext();
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.last_viewed_recipe_id_file_name), Context.MODE_PRIVATE);
        int defaultValue = applicationContext.getResources().getInteger(R.integer.default_last_viewed_recipe_id);
        int recipeId = sharedPref.getInt(applicationContext.getString(R.string.last_viewed_recipe_id_key), defaultValue);
        Timber.d("RecipeId from Shared Prefs: " + recipeId);

        RecipesRepository repository = Injection.provideRecipesRepository(applicationContext);

        repository.getRecipe(recipeId).observeForever(new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe == null) {
                    Timber.d("Received null object from repository!");
                } else {
                    Timber.d("Received recipe from repository!");
                    updateWidgetProvider(recipe);
                }
            }

            private void updateWidgetProvider(Recipe recipe) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(applicationContext);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), BakingAppWidgetProvider.class));
                // update list
                appWidgetManager.notifyAppWidgetViewDataChanged(recipeId, R.id.widget_list_lv);

                //Now update all widgets
                BakingAppWidgetProvider.updateIngredientsWidget(getApplicationContext(), appWidgetManager, recipe, appWidgetIds);
            }
        });
    }
}
