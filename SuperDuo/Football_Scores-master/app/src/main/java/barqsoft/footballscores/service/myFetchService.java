package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.ScoresColumns;
import barqsoft.footballscores.data.ScoresProvider;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class myFetchService extends IntentService
{
    public static final String LOG_TAG = "myFetchService";
    public myFetchService()
    {
        super("myFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        getData("n2");
        getData("p2");

        return;
    }

    private void getData (String timeFrame)
    {
        //Creating fetch URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod("GET");
            // This is where the API key is used
            m_connection.addRequestProperty("X-Auth-Token",getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Exception here" + e.getMessage());
        }
        finally {
            if(m_connection != null)
            {
                m_connection.disconnect();
            }
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,"Error Closing Stream");
                }
            }
        }
        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray("fixtures");
                if (matches.length() == 0) {
//                    //if there is no data, call the function on dummy data
//                    //this is expected behavior during the off season.
//                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }


                processJSONdata(JSON_data, getApplicationContext());
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    // isReal will be false if dummy data is loaded as JSONdata
    private void processJSONdata (String JSONdata,Context mContext)
    {
        //JSON data
        // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
        // be updated. Feel free to use the codes
        final String BUNDESLIGA1 = "394";
        final String BUNDESLIGA2 = "395";
        final String LIGUE1 = "396";
        final String LIGUE2 = "397";
        final String PREMIER_LEAGUE = "398";
        final String PRIMERA_DIVISION = "399";
        final String SEGUNDA_DIVISION = "400";
        final String SERIE_A = "401";
        final String PRIMERA_LIGA = "402";
        final String Bundesliga3 = "403";
        final String EREDIVISIE = "404";

        // This is for processing the JSON fields within /fixtures
        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        //Match data
        String League = null;
        String mDate = null;
        String mTime = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;


        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);


            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());

            // Process each fixture data
            for(int i = 0;i < matches.length();i++)
            {

                // Extract league id from fixture soccerseason href url
                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString("href");
                League = League.replace(SEASON_LINK,"");

                // Extra match id from fixture
                match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).
                        getString("href");
                match_id = match_id.replace(MATCH_LINK, "");


                /*
                    Playing with dates and times
                 */

                mDate = match_data.getString(MATCH_DATE);
                // The time is embedded in date. Just extract the time.
                mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                // Cut the time out of the entire date provided in the JSON.
                mDate = mDate.substring(0,mDate.indexOf("T"));
                // Convert date and time to that of the user's locale
                SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date parseddate = match_date.parse(mDate+mTime);
                    SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                    new_date.setTimeZone(TimeZone.getDefault());
                    mDate = new_date.format(parseddate);
                    mTime = mDate.substring(mDate.indexOf(":") + 1);
                    mDate = mDate.substring(0,mDate.indexOf(":"));
                }
                catch (Exception e) {
                    Log.d(LOG_TAG, "error here!");
                    Log.e(LOG_TAG,e.getMessage());
                }

                /*
                    Playing with the teams
                 */

                // Get team names
                Home = match_data.getString(HOME_TEAM);
                Away = match_data.getString(AWAY_TEAM);
                // Get number of goals
                Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                // Get match day
                match_day = match_data.getString(MATCH_DAY);

                /*
                    Insert into db
                 */

                ContentResolver cr = mContext.getContentResolver();
                Cursor cursor = cr.query(
                        ScoresProvider.Scores.CONTENT_URI,
                        new String[] {ScoresColumns.MATCH_ID},
                        ScoresColumns.MATCH_ID + " == ? ",
                        new String[] {match_id},
                        null
                );

                ContentValues match_values = new ContentValues();
                match_values.put(ScoresColumns.DATE, mDate);
                match_values.put(ScoresColumns.TIME, mTime);
                match_values.put(ScoresColumns.HOME_NAME, Home);
                match_values.put(ScoresColumns.AWAY_NAME, Away);
                match_values.put(ScoresColumns.LEAGUE_NAME, League);
                match_values.put(ScoresColumns.HOME_GOALS, Home_goals);
                match_values.put(ScoresColumns.AWAY_GOALS, Away_goals);
                match_values.put(ScoresColumns.MATCH_ID, match_id);
                match_values.put(ScoresColumns.MATCH_DAY, match_day);

                //log spam

//                Log.v(LOG_TAG,match_id);
//                Log.v(LOG_TAG,mDate);
//                Log.v(LOG_TAG,mTime);
//                Log.v(LOG_TAG,Home);
//                Log.v(LOG_TAG,Away);
//                Log.v(LOG_TAG,Home_goals);
//                Log.v(LOG_TAG,Away_goals);

                if (cursor.moveToFirst()) {
                    cr.update(ScoresProvider.Scores.CONTENT_URI,
                            match_values,
                            ScoresColumns.MATCH_ID + " == ? ",
                            new String[] {match_id}
                    );
                } else {
                    values.add(match_values);
                }

                cursor.close();
            }

            // Bulk insert the scores data
            ContentValues[] cv = new ContentValues[values.size()];
            values.toArray(cv);
            mContext.getContentResolver().bulkInsert(ScoresProvider.Scores.CONTENT_URI, cv);
        }
        catch (JSONException e) {
            Log.e(LOG_TAG,e.getMessage());
        }
    }
}

