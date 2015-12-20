package barqsoft.footballscores.API;

import android.content.Context;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import barqsoft.footballscores.R;
import barqsoft.footballscores.constant.JSON;
import barqsoft.footballscores.model.Fixture;
import barqsoft.footballscores.model.FixtureComplete;
import barqsoft.footballscores.model.FixturesResult;
import barqsoft.footballscores.model.FixturesResultTeam;
import barqsoft.footballscores.model.LeagueTable;
import barqsoft.footballscores.model.PlayersResult;
import barqsoft.footballscores.model.Soccerseason;
import barqsoft.footballscores.model.SoccerseasonsResult;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.model.TeamsResult;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by TROD on 20151216.
 *
 * Class used to call APIService with specified information.
 */
public class APIHelper {

    private APIService api;


    /*
        Retrofit 2 Header implementation tutorial here:
          https://futurestud.io/blog/retrofit-add-custom-request-header
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

    public SoccerseasonsResult getSoccerseasons(String season) throws IOException {
        return api.getSoccerseasons(season).execute().body();
    }

    public Soccerseason getSingleSoccerseason(int id) throws IOException {
        return api.getSingleSoccerseason(id).execute().body();
    }

    public TeamsResult getSingleSoccerseasonTeams(int id) throws IOException {
        return api.getSingleSoccerseasonTeams(id).execute().body();
    }

    public LeagueTable getSingleSoccerseasonLeagueTable(int id, int matchDay) throws IOException {
        return api.getSingleSoccerseasonLeagueTable(id, matchDay).execute().body();
    }

    public Fixture getSingleSoccerseasonFixtures(int id, String timeFrame, int matchDay)
            throws IOException {
        return api.getSingleSoccerseasonFixtures(id, timeFrame, matchDay).execute().body();
    }


    /*
        Fixtures API implementations
     */

    public FixturesResult getFixtures(String league, String timeFrame) throws IOException {
        return api.getFixtures(league, timeFrame).execute().body();
    }

    public FixtureComplete getSingleFixture(int id, int head2head) throws IOException {
        return api.getSingleFixture(id, head2head).execute().body();
    }


    /*
        Teams API implementations
     */

    public Team getSingleTeam(int id) throws IOException {
        return api.getSingleTeam(id).execute().body();
    }

    public FixturesResultTeam getSingleTeamFixtures(int id, String season, String timeFrame,
                                                    String venue)
            throws IOException {
        return api.getSingleTeamFixtures(id, season, timeFrame, venue).execute().body();
    }

    public PlayersResult getSingleTeamPlayers(int id) throws IOException {
        return api.getSingleTeamPlayers(id).execute().body();
    }
}