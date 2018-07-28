package com.zn.baking.ui;

import android.support.transition.Transition;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

/**
 * Utilities for handling fragments in a host activity outside the hosting activity's class
 */
public interface FragmentHost {

    /**
     * Allows for adding fragments from outside the hosting activity's class
     * @param fragment
     * @param fragmentContainerId
     * @param addToBackstack
     * @param sharedImageTransition
     * @param enterTransition
     * @param exitTransition
     */
    void showFragment(Fragment fragment, int fragmentContainerId, Boolean addToBackstack,
                      ImageView sharedImageTransition, Transition enterTransition,
                      Transition exitTransition,
                      int enterAnimResource, int exitAnimResource,
                      int popEnterAnimResource, int popExitAnimResource);

    /**
     * Lets the calling fragment know if it's in tablet layout
     * @return
     */
    boolean isInTabletLayout();
}
