package com.jonathanbloodmatchtracker.main;

/**
 * A class to hold player stats information.
 *
 * @author Jonathan
 */
public class PlayerStats {
    private String name;
    private int goals;
    private int points;
    private int tries;
    private int conversions;
    private int dropGoals;
    private int penalties;


    /**
     * Constructor: Providing player rank information through the constructor.
     *
     * @param name        - The player name
     * @param goals       - Number of goals
     * @param points      - Number of points
     * @param tries       - Number of tries
     * @param conversions - Number of conversions
     * @param dropGoals   - Number of drop goals
     * @param penalties   - Number of penalties
     */
    public PlayerStats(String name, int goals, int points, int tries, int conversions, int dropGoals, int penalties) {
        this.name = name;
        this.goals = goals;
        this.points = points;
        this.tries = tries;
        this.conversions = conversions;
        this.dropGoals = dropGoals;
        this.penalties = penalties;
    }

    /**
     * Get the name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name Player name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the goals.
     *
     * @return goals
     */
    public int getGoals() {
        return goals;
    }

    /**
     * Get the points.
     *
     * @return points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Set the points.
     *
     * @param points Number of points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Get the tries.
     *
     * @return tries
     */
    public int getTries() {
        return tries;
    }

    /**
     * Get the conversions.
     *
     * @return conversions
     */
    public int getConversions() {
        return conversions;
    }

    /**
     * Get the drop goals.
     *
     * @return drop goals
     */
    public int getDropGoals() {
        return dropGoals;
    }

    /**
     * Get the penalties.
     *
     * @return penalties
     */
    public int getPenalties() {
        return penalties;
    }
}
