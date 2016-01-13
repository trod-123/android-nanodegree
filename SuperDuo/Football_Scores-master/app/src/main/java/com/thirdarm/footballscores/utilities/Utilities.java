package com.thirdarm.footballscores.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.thirdarm.footballscores.R;
import com.thirdarm.footballscores.sync.ScoresSyncAdapter;

/**
 * Created by yehya khaled on 3/3/2015.
 *
 * Class updated by TROD on 20151216
 */
public class Utilities {

    private static final String LOG_TAG = Utilities.class.getSimpleName();

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
     * Gets the date and time
     */
    public static String[] convertUserDateTime(String dateTimeString) {
        // The time is embedded in date. Just extract the time.
        String time = dateTimeString.substring(dateTimeString.indexOf("T") + 1, dateTimeString.indexOf("Z"));
        // Cut the time out of the entire date provided in the JSON.
        String date = dateTimeString.substring(0, dateTimeString.indexOf("T"));

        // Get rid of seconds in the date-time format
        SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date parseddate = match_date.parse(date + time);
            SimpleDateFormat new_date = new SimpleDateFormat("EEEE, MMM d:h:mm aa");
            // Convert date-time to that of user's locale
            new_date.setTimeZone(TimeZone.getDefault());
            date = new_date.format(parseddate);
            time = date.substring(date.indexOf(":") + 1);
            date = date.substring(0, date.indexOf(":"));
            return new String[] {date, time};
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a date time array into a shortened, widget-friendly date time configuration
     * @param date The original date
     * @return A string array containing a shortened date and time
     */
    public static String convertShortUserDate(String date) {
        SimpleDateFormat longDateFormat = new SimpleDateFormat("EEEE, MMM d");
        try {
            Date parsedDate = longDateFormat.parse(date);
            SimpleDateFormat shortDateFormat = new SimpleDateFormat("EEE, MMM d");
            String shortDate = shortDateFormat.format(parsedDate);
            return shortDate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts milliseconds into a cursor-readable date format
     * @param ms Milliseconds
     * @return Date string in the form "EEEE, MMM dd"
     */
    public static String getUserDate(long ms) {
        Date date = new Date(ms);
        SimpleDateFormat sdFormat = new SimpleDateFormat("EEEE, MMM d");
        return sdFormat.format(date);
    }


    // Static URL prefixes
    public static final String SEASON_LINK = "http://api.football-data.org/v1/soccerseasons/";
    public static final String FIXTURE_LINK = "http://api.football-data.org/v1/fixtures/";
    public static final String TEAM_LINK = "http://api.football-data.org/v1/teams/";
    private static final String EMPTY_STRING = "";

    /**
     * Extracts the id associated with a given url
     *
     * @param url The url
     * @param baseUrl The type of url (prefix)
     * @return The id
     */
    public static int extractId(String url, String baseUrl) {
        return Integer.parseInt(url.replace(baseUrl, EMPTY_STRING));
    }

    public static String convertCrestUrl(String crestUrl) {
        if (crestUrl.length() > 1 && !crestUrl.contains(".png")) {
            String IMAGE_URL_BASE = "http://upload.wikimedia.org/wikipedia/";
//            Log.d(LOG_TAG, "Original crest url is: " + crestUrl);
            String crestFileName = crestUrl.substring(crestUrl.lastIndexOf("/") + 1);
//            Log.d(LOG_TAG, "Crest filename is: " + crestFileName);
            String prefixCrestUrl = crestUrl.substring(0, crestUrl.indexOf("/", IMAGE_URL_BASE.length() + 1));
//            Log.d(LOG_TAG, "Prefixed crest url is: " + prefixCrestUrl);
            String postfixCrestUrl = crestUrl.substring(prefixCrestUrl.length());
//            Log.d(LOG_TAG, "Postfixed crest url is: " + postfixCrestUrl);
            crestUrl = prefixCrestUrl + "/thumb" + postfixCrestUrl + "/200px-" + crestFileName + ".png";
//            Log.d(LOG_TAG, "NEW Crest url is: " + crestUrl);
        }
        return crestUrl;
    }

}
