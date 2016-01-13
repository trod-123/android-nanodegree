package com.thirdarm.footballscores.API;

import java.util.ArrayList;

import com.thirdarm.footballscores.model.Fixture;
import com.thirdarm.footballscores.model.FixtureComplete;
import com.thirdarm.footballscores.model.FixturesResult;
import com.thirdarm.footballscores.model.FixturesResultTeam;
import com.thirdarm.footballscores.model.LeagueTable;
import com.thirdarm.footballscores.model.PlayersResult;
import com.thirdarm.footballscores.model.Soccerseason;
import com.thirdarm.footballscores.model.Team;
import com.thirdarm.footballscores.model.TeamsResult;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by TROD on 20151216.
 *
 * Service used to collect and parse JSON data from a server
 * Used for both Asynchronous and Synchronous calls
 *  (uses Retrofit 2.0, by Jake Wharton [Square, Inc., 2015]: http://square.github.io/retrofit/)
 *
 * NOTE: football-data api only allows for 50 requests per minute...
 */
public interface APIService {

    String URL_BASE = "http://api.football-data.org/v1/";

    /*
     * Base url: "http://api.football-data.org/v1/fixtures"
     */

    class PARAMS {
        public static final String ID = "id"; // Integer /[0-9]+/
        public static final String MATCHDAY = "matchday"; // Integer /[1-4]*[0-9]*/
        public static final String SEASON = "season"; // String /\d\d\d\d/
        public static final String HEAD2HEAD = "head2head"; // 	Integer /[0-9]+/
        public static final String VENUE = "venue"; // String /away|home/
        public static final String LEAGUE = "league"; // (comma separated) String /[\w\d]{2,4}(,[\w\d]{2,4})*/
        public static final String TIMEFRAME = "timeFrame"; // self-defined p|n[1-9]{1,2}
    }

    /*
        Soccerseasons API methods
     */

    /**
     * Gets soccerseasons information
     *   e.g. http://api.football-data.org/v1/soccerseasons
     */
    @GET("soccerseasons")
    Call<ArrayList<Soccerseason>> getSoccerseasons(@Query(PARAMS.SEASON) String season);

    /**
     * Gets a particular soccerseason
     *   e.g. http://api.football-data.org/v1/soccerseasons/{id}
     */
    @GET("soccerseasons/{id}")
    Call<Soccerseason> getSingleSoccerseason(@Path(PARAMS.ID) int id);

    /**
     * Gets information about a soccerseason's teams
     *   e.g. http://api.football-data.org/v1/soccerseasons/{id}/teams
     */
    @GET("soccerseasons/{id}/teams")
    Call<TeamsResult> getSingleSoccerseasonTeams(@Path(PARAMS.ID) int id);

    /**
     * Gets information about a soccerseason's current league table / standing
     *   e.g. http://api.football-data.org/v1/soccerseasons/{id}/leagueTable
     */
    @GET("soccerseasons/{id}/leagueTable")
    Call<LeagueTable> getSingleSoccerseasonLeagueTable(@Path(PARAMS.ID) int id,
                                                       @Query(PARAMS.MATCHDAY) int matchDay);

    /**
     * Gets information about a soccerseason's fixtures
     *   e.g. http://api.football-data.org/v1/soccerseasons/{id}/fixtures
     */
    @GET("soccerseasons/{id}/fixtures")
    Call<Fixture> getSingleSoccerseasonFixtures(@Path(PARAMS.ID) int id,
                                                @Query(PARAMS.TIMEFRAME) String timeFrame,
                                                @Query(PARAMS.MATCHDAY) int matchDay);


    /*
        Fixtures API methods
     */

    /**
     * Gets fixture information
     *   e.g. http://api.football-data.org/v1/fixtures?timeFrame=n2
     */
    @GET("fixtures")
    Call<FixturesResult> getFixtures(@Query(PARAMS.LEAGUE) String league,
                                     @Query(PARAMS.TIMEFRAME) String timeFrame);

    /**
     * Gets a particular fixture
     *   e.g. http://api.football-data.org/v1/fixtures/{id}
     */
    @GET("fixtures/{id}")
    Call<FixtureComplete> getSingleFixture(@Path(PARAMS.ID) int id,
                                           @Query(PARAMS.HEAD2HEAD) int head2head);


    /*
        Teams API methods
     */

    /**
     * Gets information about a single team
     *   e.g. http://api.football-data.org/v1/teams/{id}
     */
    @GET("teams/{id}")
    Call<Team> getSingleTeam(@Path("id") int id);

    /**
     * Gets all fixtures for a particular team
     *   e.g. http://api.football-data.org/v1/teams/{id}/fixtures
     */
    @GET("teams/{id}/fixtures")
    Call<FixturesResultTeam> getSingleTeamFixtures(@Path(PARAMS.ID) int id,
                                                   @Query(PARAMS.SEASON) String season,
                                                   @Query(PARAMS.TIMEFRAME) String timeFrame,
                                                   @Query(PARAMS.VENUE) String venue);

    /**
     * Gets all players for a particular team
     *   e.g. http://api.football-data.org/v1/teams/{id}/players
     */
    @GET("teams/{id}/players")
    Call<PlayersResult> getSingleTeamPlayers(@Path(PARAMS.ID) int id);
}
