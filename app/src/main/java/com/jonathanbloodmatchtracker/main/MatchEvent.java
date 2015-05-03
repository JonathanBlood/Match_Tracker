package com.jonathanbloodmatchtracker.main;

/**
 * Allows for the construction of a match event object. Match event object consists of a
 * time, type of event that occurred in the match, the players involved and the team involved.
 *
 * @author Jonathan
 */
public class MatchEvent {
    private String time = "0.0"; // Time event occurred at.
    private String type = ""; // Type of event e.g. red card, injury, substitution.
    private String player1 = ""; // Player1 which was effected by the event.
    private String player2 = ""; // Player2 which was effected by the event.
    private String team = ""; // Team which was effected by the event.

    /**
     * Match event where one players involved e.g. goal, red card.
     *
     * @param time    - The time of the match event
     * @param type    - Event type such as goal, substitution etc
     * @param player1 - The player involved in event
     * @param team    - The team player 1 is a member off
     */
    public MatchEvent(String time, String type, String player1, String team) {
        this.time = time;
        this.type = type;
        this.player1 = player1;
        this.team = team;
    }

    /**
     * Match event where two players involved e.g. Sub.
     *
     * @param time    - Time of match event
     * @param type    - goal, substitution etc
     * @param player1 - Player 1 involved in event
     * @param player2 - Player 2 involved in event
     * @param team    - The team both players  are a member off
     */
    public MatchEvent(String time, String type, String player1, String player2, String team) {
        this.time = time;
        this.type = type;
        this.player1 = player1;
        this.player2 = player2;
        this.team = team;
    }

    /**
     * Get time event happened at.
     *
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     * Get type of the event that occurred.
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Get player1 involved in event.
     *
     * @return player1
     */
    public String getPlayer1() {
        return player1;
    }

    /**
     * Get player2 involved in event.
     *
     * @return player2
     */
    public String getPlayer2() {
        return player2;
    }

    /**
     * Get team involved in event.
     *
     * @return team
     */
    public String getTeam() {
        return team;
    }

    /**
     * Match Event string representation
     *
     * @return String representation of a Match Event
     */
    public String toString() {
        if ("".equals(player2)) {
            return "Event: " + time + "\n\t Type: " + type + "\n\t Player: " + player1 + "\n\t Team: " + team + " \n\n";
        } else {
            return "Event: " + time + "\n\t Type: " + type + "\n\t Player (in): " + player1 + "\n\t Player (out): " + player2 + "\n\t Team: " + team + " \n\n";
        }
    }

}
