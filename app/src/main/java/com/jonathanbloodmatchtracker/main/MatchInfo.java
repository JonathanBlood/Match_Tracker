package com.jonathanbloodmatchtracker.main;

/**
 * A class to hold match information such as the teams involved, date, type, venue, referee etc.
 * * @author Jonathan
 */
public class MatchInfo {
    private int match_id;
    private String date;
    private String sport;
    private String team1;
    private String team2;
    private String venue;
    private String type;
    private String type_name;
    private String referee;

    /**
     * Constructor pass information on instantiation of object.
     *
     * @param match_id  - Match ID
     * @param date      - Date match took place
     * @param sport     - Football, hurling, gaelic, rugby
     * @param team1     - First Team
     * @param team2     - Second Team
     * @param venue     - The place the  match took place
     * @param type      - Friendly, cup or league game
     * @param type_name - Type name
     * @param referee   - The name of the Referee
     */
    public MatchInfo(int match_id, String date, String sport, String team1, String team2, String venue, String type, String type_name, String referee) {

        this.match_id = match_id;
        this.date = date;
        this.sport = sport;
        this.team1 = team1;
        this.team2 = team2;
        this.venue = venue;
        this.type = type;
        this.type_name = type_name;
        this.referee = referee;
    }

    /**
     * Get the match ID.
     *
     * @return match ID
     */
    public int getMatchID() {
        return match_id;
    }

    /**
     * Get the date.
     *
     * @return date of match
     */
    public String getDate() {
        return date;
    }

    /**
     * Get the sport.
     *
     * @return sport
     */
    public String getSport() {
        return sport;
    }

    /**
     * Set the sport.
     *
     * @param sport - Football, hurling, gaelic, rugby
     */
    public void setSport(String sport) {
        this.sport = sport;
    }

    /**
     * Get the team1.
     *
     * @return team1
     */
    public String getTeam1() {
        return team1;
    }

    /**
     * Set the team1.
     *
     * @param team1 - First Team
     */
    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    /**
     * Get the team2.
     *
     * @return team2
     */
    public String getTeam2() {
        return team2;
    }

    /**
     * Set the team2.
     *
     * @param team2 - Second Team
     */
    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    /**
     * Get the venue.
     *
     * @return venue
     */
    public String getVenue() {
        return venue;
    }

    /**
     * Set the venue.
     *
     * @param venue - Place event took place
     */
    public void setVenue(String venue) {
        this.venue = venue;
    }

    /**
     * Get the type.
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type.
     *
     * @param type - Cop, Friendly or League
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the type name.
     *
     * @return type name
     */
    public String getTypeName() {
        return type_name;
    }

    /**
     * Get the referee.
     *
     * @return referee name
     */
    public String getReferee() {
        return referee;
    }

    /**
     * Set the referee.
     *
     * @param referee - Referee Name
     */
    public void setReferee(String referee) {
        this.referee = referee;
    }

}
