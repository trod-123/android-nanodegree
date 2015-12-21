package barqsoft.footballscores.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by TROD on 20151216.
 *
 * Schematic implementation of columns for content provider
 */
public interface ScoresColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull String DATE = "date";
    @DataType(INTEGER) @NotNull String TIME = "time";
    @DataType(TEXT) @NotNull String HOME_NAME = "home_name";
    @DataType(TEXT) @NotNull String AWAY_NAME = "away_name";
    @DataType(INTEGER) @NotNull String LEAGUE_NAME = "league_name";
    @DataType(INTEGER) String HOME_GOALS = "home_goals";
    @DataType(INTEGER) String AWAY_GOALS = "away_goals";
    @DataType(INTEGER) @NotNull String MATCH_ID = "match_id";
    @DataType(INTEGER) @NotNull String MATCH_DAY = "match_day";
    @DataType(TEXT) String HOME_CREST_URL = "home_crest_url";
    @DataType(TEXT) String AWAY_CREST_URL = "away_crest_url";

}
