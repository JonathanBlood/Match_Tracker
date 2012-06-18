package com.android.main;

/**
 * Allows for the construction of a match event object. Match event object consists of a time, type of event that
 * occurred in the match, the players involved and the team involved.
 * 
 * @author Jonathan
 * 
 */
public class MatchEvent
{
	/**
	 * Stores the time a match event occurred at as a String.
	 */
	private String time = "0.0"; 
	
	/**
	 * The type of match event e.g. red card, injury, substitution.
	 */
	private String type = ""; 
	
	/**
	 * First player which was effected by the match event.
	 */
	private String player1 = ""; 
	
	/**
	 * Second player which was effected by the match event.
	 */
	private String player2 = ""; 
	
	/**
	 * The team which was effected by the match event.
	 */
	private String team = ""; 
	
	/**
	 * A note as a match event.
	 */
	private String note = "";
	
	/**
	 * Constructor: Match event were one player was involved.
	 * @param time
	 * @param type
	 * @param player1
	 * @param team
	 */
	public MatchEvent(String time, String type, String player1, String team)
	{
		this.time = time;
		this.type = type;
		this.player1 = player1;
		this.team = team;
	}
	
	/**
	 * Constructor: Match event were one player was involved.
	 * @param time
	 * @param type
	 * @param note
	 */
	public MatchEvent(String time, String type, String note)
	{
		this.time = time;
		this.type = type;
		this.note = note;
	}

	/**
	 * Constructor: Match event were two players was involved e.g. Substitution.
	 * @param time
	 * @param type
	 * @param player1
	 * @param player2
	 * @param team
	 */
	public MatchEvent(String time, String type, String player1, String player2, String team)
	{
		this.time = time;
		this.type = type;
		this.player1 = player1;
		this.player2 = player2;
		this.team = team;
	}

	public String getTime()
	{
		return time;
	}

	public String getType()
	{
		return type;
	}

	public String getPlayer1()
	{
		return player1;
	}

	public String getPlayer2()
	{
		return player2;
	}

	public String getTeam()
	{
		return team;
	}
	
	public String getNote()
	{
		return note;
	}

	public String toString()
	{
		if(type.equals("Note")) 
			return time + ": " + note +"\n\n";
		else if (player1.equals("") && player2.equals("")) 
			return type + ": " + time + ", " + team + " \n\n";
		else if (player1.equals(""))
			return type + ": " + time + ", " + player2 + ", " + team + " \n\n";
		else if (player2.equals("")) 
			return type + ": " + time + ", " + player1 + ", " + team + " \n\n";
		else
			return type + ": " + time + ", " + player1 + ", " + player2 + ", " + team + " \n\n";
	}

}
