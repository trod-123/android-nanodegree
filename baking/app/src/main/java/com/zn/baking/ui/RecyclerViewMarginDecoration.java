package com.zn.baking.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zn.baking.R;

/**
 * From: http://blog.sqisland.com/2014/12/recyclerview-autofit-grid.html
 * https://github.com/chiuki/android-recyclerview
 */
public class RecyclerViewMarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    public RecyclerViewMarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.recycler_view_item_margin);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }
}
