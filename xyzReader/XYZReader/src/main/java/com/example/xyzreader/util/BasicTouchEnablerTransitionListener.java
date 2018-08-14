package com.example.xyzreader.util;

import android.transition.Transition;
import android.view.Window;

/**
 * Transition Listener that disables touches during transitions, and re-enables them once they're
 * done
 * <p>
 * If touch needn't be modified in any of the methods, just override and don't pass in super
 * <p>
 * Idea from: https://stackoverflow.com/questions/26971825/disable-clicks-when-fragment-adding-animation-playing
 */
public class BasicTouchEnablerTransitionListener implements Transition.TransitionListener {
    private Window mWindow;

    public BasicTouchEnablerTransitionListener(Window window) {
        mWindow = window;
    }

    @Override
    public void onTransitionStart(Transition transition) {
        Toolbox.enableTouchResponse(mWindow, false);
    }

    @Override
    public void onTransitionEnd(Transition transition) {
        Toolbox.enableTouchResponse(mWindow, true);
    }

    @Override
    public void onTransitionCancel(Transition transition) {
        Toolbox.enableTouchResponse(mWindow, true);
    }

    @Override
    public void onTransitionPause(Transition transition) {
        Toolbox.enableTouchResponse(mWindow, true);
    }

    @Override
    public void onTransitionResume(Transition transition) {
        Toolbox.enableTouchResponse(mWindow, false);
    }
}
