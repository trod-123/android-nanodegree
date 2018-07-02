package com.zn.baking.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private double quantity = JsonParser.RECIPE_INVALID_COUNT;
    private Measure measure = null;
    private String ingredient = "";

    @SuppressWarnings("unused")
    public Ingredient() {
    }

    public Ingredient(int quantity, Measure measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
