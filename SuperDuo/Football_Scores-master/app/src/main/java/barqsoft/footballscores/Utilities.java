package barqsoft.footballscores;

import android.content.Context;

/**
 * Created by yehya khaled on 3/3/2015.
 *
 * Class updated by TROD on 20151216
 */
public class Utilities {

    // League numbers
    public static final int BUNDESLIGA1 = 394; // "1. Bundesliga 2015/16"
    public static final int BUNDESLIGA2 = 395; // "2. Bundesliga 2015/16"
    public static final int LIGUE1 = 396; // "Ligue 1 2015/16"
    public static final int LIGUE2 = 397; // "Ligue 2 2015/16"
    public static final int PREMIER_LEAGUE = 398; // "Premier League 2015/16"
    public static final int PRIMERA_DIVISION = 399; // "Primera Division 2015/16"
    public static final int SEGUNDA_DIVISION = 400; // "Segunda Division 2015/16"
    public static final int SERIE_A = 401; // "Serie A 2015/16"
    public static final int PRIMERA_LIGA = 402; // "Primeira Liga 2015/16"
    public static final int BUNDESLIGA3 = 403; // "3. Bundesliga 2015/16"
    public static final int EREDIVISIE = 404; // "Eredivisie 2015/16"
    public static final int CHAMPIONS = 405; // "Champions League 2015/16"

    /**
     * Gets the name of the league corresponding to its number
     *
     * @param c Context of the activity
     * @param league_num The number of the league
     * @return The name of the league
     */
    public static String getLeague(Context c, int league_num) {
        switch (league_num) {
            case BUNDESLIGA1 :
            case BUNDESLIGA2 :
            case BUNDESLIGA3 : return c.getString(R.string.league_bundesliga);
            case LIGUE1 :
            case LIGUE2 :  return c.getString(R.string.league_ligue);
            case PREMIER_LEAGUE : return c.getString(R.string.league_premier_league);
            case PRIMERA_DIVISION : return c.getString(R.string.league_primera_division);
            case SEGUNDA_DIVISION : return c.getString(R.string.league_segunda_division);
            case SERIE_A : return c.getString(R.string.league_serie_a);
            case PRIMERA_LIGA : return c.getString(R.string.league_primeira_liga);
            case EREDIVISIE : return c.getString(R.string.league_eredivisie);
            case CHAMPIONS : return c.getString(R.string.league_champions);
            default : return c.getString(R.string.league_unknown);
        }
    }

    /**
     * Gets the number of the match day. If it is currently champions league, then it gets the
     *  name of the stage of the league
     *
     * @param c Context of the activity
     * @param match_day The match day
     * @param league_num The league number
     * @return The name or number of the match day
     */
    public static String getMatchDay(Context c, int match_day, int league_num) {
        if (league_num == CHAMPIONS) {
            if (match_day <= 6) return c.getString(R.string.match_champions_gs);
            else if(match_day == 7 || match_day == 8) return c.getString(R.string.match_champions_fkr);
            else if(match_day == 9 || match_day == 10) return c.getString(R.string.match_champions_qf);
            else if(match_day == 11 || match_day == 12) return c.getString(R.string.match_champions_sf);
            else return c.getString(R.string.match_champiions_f);
        }
        else return c.getString(R.string.match_default, match_day);
    }

    /**
     * Gets a string containing the number of home and away goals
     *
     * @param c Context of the activity
     * @param home_goals Number of home goals
     * @param away_goals Number of away goals
     * @return Formatted string containing number of home and away goals
     */
    public static String getScores(Context c, int home_goals,int away_goals) {
        return home_goals < 0 || away_goals < 0 ?
                c.getString(R.string.scores_invalid) :
                c.getString(R.string.scores_home_away, home_goals, away_goals);
    }

    /**
     * Gets the image of the team's crest
     *
     * @param teamname Name of the team
     * @return The id of the drawable of the team's crest
     */
    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }
}
