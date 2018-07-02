package com.zn.baking.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zn.baking.model.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientVH> {

    private List<Ingredient> mIngredientList;

    public IngredientAdapter(List<Ingredient> ingredientList) {
        this.mIngredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientVH ingredientVH, int i) {

    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }

    class IngredientVH extends RecyclerView.ViewHolder {

        public IngredientVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
