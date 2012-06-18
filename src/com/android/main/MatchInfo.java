package com.android.main;

/**
 * A class to hold match information such as the teams involved, date, type, venue, referee etc.
 ** 
 * @author Jonathan
 */
public class MatchInfo
{
	/**
	 * Stores the match identifer for the match.
	 */
	private int match_id;
	
	/**
	 * The date for the match.
	 */
	private String date;
	
	/**
	 * Stores the sporting event for the match e.g. football, rugby, gaa etc.
	 */
	private String sport;
	
	/**
	 * The first team in the match.
	 */
	private String team1;
	
	/**
	 * The second team in the match.
	 */
	private String team2;
	
	/**
	 * The venue that the match took place.
	 */
	private String venue;
	
	/**
	 * The type of match fixture it was e.g. cup, league or friendly fixture.
	 */
	private String type;
	
	/**
	 * If the match fixture is a cup or league then this stores the name.
	 * of this cup or league.
	 */
	private String type_name;
	
	/**
	 * The name of the referee for the match.
	 */
	private String referee;

	/**
	 * Constructor pass information on instantiation of object.
	 * 
	 * @param match_id
	 * @param date
	 * @param sport
	 * @param team1
	 * @param team2
	 * @param venue
	 * @param type
	 * @param type_name
	 * @param referee
	 */
	public MatchInfo(int match_id, String date, String sport, String team1, String team2, String venue, String type,
			String type_name, String referee)
	{

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
	 * Constructor pass information using setters.
	 */
	public MatchInfo(){}

	public void setMatchID(int match_id)
	{
		this.match_id = match_id;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public void setSport(String sport)
	{
		this.sport = sport;
	}

	public void setTeam1(String team1)
	{
		this.team1 = team1;
	}

	public void setTeam2(String team2)
	{
		this.team2 = team2;
	}

	public void setVenue(String venue)
	{
		this.venue = venue;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setTypeName(String type_name)
	{
		this.type_name = type_name;
	}

	public void setReferee(String referee)
	{
		this.referee = referee;
	}

	public int getMatchID()
	{
		return match_id;
	}

	public String getDate()
	{
		return date;
	}

	public String getSport()
	{
		return sport;
	}

	public String getTeam1()
	{
		return team1;
	}

	public String getTeam2()
	{
		return team2;
	}

	public String getVenue()
	{
		return venue;
	}

	public String getType()
	{
		return type;
	}

	public String getTypeName()
	{
		return type_name;
	}

	public String getReferee()
	{
		return referee;
	}

}
