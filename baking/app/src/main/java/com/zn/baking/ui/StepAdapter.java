package com.zn.baking.ui;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zn.baking.R;
import com.zn.baking.model.Step;
import com.zn.baking.util.Toolbox;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepVH> {

    private List<Step> mSteps;
    private OnClickHandler mClickHandler;
    private int mResourceLayoutId;

    private boolean mCompactAdapter;

    private int mPositionSelected = RecyclerView.NO_POSITION;

    public int getPositionSelected() {
        return mPositionSelected;
    }

    public void setPositionSelected(int position) {
        if (mCompactAdapter) {
            notifyItemChanged(mPositionSelected);
            mPositionSelected = position;
            notifyItemChanged(mPositionSelected);
        }
    }

    public int setPositionSelected(Step step) {
        if (step != null && mCompactAdapter) {
            int position = getPositionOfItem(step);
            setPositionSelected(position);
            return position;
        } else {
            return Toolbox.NO_SELECTED_ID;
        }
    }

    /**
     * Builds a new Adapter for the Step class. Make sure the resourceLayoutId passed
     * at least contains a view for stepId and stepDescription
     *
     * @param steps
     * @param handler
     * @param resourceLayoutId
     */
    public StepAdapter(List<Step> steps, int resourceLayoutId, OnClickHandler handler) {
        mSteps = steps;
        mClickHandler = handler;
        mResourceLayoutId = resourceLayoutId;
        mCompactAdapter = mResourceLayoutId == R.layout.item_step_compact;
    }

    public interface OnClickHandler {
        void onClick(Step step);
    }

    @NonNull
    @Override
    public StepVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mResourceLayoutId, parent, false);
        return new StepVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepVH holder, int position) {
        // Highlight the selected item:
        // From: https://stackoverflow.com/questions/27194044/how-to-properly-highlight-selected-item-on-recyclerview
        if (mCompactAdapter) holder.itemView.setSelected(mPositionSelected == position);
        holder.bindView(mSteps.get(position));
    }

    @Override
    public int getItemCount() {
        if (mSteps != null) {
            return mSteps.size();
        } else {
            return 0;
        }
    }

    public int getPositionOfItem(Step step) {
        return mSteps.indexOf(step);
    }

    class StepVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.image_step_thumbnail)
        ImageView mIv_photo;
        @Nullable
        @BindView(R.id.step_loading_spinner)
        ProgressBar mPb_image;
        @BindView(R.id.text_step_number)
        TextView mTv_stepNum;
        @BindView(R.id.text_step_instruction)
        TextView mTv_instruction;

        private Step mStep;

        StepVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindView(Step step) {
            mStep = step;
            if (mIv_photo != null) {
                mPb_image.setVisibility(View.VISIBLE);
                RequestListener<Bitmap> requestListener = new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Timber.e(e != null ? e.getMessage() : "Exception message returned null",
                                "There was an issue loading the step thumbnail: %s");
                        mPb_image.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        mPb_image.setVisibility(View.GONE);
                        return false;
                    }
                };
                // Load either thumbnail URL or from video URL, whichever exists
                // If neither exists, glide loads a fallback image
                String sourceUrl = !step.getThumbnailURL().isEmpty() ?
                        step.getThumbnailURL() : step.getVideoURL();
                Toolbox.loadThumbnailFromUrl(itemView.getContext(), sourceUrl, mIv_photo,
                        requestListener);
            }

            mTv_stepNum.setText(String.format("%s", getAdapterPosition() == 0 ? "Intro" : getAdapterPosition()));
            mTv_instruction.setText(step.getShortDescription());
        }

        @Override
        public void onClick(View view) {
            if (mStep != null) {
                if (mCompactAdapter) setPositionSelected(getLayoutPosition());
                mClickHandler.onClick(mStep);
            } else {
                Toolbox.showToast(view.getContext(),
                        "There was an error registering the Step click: Step is null");
            }
        }
    }
}
