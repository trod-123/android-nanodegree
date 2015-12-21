package barqsoft.footballscores.API;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.model.FixturesResult;
import barqsoft.footballscores.utilities.Utilities;
import barqsoft.footballscores.constant.JSON;
import barqsoft.footballscores.model.Fixture;
import barqsoft.footballscores.model.FixtureComplete;
import barqsoft.footballscores.model.FixturesLinks;
import barqsoft.footballscores.model.FixturesResultTeam;
import barqsoft.footballscores.model.LeagueTable;
import barqsoft.footballscores.model.PlayersResult;
import barqsoft.footballscores.model.Soccerseason;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.model.TeamsResult;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by TROD on 20151216.
 *
 * Class used to call APIService with specified information.
 */
public class APIHelper {

    private final static String LOG_TAG = APIHelper.class.getSimpleName();

    private APIService api;


    /*
        Retrofit 2 Header implementation tutorial here:
          https://futurestud.io/blog/retrofit-add-custom-request-header
        This is for API key use
     */
    public APIHelper(final Context c) {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("X-Auth-Token", c.getString(R.string.api_key))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(JSON.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        api = retrofit.create(APIService.class);
    }


    /*
        Soccerseasons API implementations
     */

    public ArrayList<Soccerseason> getSoccerseasons(String season) {
        try {
            return api.getSoccerseasons(season).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Soccerseason getSingleSoccerseason(int id) {
        try {
            return api.getSingleSoccerseason(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public TeamsResult getSingleSoccerseasonTeams(int id) {
        try {
            return api.getSingleSoccerseasonTeams(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LeagueTable getSingleSoccerseasonLeagueTable(int id, int matchDay) {
        try {
            return api.getSingleSoccerseasonLeagueTable(id, matchDay).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Fixture getSingleSoccerseasonFixtures(int id, String timeFrame, int matchDay) {
        try {
            return api.getSingleSoccerseasonFixtures(id, timeFrame, matchDay).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
        Fixtures API implementations
     */

    public List<Fixture> getFixtures(String league, String timeFrame) {
        Log.d(LOG_TAG, "In getFixtures");
        Call<FixturesResult> response = api.getFixtures(league, timeFrame);
        try {
            FixturesResult result = response.execute().body();
            if (result != null) {
                return result.getFixtures();
            } else {
                Log.d(LOG_TAG, "Fixtures was null. Redoing...");
                return getFixtures(league, timeFrame);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public FixtureComplete getSingleFixture(int id, int head2head) {
        try {
            return api.getSingleFixture(id, head2head).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
        Teams API implementations
     */

    public Team getSingleTeam(int id) {
//        Log.d(LOG_TAG, "In getSingleTeam. " + id);
        Call<Team> response = api.getSingleTeam(id);
        try {
            Team team = response.execute().body();
            if (team != null) {
                return team;
            } else {
//                Log.d(LOG_TAG, "Team was null. Redoing...");
                return getSingleTeam(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public FixturesResultTeam getSingleTeamFixtures(int id, String season, String timeFrame,
                                                    String venue) {
        try {
            return api.getSingleTeamFixtures(id, season, timeFrame, venue).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PlayersResult getSingleTeamPlayers(int id) {
        try{
            return api.getSingleTeamPlayers(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
        Synthesis API implementations
     */

    /**
     * Gets home and away team objects from fixture using fixture links
     *
     * @param fixture Fixture
     * @return List of team objects in the order of {Home, Away}
     */
    public List<Team> getHomeAwayTeamsFromSingleFixture(Fixture fixture) {
        List<Team> teams = new ArrayList<>();
        FixturesLinks links = fixture.getFixturesLinks();

        // Extract team id from team urls
        int homeTeamId = Utilities.extractId(links.getLinksHomeTeam().getHref(), Utilities.TEAM_LINK);
        int awayTeamId = Utilities.extractId(links.getLinksAwayTeam().getHref(), Utilities.TEAM_LINK);

        // Add team objects to list
        Log.d(LOG_TAG, "In long method. Home " + homeTeamId);
        teams.add(getSingleTeam(homeTeamId));
        Log.d(LOG_TAG, "In long method. Away " + awayTeamId);
        teams.add(getSingleTeam(awayTeamId));

        return teams;
    }
}