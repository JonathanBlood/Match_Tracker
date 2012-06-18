package com.android.send;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

/**
 * This class deals with the sending of Matches data to PHP files (located in ../MatchInformation/) to and from the
 * Android application. SETUP: Edit variable below called webPage with link to the folder on your server.
 * 
 * @author Jonathan
 * 
 */
public class SendMatches
{

	// FILL IN webPage with link to the All folder on your server.
	private String webPage = "http://csisan.ucd.ie/jonathan/Match%20Tracker/MatchInformation/";

	// File name of the PHP file that is to be used by the method.
	private String fileName;
	String delims = " ,\t\n\"\\;{}[]\\()<>&^%$@!+/*~=";

	/**
	 * Constructor.
	 */
	public SendMatches()
	{
	}

	/**
	 * Method to pass data on to given php file. Data being passed stored in a ArrayList of NameValuePairs.
	 * 
	 * @param file
	 * @param data
	 */
	private String manageData(String file, ArrayList<NameValuePair> data)
	{
		InputStream is = null;
		String line = null;
		String output = null;

		// Connect and obtain data via HTTP.
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(webPage + file);
			httppost.setEntity(new UrlEncodedFormEntity(data));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error in http connection " + e.toString());
		}

		// Parse data
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));

			while ((line = reader.readLine()) != null)
			{
				// Parse data into tokens and removing unimportant tokens.
				StringTokenizer st = new StringTokenizer(line, delims, false);

				if (st.hasMoreTokens())
				{
					output = st.nextToken();
				}
			}
			is.close();

			// Log output of data in LogCat.
			Log.d("DATA", "DATA:" + output);

		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		return output;
	}

	/**
	 * Send match information to server.
	 * 
	 * @param userID
	 * @param date
	 * @param sport
	 * @param team1
	 * @param team2
	 * @param venue
	 * @param type
	 * @param typeName
	 * @param referee
	 * @return
	 */
	public String sendMatchesInfo(String userID, String date, String sport, String team1, String team2, String venue,
			String type, String typeName, String referee)
	{
		fileName = "CreateMatch.php";

		// Add arguments to arrayList<NameValuePairs> so we can encode the data and send it on.
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(9);
		nameValuePairs.add(new BasicNameValuePair("User_ID", userID));
		nameValuePairs.add(new BasicNameValuePair("Date", date));
		nameValuePairs.add(new BasicNameValuePair("Sport", sport));
		nameValuePairs.add(new BasicNameValuePair("Team1", team1));
		nameValuePairs.add(new BasicNameValuePair("Team2", team2));
		nameValuePairs.add(new BasicNameValuePair("Venue", venue));
		nameValuePairs.add(new BasicNameValuePair("Type", type));
		nameValuePairs.add(new BasicNameValuePair("Type_Name", typeName));
		nameValuePairs.add(new BasicNameValuePair("Referee", referee));

		String reply = this.manageData(fileName, nameValuePairs);
		return reply;
	}

}
