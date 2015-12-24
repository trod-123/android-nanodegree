package com.thirdarm.footballscores.constant;

/**
 * Created by TROD on 20151216.
 */
public class JSON {

    // Url bases and segments
    public static final String URL_BASE = "http://api.football-data.org/v1/";

    // League codes (may need to be updated for next season, Fall 2016)
    public static final String BUNDESLIGA1 = "394"; // "1. Bundesliga 2015/16"
    public static final String BUNDESLIGA2 = "395"; // "2. Bundesliga 2015/16"
    public static final String LIGUE1 = "396"; // "Ligue 1 2015/16"
    public static final String LIGUE2 = "397"; // "Ligue 2 2015/16"
    public static final String PREMIER_LEAGUE = "398"; // "Premier League 2015/16"
    public static final String PRIMERA_DIVISION = "399"; // "Primera Division 2015/16"
    public static final String SEGUNDA_DIVISION = "400"; // "Segunda Division 2015/16"
    public static final String SERIE_A = "401"; // "Serie A 2015/16"
    public static final String PRIMERA_LIGA = "402"; // "Primeira Liga 2015/16"
    public static final String Bundesliga3 = "403"; // "3. Bundesliga 2015/16"
    public static final String EREDIVISIE = "404"; // "Eredivisie 2015/16"
    public static final String CHAMPIONS = "405"; // "Champions League 2015/16"

    // For processing the JSON fields within /fixtures
    public static final String SEASON_LINK = "http://api.football-data.org/v1/soccerseasons/";
    public static final String MATCH_LINK = "http://api.football-data.org/v1/fixtures/";
    public static final String FIXTURES = "fixtures";
    public static final String LINKS = "_links";
    public static final String SOCCER_SEASON = "soccerseason";
    public static final String SELF = "self";
    public static final String MATCH_DATE = "date";
    public static final String HOME_TEAM = "homeTeamName";
    public static final String AWAY_TEAM = "awayTeamName";
    public static final String RESULT = "result";
    public static final String HOME_GOALS = "goalsHomeTeam";
    public static final String AWAY_GOALS = "goalsAwayTeam";
    public static final String MATCH_DAY = "matchday";
}
