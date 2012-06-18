package com.android.record;

import java.util.ArrayList;

import com.android.database.MatchDbAdapter;
import com.android.database.TeamDbAdapter;
import com.android.main.Actionbar;
import com.android.main.LoginActivity;
import com.android.main.MainMenuActivity;
import com.android.send.SyncService;
import com.jonathanblood.main.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Activity to allow the user to enter pre match information such as teams, venues ect.
 * 
 * @author Jonathan
 * 
 */
public class RugbyPreMatchInfoActivity extends Activity
{
	
	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionBarOperations = new Actionbar(this);

	// Login information
	private boolean loggedIn = false;

	private RadioButton friendlyRadioBN, leagueRadioBN, cupRadioBN;
	private ArrayAdapter<String> teamAdapter, venueAdapter, refereeAdapter, leagueCupNameAdapter;
	private AutoCompleteTextView team1AutoTV, team2AutoTV, venueAutoTV, refereeNameAutoET, leagueCupNameAutoTV;
	private EditText numOfPlayersET, minutesPerHalfET, tryValueET, conversionValueET, penaltyValueET, dropGoalValueET;
	private Button createMatchBN;
	private int numOfPlayers, minutesPerHalf, tryValue, conversionValue, penaltyValue, dropGoalValue;
	private String team1, team2, venue, refereeName, type, leagueCupName;

	private MatchDbAdapter matchDbAdapter; // Database adapter for Match table.
	private TeamDbAdapter teamDbAdapter; // Database adapter for Team table.

	// For storing suggestions for input.
	private String[] teamsArr;
	private String[] refereesArr;
	private String[] venueArr;
	private String[] leagueCupNameArr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rugby_pre_match_info);

		// Actionbar Listener.
		ActionBarClickListener actionBClickListener = new ActionBarClickListener();
		findViewById(R.id.home_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.login_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.sync_button).setOnClickListener(actionBClickListener);

		//Handle how the actionbar buttons are displayed.
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());

		// Database adapter setup.
		matchDbAdapter = new MatchDbAdapter(this);
		teamDbAdapter = new TeamDbAdapter(this);

		// Get info from database for autocomplete input suggestions.
		// Team suggestions.
		teamDbAdapter.open();
		ArrayList<String> teamsAL = teamDbAdapter.getColumnValuesStr(TeamDbAdapter.KEY_NAME, false);
		teamsArr = teamsAL.toArray(new String[teamsAL.size()]);
		teamDbAdapter.close();

		matchDbAdapter.open();
		// Referee suggestions.
		ArrayList<String> refAL = matchDbAdapter.getColumnValues(MatchDbAdapter.KEY_REFEREE, false);
		refereesArr = refAL.toArray(new String[refAL.size()]);
		// Venue suggestions.
		ArrayList<String> venueAL = matchDbAdapter.getColumnValues(MatchDbAdapter.KEY_VENUE, false);
		venueArr = venueAL.toArray(new String[venueAL.size()]);
		// League/Cup name suggestions.
		ArrayList<String> leagueCupNameAL = matchDbAdapter.getColumnValues(MatchDbAdapter.KEY_TYPENAME, false);
		leagueCupNameArr = leagueCupNameAL.toArray(new String[leagueCupNameAL.size()]);
		matchDbAdapter.close();

		teamAdapter = new ArrayAdapter<String>(this, R.layout.list_team, teamsArr);
		venueAdapter = new ArrayAdapter<String>(this, R.layout.list_team, venueArr);
		refereeAdapter = new ArrayAdapter<String>(this, R.layout.list_team, refereesArr);
		leagueCupNameAdapter = new ArrayAdapter<String>(this, R.layout.list_team, leagueCupNameArr);

		// Team1 text view and input set up.
		team1AutoTV = (AutoCompleteTextView) findViewById(R.id.team1AutoTVRugby);
		team1AutoTV.setAdapter(teamAdapter);

		// Team2 text view and input set up.
		team2AutoTV = (AutoCompleteTextView) findViewById(R.id.team2AutoTVRugby);
		team2AutoTV.setAdapter(teamAdapter);

		// Venue input.
		venueAutoTV = (AutoCompleteTextView) findViewById(R.id.venueAutoTVRugby);
		venueAutoTV.setAdapter(venueAdapter);

		// Value of a try input.
		tryValueET = (EditText) this.findViewById(R.id.tryValueETRugby);

		// Value of a conversion input.
		conversionValueET = (EditText) this.findViewById(R.id.conversionValueETRugby);

		// Value of a penalty input.
		penaltyValueET = (EditText) this.findViewById(R.id.penaltyValueETRugby);

		// Value of a drop goal input.
		dropGoalValueET = (EditText) this.findViewById(R.id.dropGoalValueETRugby);

		// Number of players input.
		numOfPlayersET = (EditText) this.findViewById(R.id.numOfPlayersETRugby);

		// Minutes per half input.
		minutesPerHalfET = (EditText) this.findViewById(R.id.minutesPerHalfETRugby);

		// Referee input
		refereeNameAutoET = (AutoCompleteTextView) findViewById(R.id.refereeNameAutoTVRugby);
		refereeNameAutoET.setAdapter(refereeAdapter);

		// Type input selection
		friendlyRadioBN = (RadioButton) this.findViewById(R.id.friendlyRadioBNRugby);
		leagueRadioBN = (RadioButton) this.findViewById(R.id.leagueRadioBNRugby);
		cupRadioBN = (RadioButton) this.findViewById(R.id.cupRadioBNRugby);
		friendlyRadioBN.setOnClickListener(radioBoxOnClickListener);
		leagueRadioBN.setOnClickListener(radioBoxOnClickListener);
		cupRadioBN.setOnClickListener(radioBoxOnClickListener);
		leagueRadioBN.setChecked(true);

		// League/Cup Name input.
		// Pass in the League/Cup name from the database.
		leagueCupNameAutoTV = (AutoCompleteTextView) findViewById(R.id.leagueCupNameAutoTVRugby);
		leagueCupNameAutoTV.setAdapter(leagueCupNameAdapter);

		// Create button to submit the above pre match info.
		// Launches match Activity and passes pre match info to it.
		createMatchBN = (Button) this.findViewById(R.id.createMatchBNRugby);
		this.createMatchButton();

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

	// Actionbar onClick Listener.
	private class ActionBarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent i = null;
			switch (v.getId())
			{
			case R.id.home_button:
				i = new Intent(RugbyPreMatchInfoActivity.this, MainMenuActivity.class);
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
					i = new Intent(RugbyPreMatchInfoActivity.this, LoginActivity.class);
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
					Intent syncIntent = new Intent(RugbyPreMatchInfoActivity.this, SyncService.class);
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

	/**
	 * On clock listen for radio box selection.
	 */
	RadioButton.OnClickListener radioBoxOnClickListener = new RadioButton.OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub

			// Logic to enable/disable league/cup Name autoTV.
			if (friendlyRadioBN.isChecked())
			{
				leagueCupNameAutoTV.setEnabled(false);
			}
			else if (leagueRadioBN.isChecked())
			{
				leagueCupNameAutoTV.setEnabled(true);
			}
			else
			{
				leagueCupNameAutoTV.setEnabled(true);
			}
		}

	};

	/**
	 * Create match button listener method.
	 */
	private void createMatchButton()
	{
		createMatchBN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				// Check to see if all input elements validate correctly.
				if (validateForm())
				{
					team1 = team1AutoTV.getText().toString().trim();
					team2 = team2AutoTV.getText().toString().trim();
					venue = venueAutoTV.getText().toString().trim();
					tryValue = Integer.parseInt(tryValueET.getText().toString().trim());
					conversionValue = Integer.parseInt(conversionValueET.getText().toString().trim());
					penaltyValue = Integer.parseInt(penaltyValueET.getText().toString().trim());
					dropGoalValue = Integer.parseInt(dropGoalValueET.getText().toString().trim());
					numOfPlayers = Integer.parseInt(numOfPlayersET.getText().toString().trim());
					minutesPerHalf = Integer.parseInt(minutesPerHalfET.getText().toString().trim());
					leagueCupName = leagueCupNameAutoTV.getText().toString().trim();
					refereeName = refereeNameAutoET.getText().toString().trim();

					if (friendlyRadioBN.isChecked())
						type = friendlyRadioBN.getText().toString();
					else if (leagueRadioBN.isChecked())
						type = leagueRadioBN.getText().toString();
					else
						type = cupRadioBN.getText().toString();

					// Log.d("INPUT", "TEAM1: "+team1 +
					// "TEAM2: "+team2 +
					// "VENUE: "+venue +
					// "TRYVALUE: "+tryValue +
					// "CONVERSIONVALUE: "+conversionValue +
					// "PENALTYVALUE: "+penaltyValue +
					// "DROPGOALVALUE: "+dropGoalValue +
					// "NUMPLAYERS: "+numOfPlayers +
					// "MINPERHALF: "+minutesPerHalf +
					// "LEAGUECUPNAME: "+leagueCupName +
					// "REFEREE: "+refereeName +
					// "TYPE: "+type);

					// Launch match Activity and pass data to that Activity.
					Intent matchIntent = new Intent(RugbyPreMatchInfoActivity.this, RugbyMatchActivity.class);
					matchIntent.putExtra("Team1", team1);
					matchIntent.putExtra("Team2", team2);
					matchIntent.putExtra("Venue", venue);
					matchIntent.putExtra("TryValue", tryValue);
					matchIntent.putExtra("ConversionValue", conversionValue);
					matchIntent.putExtra("PenaltyValue", penaltyValue);
					matchIntent.putExtra("DropGoalValue", dropGoalValue);
					matchIntent.putExtra("NumOfPlayers", numOfPlayers);
					matchIntent.putExtra("MinutesPerHalf", minutesPerHalf);
					matchIntent.putExtra("LeagueCupName", leagueCupName);
					matchIntent.putExtra("RefereeName", refereeName);
					matchIntent.putExtra("Type", type);
					startActivity(matchIntent);
				}
				else
				{ // Form didn't validate proper.
					Toast.makeText(getApplicationContext(), "Fix input values.", Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	/**
	 * Validate all the user inputs from this activity.
	 * 
	 * @return true if all validation was successful.
	 */
	private boolean validateForm()
	{
		if (!validateAutoTextViewValue(team1AutoTV))
			return false;
		if (!validateAutoTextViewValue(team2AutoTV))
			return false;
		if (!validateEditTextValue(numOfPlayersET))
			return false;
		if (!validateEditTextValue(minutesPerHalfET))
			return false;
		if (!validateEditTextValue(tryValueET))
			return false;
		if (!validateEditTextValue(conversionValueET))
			return false;
		if (!validateEditTextValue(penaltyValueET))
			return false;
		if (!validateEditTextValue(dropGoalValueET))
			return false;

		return true;
	}

	/**
	 * Validate autoCompleteTextView - non empty.
	 * 
	 * @param autoCompleteTextView
	 * @return boolean true if validation was successful.
	 */
	private boolean validateAutoTextViewValue(AutoCompleteTextView autoCompleteTextView)
	{
		String value = autoCompleteTextView.getText().toString();
		if (value.length() == 0)
		{
			autoCompleteTextView.setError("Please enter a name.");
			return false;
		}
		return true;
	}

	/**
	 * Validate EditText - value is non empty and int.
	 * 
	 * @param autoCompleteTextView
	 * @return boolean true if validation was successful.
	 */
	private boolean validateEditTextValue(EditText editText)
	{
		String value = editText.getText().toString();

		// Check for non empty.
		if (value.length() == 0)
		{
			editText.setError("Please enter a number.");
			return false;
		}

		// Check that value is an int.
		try
		{
			Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}

}
