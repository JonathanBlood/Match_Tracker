package com.android.management;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * This class deals with the sending and receiving data to and from PHP files (located in ../Management/) to and from
 * the android application. SETUP: Edit variable below called webPage with link to the folder on your server.
 * 
 * @author Jonathan
 * 
 */
public class Management
{

	/**
	 *  Fill in  webPage with link to the All folder on your server.
	 */
	private String webPage = "http://csisan.ucd.ie/jonathan/Match%20Tracker/Management/";

	/**
	 *  File name of the PHP file that is to be used by the method.
	 */
	private String fileName;
	
	/**
	 * Tokens we want to delimit. Used when parsing content.
	 */
	String delims = " ,\t\n\"\\;{}[]\\()<>&^%$@!+/*~=";

	/**
	 * Constructor.
	 */
	public Management()
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
					output = st.nextToken();
			}
			is.close();

		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		return output;
	}

	/**
	 * Login from webserver. If valid userID then userID won't be zero. If invalid return the userID as zero.
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public int login(String userName, String password)
	{
		fileName = "IsLoginCorrect.php";
		int userID;

		// Add arguments to arrayList<NameValuePairs> so we can encode the data and send it on.
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("userName", userName));
		nameValuePairs.add(new BasicNameValuePair("password", password));

		String reply = this.manageData(fileName, nameValuePairs);
		try
		{
			userID = Integer.parseInt(reply);
		}
		catch (NumberFormatException e)
		{
			userID = 0;
		}
		return userID;
	}

	/**
	 * Method to see if device has any access to the Internet.
	 * 
	 * @return boolean true if connected, otherwise false.
	 */
	public boolean isConnectedToInternet(Context context)
	{
		try
		{
			// Network is available but check if we can get access from the
			// network.
			URL url = new URL("http://www.Google.com/");
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			urlc.setRequestProperty("Connection", "close");
			urlc.setConnectTimeout(2000); 
			urlc.connect();

			if (urlc.getResponseCode() == 200) // Successful response.
			{ 
				return true;
			}
			else
			{
				Toast.makeText(context, "No connection to internet.", Toast.LENGTH_LONG).show();
				Log.d("NO INTERNET", "NO INTERNET");
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Toast.makeText(context, "No connection to internet.", Toast.LENGTH_LONG).show();
		return false;
	}
}