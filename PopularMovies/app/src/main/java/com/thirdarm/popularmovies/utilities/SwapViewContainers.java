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

package com.thirdarm.popularmovies.utilities;

import android.view.View;
import android.view.ViewGroup;

/**
 * Class for swapping view container layers that pertain to single fragment instances.
 */
public class SwapViewContainers {

    /**
     * Displays overlay containing loading icon and status, resets horizontal progress bar, and
     *  disables touch events.
     */
    public static void showViewContainer(View container, View rootView) {
        container.bringToFront();
        container.setVisibility(View.VISIBLE);
        enableDisableViewGroup((ViewGroup) rootView, false);
    }

    /** Hides overlay and enables touch events */
    public static void hideViewContainer(View container, View rootView) {
        container.setVisibility(View.INVISIBLE);
        container.setClickable(true);
        enableDisableViewGroup((ViewGroup) rootView, true);
    }

    /**
     * Enables/Disables all child views in a view group.
     * From http://stackoverflow.com/questions/5418510/disable-the-touch-events-for-all-the-views
     *
     * @param viewGroup the view group
     * @param enabled <code>true</code> to enable, <code>false</code> to disable
     * the views.
     */
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }
}
