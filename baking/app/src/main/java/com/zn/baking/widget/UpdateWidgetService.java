package com.zn.baking.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.zn.baking.model.Recipe;

public class UpdateWidgetService extends IntentService {

    public static final String ACTION_POPULATE_INGREDIENT_WIDGET =
            "com.zn.baking.widget.action_populate_ingredient_widget";

    public UpdateWidgetService(String name) {
        super(name);
    }

    public UpdateWidgetService() {
        super("UpdateWidgetService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_POPULATE_INGREDIENT_WIDGET:
                    handleActionPopulateIngredientsWidget();
                    break;
            }
        }
    }

    /**
     * Helper method for starting up the update ingredients widget service directly
     *
     * @param context
     */
    public static void startActionPopulateIngredientsWidget(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        intent.setAction(ACTION_POPULATE_INGREDIENT_WIDGET);
        context.startService(intent);
    }

    /**
     * Updates the information provided by the ingredients widget
     */
    private void handleActionPopulateIngredientsWidget() {
        // update all widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, IngredientsWidget.class));

        IngredientsWidget.updateAppWidgets(this, appWidgetManager, appWidgetIds);
    }
}
