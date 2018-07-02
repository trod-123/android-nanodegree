package com.zn.baking.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesGridItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesGridItemDecoration(int space) {
        this.space = space;
    }

    // TODO: Update this so it works with n number of columns
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;

        // Add top margin only for the top row to avoid double space between items
        if (parent.getChildLayoutPosition(view) <= 1) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }

        // Cut middle margins by half
        if((parent.getChildLayoutPosition(view) + 1) % 2 == 0) {
            outRect.right = space;
            outRect.left = space / 2;
        } else {
            outRect.right = space / 2;
            outRect.left = space;
        }
    }
}
