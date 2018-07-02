package com.zn.baking.widget;

import com.zn.baking.model.Recipe;

public interface WidgetConfigurationResultCallback {
    void passRecipeBackToActivity(Recipe recipe);
}
