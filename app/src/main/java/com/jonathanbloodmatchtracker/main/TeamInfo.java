package com.jonathanbloodmatchtracker.main;

/**
 * A class to hold team information such as team name, wins losses, draws and total points.
 *
 * @author Jonathan
 */
public class TeamInfo {
    private String name;
    private int wins;
    private int losses;
    private int draws;
    private int totalPoints;

    /**
     * Constructor pass information on instantiation of object.
     *
     * @param name name
     * @param wins wins
     * @param losses losses
     * @param draws draws
     * @param totalPoints total points
     */
    public TeamInfo(String name, int wins, int losses, int draws, int totalPoints) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.totalPoints = totalPoints;
    }

    /**
     * Get name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the team.
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get number of wins.
     *
     * @return wins
     */
    public int getWins() {
        return wins;
    }

    /**
     * Set the wins.
     *
     * @param wins wins
     */
    public void setWins(int wins) {
        this.wins = wins;
    }

    /**
     * Get number of losses.
     *
     * @return losses
     */
    public int getLosses() {
        return losses;
    }

    /**
     * Set the losses.
     *
     * @param losses losses
     */
    public void setLosses(int losses) {
        this.losses = losses;
    }

    /**
     * Get number of draws.
     *
     * @return draws
     */
    public int getDraws() {
        return draws;
    }

    /**
     * Set the draws.
     *
     * @param draws draws
     */
    public void setDraws(int draws) {
        this.draws = draws;
    }

    /**
     * Get total points.
     *
     * @return total points
     */
    public int getTotalPoints() {
        return totalPoints;
    }

}
