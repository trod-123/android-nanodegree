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
import com.zn.baking.ui.FragmentHost;
import com.zn.baking.ui.StepAdapter;
import com.zn.baking.util.Toolbox;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Only shows for tablet devices
 */
public class CompactStepListFragment extends Fragment {

    public static final String SELECTED_STEP_KEY = "com.zn.baking.selected_step_key";

    @BindView(R.id.recyclerview_steps_list)
    RecyclerView mRecyclerView_steps;
    StepAdapter mAdapter;

    Recipe mRecipe;

    int mSelectedStepIndex = Toolbox.NO_SELECTED_ID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_list, container, false);
        ButterKnife.bind(this, view);
        Timber.tag(StepActivity.class.getSimpleName());

        // Up navigation handled in hosting Activity
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecipe = getArguments().getParcelable(RecipeListFragment.RECIPE_PARCELABLE_EXTRA_KEY);
        if (mRecipe != null) {
            // set up steps recyclerview
            mAdapter = new StepAdapter(mRecipe.getSteps(), R.layout.item_step_compact,
                    new StepAdapter.OnClickHandler() {
                        @Override
                        public void onClick(Step step) {
                            int index = mAdapter.getPositionOfItem(step);
                            if (mSelectedStepIndex != index) {
                                launchRecipeStepFragment(step, index,
                                        mRecipe.getName(), mRecipe.getSteps().size());
                            }
                        }
                    });
            mSelectedStepIndex = getArguments().getInt(RecipeStepFragment.STEP_POSITION_EXTRA_KEY,
                    Toolbox.NO_SELECTED_ID);
        }

        if (savedInstanceState != null) {
            mSelectedStepIndex = savedInstanceState.getInt(SELECTED_STEP_KEY,
                    Toolbox.NO_SELECTED_ID);
        }

        mRecyclerView_steps.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView_steps.setAdapter(mAdapter);
        mRecyclerView_steps.setHasFixedSize(true);

        mAdapter.setPositionSelected(mSelectedStepIndex);

        return view;
    }

    /**
     * Helper to launch the step fragment for the selected step
     *
     * @param step
     * @param recipeName
     * @param numSteps
     */
    private void launchRecipeStepFragment(Step step, int position, String recipeName, int numSteps) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RecipeStepFragment.STEP_PARCELABLE_EXTRA_KEY, step);
        bundle.putInt(RecipeStepFragment.STEP_POSITION_EXTRA_KEY, position);
        bundle.putString(RecipeStepFragment.RECIPE_NAME_EXTRA_KEY, recipeName);
        bundle.putInt(RecipeStepFragment.NUM_STEPS_EXTRA_KEY, numSteps);

        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(bundle);

        mAdapter.setPositionSelected(mSelectedStepIndex = position);

        ((FragmentHost) getActivity()).showFragment(
                fragment, R.id.step_fragment_container, false, null,
                null, null,
                Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE,
                Toolbox.NO_ANIMATOR_RESOURCE, Toolbox.NO_ANIMATOR_RESOURCE);
    }
}
