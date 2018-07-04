package com.zn.baking;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zn.baking.model.Recipe;
import com.zn.baking.model.Step;
import com.zn.baking.ui.CompactStepAdapter;
import com.zn.baking.ui.FragmentHost;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Only shows for tablet devices
 */
public class CompactStepListFragment extends Fragment {

    @BindView(R.id.recyclerview_steps_list)
    RecyclerView mRecyclerView_steps;
    CompactStepAdapter mAdapter;

    Recipe mRecipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_list, container, false);
        ButterKnife.bind(this, view);
        Timber.tag(StepActivity.class.getSimpleName());

        // Up navigation handled in hosting Activity
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecipe = (Recipe) getArguments()
                .getSerializable(RecipeListFragment.RECIPE_SERIALIZABLE_EXTRA_KEY);

        if (mRecipe != null) {
            // set up steps recyclerview
            mAdapter = new CompactStepAdapter(mRecipe.getSteps(), new CompactStepAdapter.OnClickHandler() {
                @Override
                public void onClick(Step step) {
                    int position = mAdapter.getPositionOfItem(step);
                    launchRecipeStepFragment(step, position, mRecipe.getName(), mRecipe.getSteps().size());
                }
            });
        }
        mRecyclerView_steps.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView_steps.setAdapter(mAdapter);
        mRecyclerView_steps.setHasFixedSize(true);

        return view;
    }

    /**
     * Helper to launch the step fragment for the selected step
     * @param step
     * @param recipeName
     * @param numSteps
     */
    private void launchRecipeStepFragment(Step step, int position, String recipeName, int numSteps) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(RecipeStepFragment.STEP_SERIALIZABLE_EXTRA_KEY, step);
        bundle.putInt(RecipeStepFragment.STEP_POSITION_EXTRA_KEY, position);
        bundle.putString(RecipeStepFragment.RECIPE_NAME_EXTRA_KEY, recipeName);
        bundle.putInt(RecipeStepFragment.NUM_STEPS_EXTRA_KEY, numSteps);

        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(bundle);

        ((FragmentHost) getActivity()).showFragment(
                fragment, R.id.step_fragment_container, false);
    }
}
