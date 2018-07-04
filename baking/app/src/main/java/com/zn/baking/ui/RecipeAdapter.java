package com.zn.baking.ui;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zn.baking.R;
import com.zn.baking.model.Recipe;
import com.zn.baking.util.Colors;
import com.zn.baking.util.Toolbox;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeVH> {

    private List<Recipe> mRecipeList;
    private OnClickHandler mClickHandler;

    public RecipeAdapter(List<Recipe> recipes, OnClickHandler handler) {
        this.mRecipeList = recipes;
        this.mClickHandler = handler;
    }

    public interface OnClickHandler {
        void onClick(Recipe recipe);
    }

    @NonNull
    @Override
    public RecipeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeVH holder, int position) {
        holder.bindView(mRecipeList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mRecipeList != null) {
            return mRecipeList.size();
        } else {
            return 0;
        }
    }

    public int getRecipePosition(Recipe recipe) {
        return mRecipeList.indexOf(recipe);
    }

    public void swapLists(List<Recipe> recipes) {
        this.mRecipeList = recipes;
        notifyDataSetChanged();
    }

    /**
     * Returns the recipe at the provided position. Null if index is out of bounds.
     *
     * @param i
     * @return
     */
    public Recipe getRecipeAtPosition(int i) {
        if (i < mRecipeList.size())
            return mRecipeList.get(i);
        else
            return null;
    }

    public class RecipeVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_recipe_photo)
        ImageView mIv_photo;
        @BindView(R.id.text_num_steps)
        TextView mTv_numSteps;
        @BindView(R.id.container_recipe_title)
        LinearLayout mLayout_recipe_title;
        @BindView(R.id.text_recipe_name)
        TextView mTv_recipe_name;
        @BindView(R.id.text_num_servings)
        TextView mTv_servings;

        private Recipe mRecipe;

        RecipeVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindView(Recipe recipe) {
            mRecipe = recipe;
            // get the final video via list of steps
            String lastVideoUrl = Toolbox.getLastVideoUrlFromRecipe(recipe);
            // get the thumbnail image from the video url
            // listener source: https://stackoverflow.com/questions/26054420/set-visibility-of-progress-bar-gone-on-completion-of-image-loading-using-glide-l
            RequestListener<Bitmap> listener = new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    if (e != null) {
                        Timber.e(e);
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    // set the background color of the title container to match color of image (source: https://developer.android.com/training/material/palette-colors)
                    Palette palette = Palette.from(resource).generate();
                    int contextColor = palette.getMutedColor(itemView.getResources().getColor(Colors.DEFAULT_RECIPE_CARD_COLOR)); // TODO: Play around with the color options
                    mLayout_recipe_title.setBackgroundColor(contextColor);
                    int textColor = Toolbox.getTextColorFromBackgroundColor(itemView.getContext(), contextColor);
                    mTv_recipe_name.setTextColor(textColor);
                    mTv_servings.setTextColor(textColor);
                    return false;
                }
            };
            Toolbox.loadThumbnailFromVideoUrl(itemView.getContext(), lastVideoUrl, mIv_photo, listener);

            // set the textviews
            mTv_numSteps.setText(
                    Toolbox.generateNumStepsString(itemView.getContext(),
                            recipe.getSteps().size() - 1));
            mTv_recipe_name.setText(recipe.getName());
            mTv_servings.setText(
                    Toolbox.generateNumServingsString(itemView.getContext(), recipe.getServings()));
        }

        @Override
        public void onClick(View view) {
            if (mRecipe != null) {
                mClickHandler.onClick(mRecipe);
            } else {
                Toolbox.showToast(view.getContext(),
                        "There was an error registering the Recipe click: Recipe is null");
            }
        }
    }
}
