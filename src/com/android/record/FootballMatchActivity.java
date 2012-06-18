package com.android.record;

import java.util.ArrayList;

import com.android.database.FactsDbAdapter;
import com.android.database.MatchDbAdapter;
import com.android.database.TeamDbAdapter;
import com.android.main.Actionbar;
import com.android.main.LoginActivity;
import com.android.main.MainMenuActivity;
import com.android.main.MatchEvent;
import com.android.send.SyncService;
import com.jonathanblood.main.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Chronometer.OnChronometerTickListener;

/**
 * This class is responsible for the recording of Football matches.
 * 
 * @author Jonathan
 * 
 */
public class FootballMatchActivity extends Activity
{

	/**
	 * Constants for pre match information.
	 */
	private static final String SPORT = "Football";
	private static final String WIN = "Win";
	private static final String LOSS = "Loss";
	private static final String DRAW = "Draw";
	
	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionBarOperations = new Actionbar(this);

	/* MATCH RECORDER ELEMENTS */
	/**
	 * Boolean whether the user is logged in or not.
	 */
	private boolean loggedIn = false;
	
	/**
	 * Start button that is used to start/resume the timer.
	 */
	private ImageButton startBn;
	
	/**
	 * Stop button that is used to stop/pause the timer.
	 */
	private ImageButton stopBn;
	
	/**
	 *Image Button that will allow the user to undo any match events.
	 */
	private ImageButton undoEventBn;
	
	/**
	 * Allows the user to finish the match and commit it to the database.
	 */
	private ImageButton finishMatchBn;
	
	/**
	 * Allows the user to add a note.
	 */
	private ImageButton addNoteBn;
	
	/**
	 * Displays the match events that have happened in the match so far.
	 */
	private TextView matchInfoTV;
	
	/**
	 * Displays the name of the first team.
	 */
	private TextView team1NameTV;
	
	/**
	 * Displays the name of the second team.
	 */
	private TextView team2NameTV;
	
	/**
	 * Displays the score for team one.
	 */
	private TextView team1ScoreTV;
	
	/**
	 * Displays the score for team two.
	 */
	private TextView team2ScoreTV;
	
	/**
	 * Match Timer for the match.
	 */
	private Chronometer matchChron; 
	
	/**
	 * Used to display the different match events.
	 */
	private Spinner matchEventSpinner; 

	/* ONE PLAYER EVENT DIALOG ELEMENTS. */
	/**
	 * Dialog that will pop up when user clicks one player match event. 
	 */
	private Dialog onePlayerEventDG;
	
	/**
	 * Radio button to select team 1 for the event.
	 */
	private RadioButton team1EventRBN;
	
	/**
	 * Radio button to select team 2 for the event.
	 */
	private RadioButton team2EventRBN;
	
	/**
	 * Allows the user to enter in the player who was involved in the event.
	 * It also offers suggestions of the player as user types.
	 */
	private AutoCompleteTextView playerOneNameEventAutoTV;
	
	/**
	 * Adds the match event to the match.
	 */
	private ImageButton createMatchEventBN;
	
	/**
	 * Allows the user to hide the one player match event dialog.
	 */
	private ImageButton hideMatchEventBN;
	
	/**
	 * Adapter for autocomplete to load in player names as a suggestion.
	 */
	private ArrayAdapter<String> playerAdapter;
	
	/**
	 * Array of players loaded in from database.
	 */
	private String[] players;

	/* TWO PLAYER MATCH EVENT DIALOG */
	/**
	 * Dialog that will pop up when user clicks a two player match event.
	 */
	private Dialog twoPlayerEventDG;
	
	/**
	 * Radio Button to select team 1 for match event.
	 */
	private RadioButton team1EventTwoRBN;
	
	/**
	 * Radio button to select team 2 for match event.
	 */
	private RadioButton team2EventTwoRBN;
	
	/**
	 * Allows the user to enter in the player one who was involved in the event.
	 * It also offers suggestions of the player as user types.
	 */
	private AutoCompleteTextView playerOneNameEventTwoAutoTV;
	
	/**
	 * Allows the user to enter in the player two who was involved in the event.
	 * It also offers suggestions of the player as user types.
	 */
	private AutoCompleteTextView playerTwoNameEventTwoAutoTV;
	
	/**
	 * Allows the user to add the match event to the match.
	 */
	private ImageButton createMatchEventTwoBN;
	
	/**
	 * Allows the user to hide the two player match event dialog.
	 */
	private ImageButton hideMatchEventTwoBN;
	
	/* NOTE DIALOG */
	/**
	 * Dialog that will pop up when user clicks a two player match event.
	 */
	private Dialog noteDG;
	
	/**
	 * Allows user to enter in a note for the match.
	 */
	private EditText noteET;
	
	/**
	 * Allows the user to add the match event to the match.
	 */
	private ImageButton createNoteBN;
	
	/**
	 * Allows the user to hide the two player match event dialog.
	 */
	private ImageButton hideNoteBN;

	/* STOP WATCH VARIABLES */
	
	/**
	 * Used to keep track of the time elapsed since timer started.
	 */
	private long timeElapsed = 0; 
	
	/**
	 * Boolean to check if the timer has being paused already.
	 */
	private boolean isPausedFirstRun = false;
	
	/**
	 * Boolean to check if the timer is paused.
	 */
	private boolean isPaused = false;
	
	/**
	 * String representation of the current time.
	 */
	private String currTime = ""; 
	
	/**
	 * Variable to hold the current time in milliseconds.
	 */
	private long currTimeMilli = 0; 
	boolean firstHalf = true;

	/* MATCH INFO VARIABLES */
	
	/**
	 * String containing the name of the first team.
	 */
	private String team1;
	
	/**
	 * String containing the name of the second team.
	 */
	private String team2;
	
	/**
	 * String containing the venue the match took place.
	 */
	private String venue;
	
	/**
	 * String containing the referee name.
	 */
	private String refereeName;
	
	/**
	 * String containing the type of match.
	 */
	private String type;
	
	/**
	 * String containing the name of the league/cup match.
	 */
	private String leagueCupName;
	
	/**
	 * String containing match information.
	 */
	private String  matchInfo = "";
	
	/**
	 * String containing the time the event took place.
	 */
	private String eventTime;
	
	/**
	 * String containing the  team involved in the event.
	 */
	private String eventTeamSel;
	
	/**
	 * String containing the first player of the event.
	 */
	private String eventPlayer1Name;
	
	/**
	 * String containing the second player of the event.
	 */
	private String eventPlayer2Name;
	
	/**
	 * String containing the event type.
	 */
	private String eventType;
	
	/**
	 * String containing the half time length.
	 */
	private String halfTime;
	
	/**
	 * String containing the full time length.
	 */
	private String fullTime;
	
	/**
	 * Contains the number of minutes per half.
	 */
	private int minutesPerHalf;
	
	/** 
	 * Team 1 overall score of the match.
	 */
	private int team1OverallScore = 0;
	
	/**
	 * Team 2 overall score of the match.
	 */
	private int team2OverallScore = 0;

	/**
	 * ArrayList to store the match events of the match.
	 */
	private ArrayList<MatchEvent> matchEvents = new ArrayList<MatchEvent>(); 
	
	/**
	 *  Database adapter for Match table.
	 */
	private MatchDbAdapter matchDbAdapter  = new MatchDbAdapter(this); 
	
	/**
	 *  Database adapter for Facts table.
	 */
	private FactsDbAdapter factsDbAdapter = new FactsDbAdapter(this);
	
	/**
	 * Database adapter for Team table.
	 */
	private TeamDbAdapter teamDbAdapter = new TeamDbAdapter(this); 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.football_match);

		// Actionbar Listener.
		ActionBarClickListener actionBClickListener = new ActionBarClickListener();
		findViewById(R.id.home_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.login_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.sync_button).setOnClickListener(actionBClickListener);

		//Handle how the actionbar buttons are displayed
		actionBarOperations = new Actionbar(getApplicationContext());
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());

		// Retrieve Parameters.
		Intent getParameters = getIntent();
		team1 = getParameters.getStringExtra("Team1");
		team2 = getParameters.getStringExtra("Team2");
		venue = getParameters.getStringExtra("Venue");
		leagueCupName = getParameters.getStringExtra("LeagueCupName");
		refereeName = getParameters.getStringExtra("RefereeName");
		getParameters.getIntExtra("NumOfPlayers", 11);
		minutesPerHalf = getParameters.getIntExtra("MinutesPerHalf", 45);
		type = getParameters.getStringExtra("Type");

		// Set half time and full time value.
		if (minutesPerHalf < 10) // Half time check.
		{ 
			
			halfTime = "0" + Integer.toString(minutesPerHalf) + ":00";
			
			if (minutesPerHalf * 2 < 10) // Full time check.
				fullTime = "0" + Integer.toString(minutesPerHalf * 2) + ":00";
			else
				fullTime = Integer.toString(minutesPerHalf * 2) + ":00";

		}
		else
		{
			halfTime = Integer.toString(minutesPerHalf) + ":00";
			fullTime = Integer.toString(minutesPerHalf * 2) + ":00";
		}

		// Score/Team name elements.
		team1NameTV = (TextView) this.findViewById(R.id.team1NameTVFM);
		team1NameTV.setText(team1);
		team1ScoreTV = (TextView) this.findViewById(R.id.team1GoalTVFM);
		team1ScoreTV.setText(Integer.toString(team1OverallScore));
		team2NameTV = (TextView) this.findViewById(R.id.team2NameTVFM);
		team2NameTV.setText(team2);
		team2ScoreTV = (TextView) this.findViewById(R.id.team2GoalTVFM);
		team2ScoreTV.setText(Integer.toString(team2OverallScore));

		// Get players from database for autocomplete input.
		factsDbAdapter.open();
		ArrayList<String> plyrs = factsDbAdapter.getColumnValues(FactsDbAdapter.KEY_PLAYER1, false);
		players = plyrs.toArray(new String[plyrs.size()]);
		factsDbAdapter.close();

		// Dialog setup for when match event(one player) is called.
		onePlayerEventDG = new Dialog(FootballMatchActivity.this);
		onePlayerEventDG.setContentView(R.layout.one_player_event_dialog);
		onePlayerEventDG.setTitle("Event Details: ");
		onePlayerEventDG.setCancelable(true);
		this.onePlayerEventDialogSetup();

		// Dialog setup for when match event(two player) is called.
		twoPlayerEventDG = new Dialog(FootballMatchActivity.this);
		twoPlayerEventDG.setContentView(R.layout.two_player_event_dialog);
		twoPlayerEventDG.setTitle("Event Details: ");
		twoPlayerEventDG.setCancelable(true);
		this.twoPlayerEventDialogSetup();
		
		// Dialog setup for adding notes.
		noteDG = new Dialog(FootballMatchActivity.this);
		noteDG.setContentView(R.layout.note_dialog);
		noteDG.setTitle("Add note: ");
		noteDG.setCancelable(true);
		this.noteDialogSetup();
		
		addNoteBn = (ImageButton) findViewById(R.id.addNoteFM);
		this.addNoteButton();

		// Timer elements
		startBn = (ImageButton) this.findViewById(R.id.startButtonFM);
		this.startButton();
		stopBn = (ImageButton) this.findViewById(R.id.stopButtonFM);
		stopBn.setEnabled(false);
		this.stopButton();
		matchChron = (Chronometer) findViewById(R.id.chronometerFM);
		this.onChronTick();

		// Match events elements.
		matchEventSpinner = (Spinner) findViewById(R.id.matchEventSpinnerFM);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.footballMatchEvent_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		matchEventSpinner.setAdapter(adapter);
		matchEventSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener());

		matchInfoTV = (TextView) this.findViewById(R.id.matchInfoTVFM);
		undoEventBn = (ImageButton) this.findViewById(R.id.undoEventButtonFM);
		this.undoEventButton();
		finishMatchBn = (ImageButton) this.findViewById(R.id.finishMatchFM);
		this.finishMatchButton();

	}

	/** Called when activity is destroyed. */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		onePlayerEventDG.dismiss();
		twoPlayerEventDG.dismiss();
		noteDG.dismiss();
	}

	/** Called when activity is in the background. */
	@Override
	public void onPause()
	{
		super.onPause();
		currTimeMilli = System.currentTimeMillis();
		onePlayerEventDG.dismiss();
		twoPlayerEventDG.dismiss();
		noteDG.dismiss();
	}

	/** Called when activity is called back into foreground. */
	@Override
	public void onResume()
	{
		super.onResume();
		if (isPaused == false)
			timeElapsed += System.currentTimeMillis() - currTimeMilli;

		//Handle how the actionbar buttons are displayed.
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
	}

	/** Called when activity is hidden from screen. */
	@Override
	public void onStop()
	{
		super.onStop();
		currTimeMilli = System.currentTimeMillis();
	}

	/** Called when activity is called back into foreground. */
	@Override
	public void onRestart()
	{
		super.onRestart();
		if (isPaused == false)
			timeElapsed += System.currentTimeMillis() - currTimeMilli;

		//Handle how the actionbar buttons are displayed.
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
	}

	/**
	 * The set up of one player event dialog.
	 */
	private void onePlayerEventDialogSetup()
	{

		team1EventRBN = (RadioButton) onePlayerEventDG.findViewById(R.id.team1SelEvent);
		team2EventRBN = (RadioButton) onePlayerEventDG.findViewById(R.id.team2SelEvent);
		team1EventRBN.setText(team1);
		team2EventRBN.setText(team2);
		team1EventRBN.setChecked(true);

		playerAdapter = new ArrayAdapter<String>(this, R.layout.list_team, players);
		playerOneNameEventAutoTV = (AutoCompleteTextView) onePlayerEventDG.findViewById(R.id.player1EventNameAUTOTV);
		playerOneNameEventAutoTV.setAdapter(playerAdapter);

		// Submit Match Event button.
		createMatchEventBN = (ImageButton) onePlayerEventDG.findViewById(R.id.createMatchEventBN);
		createMatchEventBN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				// Get team selected from radio buttons.
				if (team1EventRBN.isChecked())
					eventTeamSel = team1EventRBN.getText().toString();
				else if (team2EventRBN.isChecked())
					eventTeamSel = team2EventRBN.getText().toString();

				// Set player event happened to.
				eventPlayer1Name = playerOneNameEventAutoTV.getText().toString().trim();

				// Create match event and store it in list of match events.
				MatchEvent matchEvent = new MatchEvent(eventTime, eventType, eventPlayer1Name, eventTeamSel);

				// ifgGoal event update GUI to display new score.
				if ("Goal".equals(eventType))
					updateMatchScore(eventTeamSel);

				// Add match event to list of match events so far.
				matchEvents.add(matchEvent);
				matchInfo += matchEvent.toString();
				matchInfoTV.setText(matchInfo);
				playerOneNameEventAutoTV.setText("");
				onePlayerEventDG.hide();
			}
		});

		// Hide Match Event button.
		hideMatchEventBN = (ImageButton) onePlayerEventDG.findViewById(R.id.hideMatchEventBN);
		hideMatchEventBN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				playerOneNameEventAutoTV.setText("");
				onePlayerEventDG.hide();
			}
		});
	}

	/**
	 * The set up of two player event dialog.
	 */
	private void twoPlayerEventDialogSetup()
	{

		team1EventTwoRBN = (RadioButton) twoPlayerEventDG.findViewById(R.id.team1SelEventTwo);
		team2EventTwoRBN = (RadioButton) twoPlayerEventDG.findViewById(R.id.team2SelEventTwo);
		team1EventTwoRBN.setText(team1);
		team2EventTwoRBN.setText(team2);
		team1EventTwoRBN.setChecked(true);

		// Player one.
		playerAdapter = new ArrayAdapter<String>(this, R.layout.list_team, players);
		playerOneNameEventTwoAutoTV = (AutoCompleteTextView) twoPlayerEventDG
				.findViewById(R.id.player1EventTwoNameAUTOTV);
		playerOneNameEventTwoAutoTV.setAdapter(playerAdapter);

		// Player two.
		playerTwoNameEventTwoAutoTV = (AutoCompleteTextView) twoPlayerEventDG
				.findViewById(R.id.player2EventTwoNameAUTOTV);
		playerTwoNameEventTwoAutoTV.setAdapter(playerAdapter);

		// Submit Match Event button.
		createMatchEventTwoBN = (ImageButton) twoPlayerEventDG.findViewById(R.id.createMatchEventTwoBN);
		createMatchEventTwoBN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				// Get team selected from radio buttons.
				if (team1EventTwoRBN.isChecked())
					eventTeamSel = team1EventTwoRBN.getText().toString();
				else if (team2EventTwoRBN.isChecked())
					eventTeamSel = team2EventTwoRBN.getText().toString();

				// Set player1 and player 2 players involved in event.
				eventPlayer1Name = playerOneNameEventTwoAutoTV.getText().toString().trim();
				eventPlayer2Name = playerTwoNameEventTwoAutoTV.getText().toString().trim();

				// Create match event and store it in list of match events.
				MatchEvent matchEvent = new MatchEvent(eventTime, eventType, eventPlayer1Name, eventPlayer2Name,
						eventTeamSel);

				// if Goal event update GUI to display new score.
				if ("Goal".equals(eventType))
					updateMatchScore(eventTeamSel);

				// Add match event to list of match events so far.
				matchEvents.add(matchEvent);
				matchInfo += matchEvent.toString();
				matchInfoTV.setText(matchInfo);
				playerOneNameEventTwoAutoTV.setText("");
				playerTwoNameEventTwoAutoTV.setText("");
				twoPlayerEventDG.hide();
			}
		});

		// Hide Match Event button.
		hideMatchEventTwoBN = (ImageButton) twoPlayerEventDG.findViewById(R.id.hideMatchEventTwoBN);
		hideMatchEventTwoBN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				playerOneNameEventTwoAutoTV.setText("");
				playerTwoNameEventTwoAutoTV.setText("");
				twoPlayerEventDG.hide();
			}
		});
	}
	
	/**
	 * The set up of note dialog.
	 */
	private void noteDialogSetup()
	{

		noteET = (EditText) noteDG.findViewById(R.id.noteET);

		// Submit Match Event button.
		createNoteBN = (ImageButton) noteDG.findViewById(R.id.createNoteBN);
		createNoteBN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				// Create match event and store it in list of match events.
				MatchEvent matchEvent = new MatchEvent(currTime, eventType, noteET.getText().toString());

				// Add match event to list of match events so far.
				matchEvents.add(matchEvent);
				matchInfo += matchEvent.toString();
				matchInfoTV.setText(matchInfo);
				noteET.setText("");
				noteDG.hide();
			}
		});

		// Hide Match Event button.
		hideNoteBN = (ImageButton) noteDG.findViewById(R.id.hideNoteBN);
		hideNoteBN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				noteET.setText("");
				noteDG.hide();
			}
		});
	}

	// Method to rebuild match event string after an event is undone.
	private void reBuildMatchEventsString()
	{
		matchInfo = "";
		for (MatchEvent mEvent : matchEvents)
		{
			matchInfo += mEvent.toString();
		}
		matchInfoTV.setText(matchInfo);
	}
	
	/**
	 * Add note button listener method.
	 */
	private void addNoteButton()
	{
		addNoteBn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				eventType = "Note";
				noteDG.show();
			}
		});
	}

	/**
	 * Start button listener method.
	 */
	private void startButton()
	{
		startBn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startTimer();
			}
		});
	}

	/**
	 * Stop button listener method.
	 */
	private void stopButton()
	{
		stopBn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				stopTimer();
			}
		});
	}

	/**
	 * Undo event button listener method.
	 */
	private void undoEventButton()
	{
		undoEventBn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				// Check to see if there is an event to move.
				if (matchEvents.size() >= 1)
				{
					// Remove last element. I.E last event.
					MatchEvent aMatchEvent = matchEvents.remove(matchEvents.size() - 1);

					// Check if the event is a goal so we can then undo the score.
					if ("Goal".equals(aMatchEvent.getType()))
					{
						undoMatchScore(aMatchEvent.getTeam());
					}

					// Rebuild Match event string with removed element.
					reBuildMatchEventsString();
				}
			}
		});
	}

	/**
	 * Finish Match button listener method.
	 */
	private void finishMatchButton()
	{
		finishMatchBn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int matchid;
				MatchEvent event;

				// Add match to match table in database.
				matchDbAdapter.open();
				matchDbAdapter.addMatch(SPORT, team1, team2, venue, type, leagueCupName, refereeName);
				matchid = matchDbAdapter.getLastMatchID();
				matchDbAdapter.close();

				// Add match facts to Facts table in database.
				factsDbAdapter.open();
				for (int i = 0; i < matchEvents.size(); i++)
				{
					event = matchEvents.get(i);
					factsDbAdapter.addMatchFact(matchid, event.getType(), event.getPlayer1(), event.getPlayer2(),
							event.getTeam(), event.getTime(), event.getNote());
				}

				// ArrayList<String> test = factsDbAdapter.getColumnValues(factsDbAdapter.KEY_MATCHID);
				// Log.d("FACTS", "FACTS: "+ test.get(test.size()-1));
				factsDbAdapter.close();

				// Add teams to Team table in database if they don't exist else update them.
				teamDbAdapter.open();
				String team1Status;
				String team2Status;
				if (team1OverallScore > team2OverallScore)
				{// Team 1 won match.
					team1Status = WIN;
					team2Status = LOSS;
				}
				else if (team1OverallScore == team2OverallScore)
				{// Draw
					team1Status = DRAW;
					team2Status = DRAW;
				}
				else
				{// Team 1 lost the game.
					team1Status = LOSS;
					team2Status = WIN;
				}
				addOrUpdateTeam(teamDbAdapter, team1, team1Status);
				addOrUpdateTeam(teamDbAdapter, team2, team2Status);
				teamDbAdapter.close();

				// Finished saving match so bring user back to home screen.
				Toast.makeText(v.getContext(), "Match saved.", Toast.LENGTH_LONG).show();
				Intent i = new Intent(FootballMatchActivity.this, MainMenuActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
	}

	/**
	 * This method adds or updates a team in the Team table of the database.
	 * 
	 * @param teamAdapter
	 * @param team
	 * @param status
	 *            - if team won, lost or drew.
	 */
	@SuppressWarnings("static-access")
	private void addOrUpdateTeam(TeamDbAdapter teamAdapter, String team, String status)
	{
		String teamExists = teamAdapter.getColumnValueForTeamStr(teamAdapter.KEY_NAME, team);
		String sportExistsForGivenTeam = teamAdapter.getColumnValueForTeamStr(teamAdapter.KEY_SPORT, team);

		// Team exists so update. We check team name and sport as we could have teams in
		// different sports with the same name.
		if (team.equals(teamExists) && SPORT.equals(sportExistsForGivenTeam))
		{

			if (WIN.equals(status))
			{// Team won the match.

				// Get total wins and points.
				int totalWins = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_WINS, team);
				int totalPoints = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_TOTALPOINTS, team);

				// Update wins and total points.
				teamAdapter.updateSingleColumn(teamExists, teamAdapter.KEY_WINS, totalWins + 1);
				teamAdapter.updateSingleColumn(teamExists, teamAdapter.KEY_TOTALPOINTS, totalPoints + 3);
			}
			else if (DRAW.equals(status))
			{// Draw

				// Get total draws and points.
				int totalDraws = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_DRAWS, team);
				int totalPoints = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_TOTALPOINTS, team);

				// Update wins and total points.
				teamAdapter.updateSingleColumn(team, teamAdapter.KEY_DRAWS, totalDraws + 1);
				teamAdapter.updateSingleColumn(team, teamAdapter.KEY_TOTALPOINTS, totalPoints + 1);

			}
			else
			{// Team lost the game.

				// Get total losses.
				int totalLosses = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_LOSSES, team);

				// Update wins and total points.
				teamAdapter.updateSingleColumn(team, teamAdapter.KEY_LOSSES, totalLosses + 1);
			}

		}
		else
		{ // Team doesn't exist so add team.

			if (WIN.equals(status)) // Team won match.
				teamAdapter.addTeam(team, SPORT, 1, 0, 0, 3);
			else if (DRAW.equals(status)) // Draw
				teamAdapter.addTeam(team, SPORT, 0, 0, 1, 1);
			else
				// Team lost the game.
				teamAdapter.addTeam(team, SPORT, 0, 1, 0, 0);
		}
	}

	/**
	 * If goal is called it updates the score of the match.
	 * 
	 * @param aTeam
	 */
	private void updateMatchScore(String aTeam)
	{
		if (team1.equals(aTeam))
		{
			team1OverallScore++;
			team1ScoreTV.setText(Integer.toString(team1OverallScore));
		}
		else if (team2.equals(aTeam))
		{
			team2OverallScore++;
			team2ScoreTV.setText(Integer.toString(team2OverallScore));
		}
	}

	/**
	 * If undo button is called and type == goal then undo the score for the team.
	 * 
	 * @param aTeam
	 */
	private void undoMatchScore(String aTeam)
	{
		if (team1.equals(aTeam))
		{
			team1OverallScore--;
			team1ScoreTV.setText(Integer.toString(team1OverallScore));
		}
		else if (team2.equals(aTeam))
		{
			team2OverallScore--;
			team2ScoreTV.setText(Integer.toString(team2OverallScore));
		}
	}

	/**
	 * Start the timer of the chronometer.
	 */
	private void startTimer()
	{
		startBn.setEnabled(false);
		stopBn.setEnabled(true);
		isPaused = false;
		if (isPausedFirstRun)
		{ // Timer being resumed, just start timer again.
			matchChron.start();
		}
		else
		{ // First time timer is started.
			matchChron.setBase(SystemClock.elapsedRealtime());
			matchChron.start();
		}
	}

	/**
	 * Stop the timer of the chronometer.
	 */
	private void stopTimer()
	{
		startBn.setEnabled(true);
		stopBn.setEnabled(false);
		isPaused = true;

		matchChron.stop();
		isPausedFirstRun = true;
	}

	/**
	 * Method to play an alarm sound to signal half time or full time.
	 */
	private void playAlarm()
	{
		MediaPlayer mp = MediaPlayer.create(FootballMatchActivity.this, R.raw.buzzer);
		mp.start();
		mp.setOnCompletionListener(new OnCompletionListener()
		{

			@Override
			public void onCompletion(MediaPlayer mp)
			{
				mp.release();
			}

		});

	}

	/**
	 * Deals with chronometer tick listener.
	 */
	private void onChronTick()
	{
		matchChron.setOnChronometerTickListener(new OnChronometerTickListener()
		{

			public void onChronometerTick(Chronometer arg0)
			{
				// TODO Auto-generated method stub

				long minutes;
				long seconds;
				String minutesStr;
				String secondsStr;
				if (isPausedFirstRun)
				{ // If timer has being resumed from a pause state.

					// Calculate minutes and seconds.
					minutes = ((timeElapsed - matchChron.getBase()) / 1000) / 60;
					seconds = ((timeElapsed - matchChron.getBase()) / 1000) % 60;
					minutesStr = Long.toString(minutes);
					secondsStr = Long.toString(seconds);

					if (minutes < 10)
						minutesStr = "0" + minutesStr;
					if (seconds < 10)
						secondsStr = "0" + secondsStr;

					timeElapsed = timeElapsed + 1000;
					currTime = minutesStr + ":" + secondsStr;
					arg0.setText(currTime);

				}
				else
				{ // If timer hasn't being paused yet.

					// Calculate minutes and seconds.
					minutes = ((SystemClock.elapsedRealtime() - matchChron.getBase()) / 1000) / 60;
					seconds = ((SystemClock.elapsedRealtime() - matchChron.getBase()) / 1000) % 60;
					minutesStr = Long.toString(minutes);
					secondsStr = Long.toString(seconds);

					if (minutes < 10)
						minutesStr = "0" + minutesStr;
					if (seconds < 10)
						secondsStr = "0" + secondsStr;

					timeElapsed = SystemClock.elapsedRealtime();
					currTime = minutesStr + ":" + secondsStr;
					arg0.setText(currTime);
				}

				// Half time.
				if (currTime.equals(halfTime) && firstHalf == true)
				{
					firstHalf = false;
					stopTimer();
					playAlarm();
				}

				// Full time.
				if (currTime.equals(fullTime) && firstHalf == false)
				{
					stopTimer();
					playAlarm();
				}

			}

		});
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
				i = new Intent(FootballMatchActivity.this, MainMenuActivity.class);
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
					i = new Intent(FootballMatchActivity.this, LoginActivity.class);
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
					Intent syncIntent = new Intent(FootballMatchActivity.this, SyncService.class);
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

	// Spinner itemSelected listener.
	private class SpinnerItemSelectedListener implements OnItemSelectedListener
	{

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{

			// Match event selection.
			String item = parent.getItemAtPosition(pos).toString();
			eventTime = currTime;
			if (item.equals("None"))
			{
				// Do nothing.
			}
			else if (item.equals("Goal"))
			{
				eventType = "Goal";
				onePlayerEventDG.show();
			}
			else if (item.equals("Red Card"))
			{
				eventType = "Red Card";
				onePlayerEventDG.show();
			}
			else if (item.equals("Yellow Card"))
			{
				eventType = "Yellow Card";
				onePlayerEventDG.show();
			}
			else if (item.equals("Injury"))
			{
				eventType = "Injury";
				onePlayerEventDG.show();
			}
			else if (item.equals("Substitution"))
			{
				eventType = "Substitution";
				twoPlayerEventDG.show();
			}

			// Set selection to none.
			parent.setSelection(0);

		}

		public void onNothingSelected(AdapterView<?> parent)
		{
			// Do nothing.
		}
	}
}
