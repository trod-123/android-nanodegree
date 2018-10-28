package com.zn.expirytracker.constants;

/**
 * Regular public variant. No niceness here. If you want niceness, become a friend
 */
public class KeyConstants {

    /**
     * Max number of images users can attach to a single item
     * <p>
     * Note: When setting, this must be greater than 2 (one for barcode, one for scanned image)
     */
    public static final int MAX_IMAGE_LIST_SIZE = 3;

    /**
     * Max number of foods users can simultaneously store in their database
     */
    public static final int MAX_FOODS_DATABASE_SIZE_DEFAULT = 100;

    /**
     * {@code true} to always show ads
     */
    public static final boolean ALWAYS_SHOW_ADS = true;
}
