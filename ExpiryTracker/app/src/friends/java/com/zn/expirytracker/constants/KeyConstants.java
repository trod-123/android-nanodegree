package com.zn.expirytracker.constants;

import static com.zn.expirytracker.constants.Constants.MAX_FOODS_DATABASE_SIZE_NO_LIMIT;

/**
 * Friends variant. Being a bit nice. Cuz you should be nice to your friends :)
 */
public class KeyConstants {

    /**
     * Max number of images users can attach to a single item
     * <p>
     * Note: When setting, this must be greater than 2 (one for barcode, one for scanned image)
     */
    public static final int MAX_IMAGE_LIST_SIZE = 5;

    /**
     * Max number of foods users can simultaneously store in their database
     */
    public static final int MAX_FOODS_DATABASE_SIZE_DEFAULT = MAX_FOODS_DATABASE_SIZE_NO_LIMIT;

    /**
     * {@code true} to always show ads
     */
    public static final boolean ALWAYS_SHOW_ADS = false;
}
