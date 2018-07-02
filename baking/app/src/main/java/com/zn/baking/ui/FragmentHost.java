package com.zn.baking.ui;

import android.support.v4.app.Fragment;

/**
 * Utilities for handling fragments in a host activity outside the hosting activity's class
 */
public interface FragmentHost {
    /**
     * Allows for adding fragments from outside the hosting activity's class
     * @param fragment
     * @param fragmentContainerId
     * @param addToBackstack
     */
    void showFragment(Fragment fragment, int fragmentContainerId, Boolean addToBackstack);

    /**
     * Lets the calling fragment know if it's in tablet layout
     * @return
     */
    boolean isInTabletLayout();
}
