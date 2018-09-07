package com.zn.expirytracker.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zn.expirytracker.R;

/**
 * Custom {@link ViewPager} class that can enable and disable paging through swiping
 * <p>
 * Source: https://stackoverflow.com/questions/9650265/how-do-disable-paging-by-swiping-with-finger-in-viewpager-but-still-be-able-to-s
 */
public class NonSwipeableViewPager extends ViewPager {

    private boolean mPagingEnabled;

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NonSwipeableViewPager,
                0, 0);

        try {
            mPagingEnabled = a.getBoolean(R.styleable.NonSwipeableViewPager_enablePaging,
                    true);
        } finally {
            a.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mPagingEnabled) {
            // page only if it's enabled
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mPagingEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.mPagingEnabled = enabled;
    }
}
