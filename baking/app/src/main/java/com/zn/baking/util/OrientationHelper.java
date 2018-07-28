package com.zn.baking.util;

import android.content.Context;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Utility methods for manipulating device orientations
 */
public class OrientationHelper {

    private static final int ROTATION_270_LOWER = 67;
    private static final int ROTATION_270_UPPER = 133;
    private static final int ROTATION_90_LOWER = 227;
    private static final int ROTATION_90_UPPER = 293;
    private static final int ROTATION_0_LOWER = 337;
    private static final int ROTATION_0_UPPER = 23;

    private static final int ROTATION_0_LOWER_STATIC = 315;
    private static final int ROTATION_0_UPPER_STATIC = 45;
    private static final int ROTATION_270_UPPER_STATIC = 135;
    private static final int ROTATION_180_UPPER_STATIC = 225;

    public static final int ROTATION_NO_CHANGE = -1;
    public static final int ROTATION_INVALID = -1;

    /**
     * Returns the targeted Surface.ROTATION value that corresponds with the device's display
     * orientation and the device's actual physical orientation value
     *
     * @param displayOrientation
     * @param orientation
     * @return
     */
    public static int getTargetOrientation(int displayOrientation, int orientation) {
        if (displayOrientation == Surface.ROTATION_0) {
            if (shouldRotate_270(orientation)) {
                return Surface.ROTATION_270;
            } else if (shouldRotate_90(orientation)) {
                return Surface.ROTATION_90;
            }
        } else if (displayOrientation == Surface.ROTATION_270) {
            if (shouldRotate_0(orientation)) {
                return Surface.ROTATION_0;
            } else if (shouldRotate_90(orientation)) {
                return Surface.ROTATION_90;
            }
        } else if (displayOrientation == Surface.ROTATION_90) {
            if (shouldRotate_0(orientation)) {
                return Surface.ROTATION_0;
            } else if (shouldRotate_270(orientation)) {
                return Surface.ROTATION_270;
            }
        }
        return ROTATION_NO_CHANGE;
    }

    private static boolean shouldRotate_270(int orientation) {
        return orientation >= ROTATION_270_LOWER && orientation <= ROTATION_270_UPPER;
    }

    private static boolean shouldRotate_90(int orientation) {
        return orientation >= ROTATION_90_LOWER && orientation <= ROTATION_90_UPPER;
    }

    private static boolean shouldRotate_0(int orientation) {
        return orientation <= ROTATION_0_UPPER || orientation >= ROTATION_0_LOWER;
    }

    /**
     * Returns the Surface.ROTATION value that corresponds with the physical device's actual
     * orientation value
     *
     * @param orientation
     * @return
     */
    public static int getDeviceOrientation(int orientation) {
        if (orientation >= ROTATION_0_LOWER_STATIC || orientation <= ROTATION_0_UPPER_STATIC) {
            return Surface.ROTATION_0;
        } else if (orientation <= ROTATION_270_UPPER_STATIC) {
            return Surface.ROTATION_270;
        } else if (orientation <= ROTATION_180_UPPER_STATIC) {
            return Surface.ROTATION_180;
        } else {
            return Surface.ROTATION_90;
        }
    }

    /**
     * Returns the opposite orientation provided. Return ROTATION_INVALID if invalid.
     *
     * @param orientation
     * @return
     */
    public static int getOppositeOrientation(int orientation) {
        switch (orientation) {
            case Surface.ROTATION_0:
                return Surface.ROTATION_180;
            case Surface.ROTATION_90:
                return Surface.ROTATION_270;
            case Surface.ROTATION_180:
                return Surface.ROTATION_0;
            case Surface.ROTATION_270:
                return Surface.ROTATION_90;
            default:
                return ROTATION_INVALID;
        }
    }

    /**
     * Helper for getting current screen orientation. See more: getRotation() method
     *
     * @param context
     * @return
     */
    public static int getCurrentOrientation(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
    }
}
