package com.zn.baking.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zn.baking.R;
import com.zn.baking.model.Step;
import com.zn.baking.util.Toolbox;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompactStepAdapter extends RecyclerView.Adapter<CompactStepAdapter.CompactStepVH> {

    private List<Step> mSteps;
    private OnClickHandler mClickHandler;

    public CompactStepAdapter(List<Step> steps, OnClickHandler handler) {
        this.mSteps = steps;
        this.mClickHandler = handler;
    }

    public interface OnClickHandler {
        void onClick(Step step);
    }

    @NonNull
    @Override
    public CompactStepVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step, parent, false);
        return new CompactStepVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompactStepVH holder, int position) {
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

    public void swapLists(List<Step> steps) {
        this.mSteps = steps;
        notifyDataSetChanged();
    }

    class CompactStepVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_step_number)
        TextView mTv_stepNum;
        @BindView(R.id.text_step_instruction)
        TextView mTv_instruction;

        private Step mStep;

        CompactStepVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindView(Step step) {
            mStep = step;

            mTv_stepNum.setText(String.format("%s", getAdapterPosition() == 0 ? "Intro" : getAdapterPosition()));
            mTv_instruction.setText(step.getShortDescription());
        }

        @Override
        public void onClick(View view) {
            if (mStep != null) {
                mClickHandler.onClick(mStep);
            } else {
                Toolbox.showToast(view.getContext(),
                        "There was an error registering the Step click: Step is null");
            }
        }
    }
}
