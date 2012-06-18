package com.android.send;

import java.util.ArrayList;

import com.android.database.FactsDbAdapter;
import com.android.database.MatchDbAdapter;
import com.android.main.LoginActivity;
import com.android.main.MatchEvent;
import com.android.main.MatchInfo;
import com.android.management.Management;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

/**
 * This service allows users to sync all their match, facts and team data to the webserver.
 * 
 * @author Jonathan
 */
public class SyncService extends IntentService implements Runnable
{
	private int syncPoint; // The sync point stores the next match that should be uploaded.
	private int endPoint; // Last match_ID from matches db.
	private int userID;
	private MatchDbAdapter matchDBAdapter; // Communicate with matches table in db.
	private FactsDbAdapter factsDBAdapter; // Communicate with facts table in db.
	private ArrayList<MatchInfo> matches; // Arraylist to store the matches.
	private Management management; // User info from server such as login etc.
	private Handler mHandler;

	// Upload match variables.
	private SendMatches sendMatches;
	private SendFacts sendFacts;
	private SendTeams sendTeams;

	public SyncService()
	{
		super("SyncService");

	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		mHandler = new Handler();

		matchDBAdapter = new MatchDbAdapter(getApplicationContext());
		factsDBAdapter = new FactsDbAdapter(getApplicationContext());
		management = new Management();

		// Upload setup.
		sendMatches = new SendMatches();
		sendFacts = new SendFacts();
		sendTeams = new SendTeams();

	};

	@Override
	protected void onHandleIntent(Intent intent)
	{

		Thread thread = new Thread(this);
		thread.start();

	}

	public void run()
	{

		try
		{
			// Set the end point.
			matchDBAdapter.open();
			endPoint = matchDBAdapter.getLastMatchID();
			matchDBAdapter.close();

			// Get service information.
			SharedPreferences settings = getSharedPreferences("SERVICE", MODE_WORLD_WRITEABLE);
			syncPoint = settings.getInt("syncPoint", 1);

			// Get login information.
			SharedPreferences loginSettings = getSharedPreferences("LOGIN_DETAILS", MODE_WORLD_WRITEABLE);
			userID = loginSettings.getInt("userID", 0);

			// Update service to finished.
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("serviceStarted", true);
			editor.commit();

			if (userID != 0)
			{ // user is logged in check.

				// Running this as a handler allows us to display toasts.
				mHandler.post(new Runnable()
				{
					@Override
					public void run()
					{

						// Check if connected to the internet. If so then send data.
						if (management.isConnectedToInternet(getApplicationContext()))
						{
							send();
							Toast.makeText(getApplicationContext(), "All matches synced.", Toast.LENGTH_LONG).show();

						}
					}
				});
			}
			else
			{ // Not logged in. Redirect to login.
				Intent i = new Intent(SyncService.this, LoginActivity.class);
				startActivity(i);
			}
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * Method to send all the match info.
	 */
	private void send()
	{

		// Get all matches.
		matchDBAdapter.open();
		matches = matchDBAdapter.getPrevMatchesBetween(syncPoint, endPoint);
		matchDBAdapter.close();

		// Iterate through the matches.
		for (MatchInfo match : matches)
		{

			// Upload match.
			String matchIDServer = sendMatches.sendMatchesInfo(Integer.toString(userID), match.getDate(),
					match.getSport(), match.getTeam1(), match.getTeam2(), match.getVenue(), match.getType(),
					match.getTypeName(), match.getReferee());

			// Upload facts.
			factsDBAdapter.open();
			ArrayList<MatchEvent> matchEvents = factsDBAdapter.getAllFactsForGivenMatchID(match.getMatchID());
			factsDBAdapter.close();

			// Iterate through match facts and upload them.
			for (int z = 0; z < matchEvents.size(); z++)
			{
				MatchEvent matchEvt = matchEvents.get(z);
				sendFacts.sendMatchesInfo(matchIDServer, matchEvt.getType(), matchEvt.getPlayer1(),
						matchEvt.getPlayer2(), matchEvt.getTeam(), matchEvt.getTime());
			}

			// Upload team info.
			sendTeams.sendTeamInfo(matchIDServer, match.getTeam1(), match.getTeam2(), match.getSport(),
					Integer.toString(userID), match.getTypeName());
			syncPoint++;

			// Update service to finished.
			SharedPreferences settings = getSharedPreferences("SERVICE", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("syncPoint", syncPoint);
			editor.putBoolean("serviceStarted", false);
			editor.commit();
		}

	}

}
