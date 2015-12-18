package barqsoft.footballscores.data;

/**
 * Created by TROD on 20151216.
 */
public interface ScoresProjections {

    String[] COLUMNS = new String[] {
            ScoresColumns._ID,
            ScoresColumns.DATE,
            ScoresColumns.TIME,
            ScoresColumns.HOME_NAME,
            ScoresColumns.AWAY_NAME,
            ScoresColumns.LEAGUE_NAME,
            ScoresColumns.HOME_GOALS,
            ScoresColumns.AWAY_GOALS,
            ScoresColumns.MATCH_ID,
            ScoresColumns.MATCH_DAY
    };

    int COL_ID = 0;
    int COL_DATE = 1;
    int COL_TIME = 2;
    int COL_HOME_NAME = 3;
    int COL_AWAY_NAME = 4;
    int COL_LEAGUE_NAME = 5;
    int COL_HOME_GOALS = 6;
    int COL_AWAY_GOALS = 7;
    int COL_MATCH_ID = 8;
    int COL_MATCH_DAY = 9;
}