package barqsoft.footballscores.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

/**
 * Created by TROD on 20151219.
 */

@Generated("org.jsonschema2pojo")
public class FixtureComplete {

    @SerializedName("fixture")
    @Expose
    private Fixture fixture;
    @SerializedName("head2head")
    @Expose
    private Head2head head2head;

    /**
     * @return The fixture
     */
    public Fixture getFixture() {
        return fixture;
    }

    /**
     * @param fixture The fixture
     */
    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    /**
     * @return The head2head
     */
    public Head2head getHead2head() {
        return head2head;
    }

    /**
     * @param head2head The head2head
     */
    public void setHead2head(Head2head head2head) {
        this.head2head = head2head;
    }

    @Generated("org.jsonschema2pojo")
    public class Head2head {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("timeFrameStart")
        @Expose
        private String timeFrameStart;
        @SerializedName("timeFrameEnd")
        @Expose
        private String timeFrameEnd;
        @SerializedName("homeTeamWins")
        @Expose
        private Integer homeTeamWins;
        @SerializedName("awayTeamWins")
        @Expose
        private Integer awayTeamWins;
        @SerializedName("draws")
        @Expose
        private Integer draws;
        @SerializedName("lastHomeWinHomeTeam")
        @Expose
        private Fixture lastHomeWinHomeTeam;
        @SerializedName("lastWinHomeTeam")
        @Expose
        private Fixture lastWinHomeTeam;
        @SerializedName("lastAwayWinAwayTeam")
        @Expose
        private Fixture lastAwayWinAwayTeam;
        @SerializedName("lastWinAwayTeam")
        @Expose
        private Fixture lastWinAwayTeam;
        @SerializedName("fixtures")
        @Expose
        private List<FixturesResult> fixtures = new ArrayList<FixturesResult>();

        /**
         * @return The count
         */
        public Integer getCount() {
            return count;
        }

        /**
         * @param count The count
         */
        public void setCount(Integer count) {
            this.count = count;
        }

        /**
         * @return The timeFrameStart
         */
        public String getTimeFrameStart() {
            return timeFrameStart;
        }

        /**
         * @param timeFrameStart The timeFrameStart
         */
        public void setTimeFrameStart(String timeFrameStart) {
            this.timeFrameStart = timeFrameStart;
        }

        /**
         * @return The timeFrameEnd
         */
        public String getTimeFrameEnd() {
            return timeFrameEnd;
        }

        /**
         * @param timeFrameEnd The timeFrameEnd
         */
        public void setTimeFrameEnd(String timeFrameEnd) {
            this.timeFrameEnd = timeFrameEnd;
        }

        /**
         * @return The homeTeamWins
         */
        public Integer getHomeTeamWins() {
            return homeTeamWins;
        }

        /**
         * @param homeTeamWins The homeTeamWins
         */
        public void setHomeTeamWins(Integer homeTeamWins) {
            this.homeTeamWins = homeTeamWins;
        }

        /**
         * @return The awayTeamWins
         */
        public Integer getAwayTeamWins() {
            return awayTeamWins;
        }

        /**
         * @param awayTeamWins The awayTeamWins
         */
        public void setAwayTeamWins(Integer awayTeamWins) {
            this.awayTeamWins = awayTeamWins;
        }

        /**
         * @return The draws
         */
        public Integer getDraws() {
            return draws;
        }

        /**
         * @param draws The draws
         */
        public void setDraws(Integer draws) {
            this.draws = draws;
        }

        /**
         * @return The lastHomeWinHomeTeam
         */
        public Fixture getLastHomeWinHomeTeam() {
            return lastHomeWinHomeTeam;
        }

        /**
         * @param lastHomeWinHomeTeam The lastHomeWinHomeTeam
         */
        public void setLastHomeWinHomeTeam(Fixture lastHomeWinHomeTeam) {
            this.lastHomeWinHomeTeam = lastHomeWinHomeTeam;
        }

        /**
         * @return The lastWinHomeTeam
         */
        public Fixture getLastWinHomeTeam() {
            return lastWinHomeTeam;
        }

        /**
         * @param lastWinHomeTeam The lastWinHomeTeam
         */
        public void setLastWinHomeTeam(Fixture lastWinHomeTeam) {
            this.lastWinHomeTeam = lastWinHomeTeam;
        }

        /**
         * @return The lastAwayWinAwayTeam
         */
        public Fixture getLastAwayWinAwayTeam() {
            return lastAwayWinAwayTeam;
        }

        /**
         * @param lastAwayWinAwayTeam The lastAwayWinAwayTeam
         */
        public void setLastAwayWinAwayTeam(Fixture lastAwayWinAwayTeam) {
            this.lastAwayWinAwayTeam = lastAwayWinAwayTeam;
        }

        /**
         * @return The lastWinAwayTeam
         */
        public Fixture getLastWinAwayTeam() {
            return lastWinAwayTeam;
        }

        /**
         * @param lastWinAwayTeam The lastWinAwayTeam
         */
        public void setLastWinAwayTeam(Fixture lastWinAwayTeam) {
            this.lastWinAwayTeam = lastWinAwayTeam;
        }

        /**
         * @return The fixtures
         */
        public List<FixturesResult> getFixtures() {
            return fixtures;
        }

        /**
         * @param fixtures The fixtures
         */
        public void setFixtures(List<FixturesResult> fixtures) {
            this.fixtures = fixtures;
        }
    }
}