/*
 *  Copyright (C) 2015 Teddy Rodriguez (TROD)
 *    email: cia.123trod@gmail.com
 *    github: TROD-123
 *
 *  For Udacity's Android Developer Nanodegree
 *  P1-2: Popular Movies
 *
 *  Currently for educational purposes only.
 */

package com.thirdarm.popularmovies.function;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by TROD on 20150919.
 *
 * For resizing movie poster images to standardize grid view sizes
 */
public class AutoResizeImageView extends ImageView {

    public static final int POSTER_RATIO = 3/2;

    public AutoResizeImageView(Context context)
    {
        super(context);
    }

    public AutoResizeImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AutoResizeImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * POSTER_RATIO); //Snap to width
    }
}
