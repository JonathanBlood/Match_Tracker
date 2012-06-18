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
 * This class deals with the sending of match facts data to PHP files (located in ../MatchInformation/) to and from the
 * Android application. SETUP: Edit variable below called webPage with link to the folder on your server.
 * 
 * @author Jonathan
 * 
 */
public class SendFacts
{

	// FILL IN webPage with link to the All folder on your server.
	private String webPage = "http://csisan.ucd.ie/jonathan/Match%20Tracker/MatchInformation/";

	// File name of the PHP file that is to be used by the method.
	private String fileName;
	String delims = " ,\t\n\"\\;{}[]\\()<>&^%$@!+/*~=";

	/**
	 * Constructor.
	 */
	public SendFacts()
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

	public String sendMatchesInfo(String matchID, String type, String player1, String player2, String team, String time)
	{
		fileName = "CreateFact.php";

		// Add arguments to arrayList<NameValuePairs> so we can encode the data and send it on.
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("Match_ID", matchID));
		nameValuePairs.add(new BasicNameValuePair("Type", type));
		nameValuePairs.add(new BasicNameValuePair("Player1", player1));
		nameValuePairs.add(new BasicNameValuePair("Player2", player2));
		nameValuePairs.add(new BasicNameValuePair("Team", team));
		nameValuePairs.add(new BasicNameValuePair("Time", time));

		String reply = this.manageData(fileName, nameValuePairs);
		return reply;
	}

}