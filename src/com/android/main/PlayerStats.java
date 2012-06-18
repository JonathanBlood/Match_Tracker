package com.android.main;

/**
 * A class to hold player stats information.
 * 
 * @author Jonathan
 * 
 */
public class PlayerStats
{
	/**
	 * Name of the player.
	 */
	private String name;
	
	/**
	 * Number of goals the player has scored.
	 */
	private int goals;
	
	/**
	 * Number of points the player has scored.
	 */
	private int points;
	
	/**
	 * Number of tries the player has scored.
	 */
	private int tries;
	
	/**
	 * Number of conversions the player has scored.
	 */
	private int conversions;
	
	/**
	 * Number of drop goals the player has scored.
	 */
	private int dropGoals;
	
	/**
	 * Number of penalties the player has scored.
	 */
	private int penalties;

	/**
	 * Constructor: Providing player rank information through the constructor.
	 * 
	 * @param name
	 * @param goals
	 * @param points
	 * @param tries
	 * @param conversions
	 * @param dropGoals
	 * @param penalties
	 */
	public PlayerStats(String name, int goals, int points, int tries, int conversions, int dropGoals, int penalties)
	{
		this.name = name;
		this.goals = goals;
		this.points = points;
		this.tries = tries;
		this.conversions = conversions;
		this.dropGoals = dropGoals;
		this.penalties = penalties;
	}

	/**
	 * Constructor: Provides no information about player rank. Will need to use setter methods to provide information.
	 */
	public PlayerStats()
	{

	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setGoals(int goals)
	{
		this.goals = goals;
	}

	public void setPoints(int points)
	{
		this.points = points;
	}

	public void setTries(int tries)
	{
		this.tries = tries;
	}

	public void setConversions(int conversions)
	{
		this.conversions = conversions;
	}

	public void setDropGoals(int dropGoals)
	{
		this.dropGoals = dropGoals;
	}

	public void setPenalties(int penalties)
	{
		this.penalties = penalties;
	}

	public String getName()
	{
		return name;
	}

	public int getGoals()
	{
		return goals;
	}

	public int getPoints()
	{
		return points;
	}

	public int getTries()
	{
		return tries;
	}

	public int getConversions()
	{
		return conversions;
	}

	public int getDropGoals()
	{
		return dropGoals;
	}

	public int getPenalties()
	{
		return penalties;
	}
}
