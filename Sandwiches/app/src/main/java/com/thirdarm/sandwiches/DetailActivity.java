package com.thirdarm.sandwiches;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thirdarm.sandwiches.databinding.ActivityDetailBinding;
import com.thirdarm.sandwiches.model.Sandwich;
import com.thirdarm.sandwiches.utils.JsonUtils;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Use databinding to set the view */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        /* Grab the position of the selected item in MainActivity */

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        /* Grab the sandwich that is selected in MainActivity, based on the position */
        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        /* Populate the UI */
        populateUI(sandwich);

        /* Set the title of the activity */
        setTitle(sandwich.getMainName());
    }

    /**
     * Show toast to user if there was an error getting the sandwich data
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method for populating UI text elements
     */
    private void populateUI(Sandwich sandwich) {
        // populate the "easy" data automatically
        mBinding.setSandwich(sandwich);

        // if there are empty or null values, then hide these views

        String description = sandwich.getDescription();
        String origin = sandwich.getPlaceOfOrigin();

        if (description == null || description.isEmpty()) {
            mBinding.descriptionLabelTv.setVisibility(View.GONE);
            mBinding.descriptionTv.setVisibility(View.GONE);
        }

        if (origin == null || origin.isEmpty()) {
            mBinding.originLabelTv.setVisibility(View.GONE);
            mBinding.originTv.setVisibility(View.GONE);
        }

        // populate the lists, or hide views if lists are empty or null

        List<String> ingredientsList = sandwich.getIngredients();
        if (ingredientsList == null || ingredientsList.size() == 0) {
            mBinding.ingredientsLabelTv.setVisibility(View.GONE);
            mBinding.ingredientsTv.setVisibility(View.GONE);
        } else {
            String ingredients = TextUtils.join(", ", ingredientsList);
            mBinding.ingredientsTv.setText(ingredients);
        }

        List<String> alsoKnownList = sandwich.getAlsoKnownAs();
        if (alsoKnownList == null || alsoKnownList.size() == 0) {
            mBinding.alsoKnownLabelTv.setVisibility(View.GONE);
            mBinding.alsoKnownTv.setVisibility(View.GONE);
        } else {
            String aliases = TextUtils.join(", ", alsoKnownList);
            mBinding.alsoKnownTv.setText(aliases);
        }

        // load the image. show image icon if error
        Picasso.with(this)
                .load(sandwich.getImage())
                .error(R.drawable.ic_panorama_white_24dp)
                .into(mBinding.imageIv);
    }
}
