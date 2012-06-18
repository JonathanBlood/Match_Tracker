package com.android.statistics;

import java.util.ArrayList;

import com.android.database.FactsDbAdapter;
import com.android.main.Actionbar;
import com.android.main.LoginActivity;
import com.android.main.MainMenuActivity;
import com.android.main.MatchEvent;
import com.android.management.Management;
import com.android.send.SendFacts;
import com.android.send.SendMatches;
import com.android.send.SendTeams;
import com.android.send.SyncService;
import com.jonathanblood.main.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class to show Match information in an activity.
 * 
 * @author Jonathan
 * 
 */
public class MatchActivity extends Activity
{

	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionBarOperations = new Actionbar(this);
	
	// Login information
	private boolean loggedIn = false;
	private int userID = 0;

	private int match_id;
	private String date, sport, team1, team2, venue, type, type_name, referee, shareMessage = "", subject = "";
	private FactsDbAdapter factsDbAdapter;
	private TextView team1NameTV, team2NameTV, team1ScoreTV, team2ScoreTV, matchIDTV, dateTV, sportTV, venueTV,
	refereeTV, typeTV, typeNameTV, matchEventsTV;
	private Button uploadMatchBN;
	private ImageButton emailBN;

	// Upload match variables.
	private SendMatches sendMatches;
	private SendFacts sendFacts;
	private SendTeams sendTeams;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_match);

		// Actionbar Listener.
		ActionBarClickListener actionBClickListener = new ActionBarClickListener();
		findViewById(R.id.home_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.login_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.sync_button).setOnClickListener(actionBClickListener);

		//Handle how the actionbar buttons are displayed.
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());

		// Retrieve Match Parameters.
		Intent getParameters = getIntent();
		match_id = getParameters.getIntExtra("match_id", 0);
		date = getParameters.getStringExtra("date");
		sport = getParameters.getStringExtra("sport");
		team1 = getParameters.getStringExtra("team1");
		team2 = getParameters.getStringExtra("team2");
		venue = getParameters.getStringExtra("venue");
		type = getParameters.getStringExtra("type");
		type_name = getParameters.getStringExtra("type_name");
		referee = getParameters.getStringExtra("referee");

		// Upload setup.
		sendMatches = new SendMatches();
		sendFacts = new SendFacts();
		sendTeams = new SendTeams();

		// DatabaseAdapter for match events.
		factsDbAdapter = new FactsDbAdapter(this);

		// Textview setup.
		team1NameTV = (TextView) this.findViewById(R.id.team1NameStatMatch);
		team2NameTV = (TextView) this.findViewById(R.id.team2NameStatMatch);
		team1ScoreTV = (TextView) this.findViewById(R.id.team1ScoreStatMatch);
		team2ScoreTV = (TextView) this.findViewById(R.id.team2ScoreStatMatch);
		matchIDTV = (TextView) this.findViewById(R.id.matchIDStatMatch);
		dateTV = (TextView) this.findViewById(R.id.dateStatMatch);
		sportTV = (TextView) this.findViewById(R.id.sportStatMatch);
		venueTV = (TextView) this.findViewById(R.id.venueStatMatch);
		refereeTV = (TextView) this.findViewById(R.id.refereeStatMatch);
		typeTV = (TextView) this.findViewById(R.id.typeStatMatch);
		typeNameTV = (TextView) this.findViewById(R.id.typeNameStatMatch);
		matchEventsTV = (TextView) this.findViewById(R.id.matchEventsStatMatch);

		// Button setup.
		uploadMatchBN = (Button) this.findViewById(R.id.uploadMatchBN);
		this.uploadMatchesButton();
		
		emailBN = (ImageButton) this.findViewById(R.id.emailBN);
		this.emailButton();

		// Set text to textview.
		team1NameTV.setText(team1);
		team2NameTV.setText(team2);
		team1ScoreTV.setText(calculateScore(team1));
		team2ScoreTV.setText(calculateScore(team2));
		matchIDTV.setText("" + match_id);
		dateTV.setText(date);
		sportTV.setText(sport);
		venueTV.setText(venue);
		refereeTV.setText(referee);
		typeTV.setText(type);
		typeNameTV.setText(type_name);
		
		shareMessage = team1 + " (" + calculateScore(team1) + ")" +
				" : " + "(" + calculateScore(team2) + ") "+ team2 + "\n" +
				"MATCH ID:			"+ match_id + "\n" +
				"DATE:				"+ date + "\n" +
				"SPORT:				"+ sport + "\n" +
				"VENUE:				"+ venue + "\n" +
				"REFEREE:			"+ referee + "\n" +
				"TYPE:				"+ type + "\n" +
				"LEAGUE/CUP NAME:	"+ type_name + "\n";
		
		subject = sport+": "+ team1 + " VS "+ team2 + date;
		
		String matchFactsStr = "";
		factsDbAdapter.open();
		ArrayList<MatchEvent> matchEvents = factsDbAdapter.getAllFactsForGivenMatchID(match_id);
		factsDbAdapter.close();
		for (int i = 0; i < matchEvents.size(); i++)
		{
			MatchEvent matchEvt = matchEvents.get(i);
			matchFactsStr += matchEvt.toString();
		}
		matchEventsTV.setText(matchFactsStr);
		shareMessage += matchFactsStr;

	}

	/** Called when activity is called back into foreground. */
	@Override
	public void onResume()
	{
		super.onResume();

		//Handle how the actionbar buttons are displayed.
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
	}

	/** Called when activity is called back into foreground. */
	@Override
	public void onRestart()
	{
		super.onRestart();
		//Handle how the actionbar buttons are displayed.
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());

	}

	/**
	 * Upload matches onclick method.
	 */
	private void uploadMatchesButton()
	{
		uploadMatchBN.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				if (userID != 0)
				{ // User is logged in.

					Management management = new Management();

					// Check if phone has any access to the internet.
					if (management.isConnectedToInternet(getApplicationContext()))
					{

						// Upload match..
						String matchIDServer = sendMatches.sendMatchesInfo(Integer.toString(userID), date, sport,
								team1, team2, venue, type, type_name, referee);

						// Upload facts.
						factsDbAdapter.open();
						ArrayList<MatchEvent> matchEvents = factsDbAdapter.getAllFactsForGivenMatchID(match_id);
						factsDbAdapter.close();

						// Iterate through match facts and upload them.
						for (int z = 0; z < matchEvents.size(); z++)
						{
							MatchEvent matchEvt = matchEvents.get(z);
							sendFacts.sendMatchesInfo(matchIDServer, matchEvt.getType(), matchEvt.getPlayer1(),
									matchEvt.getPlayer2(), matchEvt.getTeam(), matchEvt.getTime());
						}

						// Upload team info.
						sendTeams.sendTeamInfo(matchIDServer, team1, team2, sport, Integer.toString(userID), type_name);

						Toast.makeText(getApplicationContext(), "Match uploaded.", Toast.LENGTH_LONG).show();
					}
				}
				else
				{ // Not logged in. Redirect to login.
					Intent i = new Intent(MatchActivity.this, LoginActivity.class);
					startActivity(i);
				}
			}
		});
	}
	
	private void emailButton()
	{
		emailBN.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
				intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				startActivity(Intent.createChooser(intent, "Share via:"));

			}
		});
	}

	/**
	 * Calculate a teams score.
	 * 
	 * @param aTeam
	 * @return
	 */
	private String calculateScore(String aTeam)
	{
		String score = "";
		factsDbAdapter.open();
		if ("Football".equals(sport))
		{ // If sport is football set score.
			score = "" + factsDbAdapter.countTypesWithMatchID(match_id, "Goal", aTeam);

			// If sport is gaelic or hurling set score.
		}
		else if ("Gaelic".equals(sport) || "Hurling".equals(sport))
		{

			int goal = factsDbAdapter.countTypesWithMatchID(match_id, "Goal", aTeam);
			int point = factsDbAdapter.countTypesWithMatchID(match_id, "Point", aTeam);

			score = goal + ":" + point;

		}
		else if ("Rugby".equals(sport))
		{ // If sport is rugby set
			// score.
			int totalScore = 0;
			int trySc = 0;
			int conversion = 0;
			int penalty = 0;
			int dropGoal = 0;

			// Team1 get total score.
			trySc = factsDbAdapter.countTypesWithMatchID(match_id, "Try", aTeam);
			conversion = factsDbAdapter.countTypesWithMatchID(match_id, "Conversion", aTeam);
			penalty = factsDbAdapter.countTypesWithMatchID(match_id, "Penalty", aTeam);
			dropGoal = factsDbAdapter.countTypesWithMatchID(match_id, "Drop Goal", aTeam);
			totalScore = (trySc * 5) + (conversion * 2) + (penalty * 3) + (dropGoal * 3);
			score = "" + totalScore;

		}
		factsDbAdapter.close();
		return score;
	}

	/**
	 * Actionbar onClick Listener.
	 * 
	 * @author Jonathan
	 * 
	 */
	private class ActionBarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent i = null;
			switch (v.getId())
			{
			case R.id.home_button:
				i = new Intent(MatchActivity.this, MainMenuActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				break;
			case R.id.login_button:
				ImageButton loginButton = (ImageButton) findViewById(R.id.login_button);
				SharedPreferences settings = getSharedPreferences("LOGIN_DETAILS", 0);
				loggedIn = settings.getBoolean("loggedIn", false);

				if (loggedIn)
				{ // If logged in then log them out.

					// Set login state to logged out and save that state.
					SharedPreferences.Editor editor = settings.edit();

					// Add login data we would like to save.
					editor.putBoolean("loggedIn", false);
					editor.putInt("userID", 0);
					editor.commit();

					// Logged out image set.
					loginButton.setImageResource(R.drawable.login);
					Toast.makeText(getApplicationContext(), "Logged out.", Toast.LENGTH_LONG).show();
				}
				else
				{
					i = new Intent(MatchActivity.this, LoginActivity.class);
				}
				break;
			case R.id.sync_button:
				// Sync button.
				SharedPreferences serviceSsettings = getSharedPreferences("SERVICE_DETAILS", MODE_WORLD_WRITEABLE);
				ImageButton syncButton = (ImageButton) findViewById(R.id.sync_button);

				// If service is not running.
				if (!serviceSsettings.getBoolean("serviceStarted", false))
				{
					syncButton.setImageResource(R.drawable.sync_selected);
					Intent syncIntent = new Intent(MatchActivity.this, SyncService.class);
					startService(syncIntent);
				}
				break;
			}
			if (i != null)
			{
				startActivity(i);
			}
		}
	}

}
