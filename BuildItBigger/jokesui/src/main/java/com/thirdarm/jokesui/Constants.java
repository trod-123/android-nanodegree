package com.thirdarm.jokesui;

/**
 * Created by TROD on 20160410.
 */
public class Constants {

    // Fragment tags
    public class TAGS {
        public static final String FRAGMENT_JOKES = "FRAGMENT-JOKES";
        public static final String FRAGMENT_ADD_JOKE_DIALOG = "FRAGMENT-ADD-DIALOG";
        public static final String FRAGMENT_REMOVE_JOKE_DIALOG = "FRAGMENT-REMOVE-DIALOG";
        public static final String FRAGMENT_PAID_DIALOG = "FRAGMENT-PAID-DIALOG";
        public static final String FRAGMENT_RESET_DATABASE_DIALOG = "FRAGMENT-RESET-DIALOG";
        public static final String FRAGMENT_ABOUT_DIALOG = "FRAGMENT-ABOUT-DIALOG";
    }

    // Joke category constants
    // TODO: Rename categories
    public class CATEGORY {
        public static final int FIRST = 0;
        public static final int SECOND = 1;
        public static final int THIRD = 2;
        public static final int FOURTH = 3;
        public static final int FIFTH = 4;
        public static final int SIXTH = 5;
        public static final int SEVENTH = 6;
        public static final int EIGHTH = 7;
        public static final int NINTH = 8;
        public static final int TENTH = 9;
    }

    // Joke UI integrates with different flavors. Here they are.
    public class FLAVOR {
        public static final int FREE = 0;
        public static final int PAID = 1;
    }
}
