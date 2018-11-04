package at.stefanirndorfer.bakingapp;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import at.stefanirndorfer.bakingapp.data.Recipe;
import at.stefanirndorfer.bakingapp.view.DetailActivity;
import at.stefanirndorfer.bakingapp.view.remoteview.ListWidgetService;
import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidgetProvider extends AppWidgetProvider {


    static void updateAppWidget(Context context, Recipe recipe, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Timber.d("Updating AppWidget");
        // Construct the RemoteViews object

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        Intent onclickIntent = new Intent(context, DetailActivity.class);
        onclickIntent.putExtra(DetailActivity.RECIPE_ID_EXTRA, recipe.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, onclickIntent, 0);

        //views.setOnClickPendingIntent(R.id.widget_list_lv, pendingIntent);
        views.setTextViewText(R.id.widget_title_tv, recipe.getName());

        // list view
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_lv, intent);

        views.setEmptyView(R.id.widget_list_lv, R.id.empty_view);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateIngredientsWidget(Context context, AppWidgetManager appWidgetManager, Recipe recipe, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, recipe, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BakingAppWidgetService.startActionUpdateWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        BakingAppWidgetService.startActionUpdateWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

}

