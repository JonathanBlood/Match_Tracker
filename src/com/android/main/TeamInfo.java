package com.android.main;

/**
 * A class to hold team information such as team name, wins losses, draws and total points.
 * 
 * @author Jonathan
 * 
 */
public class TeamInfo
{
	
	/**
	 * Name of the team.
	 */
	private String name;
	
	/**
	 * Number of wins the team has.
	 */
	private int wins;
	
	/**
	 * Number of losses the team has.
	 */
	private int losses;
	
	/**
	 * Number of draws the team has. 
	 */
	private int draws;
	
	/**
	 * Total points the team has.
	 */
	private int totalPoints;

	/**
	 * Constructor pass information on instantiation of object.
	 * 
	 * @param name
	 * @param wins
	 * @param losses
	 * @param draws
	 * @param totalPoints
	 */
	public TeamInfo(String name, int wins, int losses, int draws, int totalPoints)
	{
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		this.draws = draws;
		this.totalPoints = totalPoints;
	}

	/**
	 * Constructor pass information using setters.
	 */
	public TeamInfo()
	{

	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setWins(int wins)
	{
		this.wins = wins;
	}

	public void setLosses(int losses)
	{
		this.losses = losses;
	}

	public void setDraws(int draws)
	{
		this.draws = draws;
	}

	public void setTotalPoints(int totalPoints)
	{
		this.totalPoints = totalPoints;
	}

	public String getName()
	{
		return name;
	}

	public int getWins()
	{
		return wins;
	}

	public int getLosses()
	{
		return losses;
	}

	public int getDraws()
	{
		return draws;
	}

	public int getTotalPoints()
	{
		return totalPoints;
	}

}
