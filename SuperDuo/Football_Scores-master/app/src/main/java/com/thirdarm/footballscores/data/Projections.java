package com.thirdarm.footballscores.data;

/**
 * Created by TROD on 20151216.
 */
public interface Projections {

    class SCORES {
        public static final String[] COLUMNS = new String[] {
                ScoresColumns._ID,
                ScoresColumns.DATE,
                ScoresColumns.TIME,
                ScoresColumns.HOME_NAME,
                ScoresColumns.AWAY_NAME,
                ScoresColumns.LEAGUE_NAME,
                ScoresColumns.HOME_GOALS,
                ScoresColumns.AWAY_GOALS,
                ScoresColumns.MATCH_ID,
                ScoresColumns.MATCH_DAY,
        };

        public static final int COL_ID = 0;
        public static final int COL_DATE = 1;
        public static final int COL_TIME = 2;
        public static final int COL_HOME_NAME = 3;
        public static final int COL_AWAY_NAME = 4;
        public static final int COL_LEAGUE_NAME = 5;
        public static final int COL_HOME_GOALS = 6;
        public static final int COL_AWAY_GOALS = 7;
        public static final int COL_MATCH_ID = 8;
        public static final int COL_MATCH_DAY = 9;
    }

    class TEAMS {
        public static final String[] COLUMNS = new String[] {
                TeamsColumns._ID,
                TeamsColumns.NAME,
                TeamsColumns.SHORT_NAME,
                TeamsColumns.VALUE,
                TeamsColumns.CREST_URL
        };

        public static final int COL_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_SHORT_NAME = 2;
        public static final int COL_VALUE = 3;
        public static final int COL_CREST_URL = 4;
    }
}