package com.zn.baking.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zn.baking.R;
import com.zn.baking.RecipeListFragment;
import com.zn.baking.model.Recipe;

public class IngredientsWidgetConfigurationActivity extends AppCompatActivity
        implements WidgetConfigurationResultCallback {
    // 1. Must return a result, including the app widget id passed by the intent the launched the activity
    // 2. Request an update from AppWidgetManager when App Widget is first created. onUpdate() will
    // NOT be called for the first time, but it will be called for subsequent updates

    public static final String LOAD_FRAGMENT_FROM_CONFIG
            = "com.zn.baking.load_fragment_from_config";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set current result to CANCELED, so when user presses back button, widget host can cancel
        // out of widget placement
        setResult(RESULT_CANCELED);

        // set view layout resource to use
        setContentView(R.layout.widget_configure);

        // get the widget id from the intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // Quit if appWidgetId is invalid
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // set up the recipe list and display it
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LOAD_FRAGMENT_FROM_CONFIG, true);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_config_container, fragment)
                .commit();
    }

    @Override
    public void passRecipeBackToActivity(Recipe recipe) {
        // Update the app widget with the recipe
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        IngredientsWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId, recipe);

        // Pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
