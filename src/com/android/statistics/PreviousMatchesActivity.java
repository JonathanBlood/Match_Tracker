package com.android.statistics;

import java.util.ArrayList;

import com.android.database.FactsDbAdapter;
import com.android.database.MatchDbAdapter;
import com.android.main.Actionbar;
import com.android.main.LoginActivity;
import com.android.main.MainMenuActivity;
import com.android.main.MatchInfo;
import com.android.send.SyncService;
import com.jonathanblood.main.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class PreviousMatchesActivity extends ListActivity
{
	
	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionBarOperations = new Actionbar(this);

	// Login information
	private boolean loggedIn = false;
	private PreviousMatchesAdapter prevMatchesAdapter;
	private MatchDbAdapter matchDbAdapter;
	private Runnable setPrevMatchInfo;
	private ProgressDialog progressDialog;
	private Spinner sportSpinner;
	private CheckBox dateCheckBox;
	private CheckBox teamNameCheckBox;
	private Button submitBN;
	private ArrayList<MatchInfo> matches;
	private ArrayList<MatchInfo> empty;
	private String sport = "Football";
	private boolean date = true; // boolean if to sort query by date.
	private boolean teamName = false; // boolean if to sort query by teamName.
	private int[] colors;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_previous_matches);

		// Actionbar Listener.
		ActionBarClickListener actionBClickListener = new ActionBarClickListener();
		findViewById(R.id.home_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.login_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.sync_button).setOnClickListener(actionBClickListener);

		// Row colours of listview.
		colors = new int[] { 0x4210752, 0x65535 };

		//Handle how the actionbar buttons are displayed.
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());

		// Sport spinner setup.
		sportSpinner = (Spinner) findViewById(R.id.sportSpinnerPrevMatches);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Sport_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sportSpinner.setAdapter(adapter);
		sportSpinner.setOnItemSelectedListener(new SportSpinnerItemSelectedListener());

		// Checkbox and submit button setup.
		dateCheckBox = (CheckBox) findViewById(R.id.dateCheckBoxPrevMatches);
		teamNameCheckBox = (CheckBox) findViewById(R.id.teamNameCheckBoxPrevMatches);
		submitBN = (Button) this.findViewById(R.id.submitBNPrevMatches);
		this.submitButton();

		matchDbAdapter = new MatchDbAdapter(this);
		empty = new ArrayList<MatchInfo>();
		matches = new ArrayList<MatchInfo>();
		this.prevMatchesAdapter = new PreviousMatchesAdapter(this, R.layout.statistics_previous_matches_row, empty);
		setListAdapter(this.prevMatchesAdapter);

		// Set up the ListView.
		this.setupListView();
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
	 * On item clicked from listview.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		MatchInfo matchInfo = (MatchInfo) this.getListAdapter().getItem(position);
		// Launch match Activity and pass data to that Activity.
		Intent matchIntent = new Intent(PreviousMatchesActivity.this, MatchActivity.class);
		matchIntent.putExtra("match_id", matchInfo.getMatchID());
		matchIntent.putExtra("date", matchInfo.getDate());
		matchIntent.putExtra("sport", matchInfo.getSport());
		matchIntent.putExtra("team1", matchInfo.getTeam1());
		matchIntent.putExtra("team2", matchInfo.getTeam2());
		matchIntent.putExtra("venue", matchInfo.getVenue());
		matchIntent.putExtra("type", matchInfo.getType());
		matchIntent.putExtra("type_name", matchInfo.getTypeName());
		matchIntent.putExtra("referee", matchInfo.getReferee());
		startActivity(matchIntent);

	}

	/**
	 * Method to setup the listview by running the setup in its own thread and use a progressDialog while it populates
	 * the listview.
	 */
	private void setupListView()
	{
		// Set prev match info to listview.
		setPrevMatchInfo = new Runnable()
		{
			@Override
			public void run()
			{
				getMatchInfoFromDatabase();
			}
		};
		Thread thread = new Thread(null, setPrevMatchInfo, "MagentoBackground");
		thread.start();
		progressDialog = ProgressDialog.show(PreviousMatchesActivity.this, "Please wait....", "Populating matches",
				true);
	}

	/**
	 * Get the match information from the database.
	 */
	private void getMatchInfoFromDatabase()
	{
		matchDbAdapter.open();
		matches = matchDbAdapter.getPrevMatches(date, teamName, sport);
		matchDbAdapter.close();
		runOnUiThread(setListViewValues);
	}

	private Runnable setListViewValues = new Runnable()
	{

		@Override
		public void run()
		{
			if (matches.size() > 0)
			{
				prevMatchesAdapter.notifyDataSetChanged();
				// Add teaminfo to adapter.
				for (int i = 0; i < matches.size(); i++)
				{
					prevMatchesAdapter.add(matches.get(i));
				}
			}
			// When done dismiss progress Dialog.
			progressDialog.dismiss();
			prevMatchesAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * Undo event button listener method.
	 */
	private void submitButton()
	{
		submitBN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Get Checkbox selection and submit new listview query.

				date = (dateCheckBox.isChecked()) ? true : false;
				teamName = (teamNameCheckBox.isChecked()) ? true : false;

				prevMatchesAdapter.clear();
				setupListView();
				prevMatchesAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * Custom adapter for Previous Matches listview.
	 * 
	 * @author Jonathan
	 */
	private class PreviousMatchesAdapter extends ArrayAdapter<MatchInfo>
	{
		private ArrayList<MatchInfo> matches;
		private FactsDbAdapter factsDbAdapter;

		public PreviousMatchesAdapter(Context context, int textViewResourceId, ArrayList<MatchInfo> matches)
		{
			super(context, textViewResourceId, matches);
			this.matches = matches;
			factsDbAdapter = new FactsDbAdapter(context);
		}

		/**
		 * Overwrite getView method.
		 */
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			if (view == null)
			{
				LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = li.inflate(R.layout.statistics_previous_matches_row, null);

				// Set colour of rows.
				int colorPos = position % colors.length;
				view.setBackgroundColor(colors[colorPos]);
			}

			// Set MatchInfo for adapter.
			MatchInfo match = matches.get(position);
			if (match != null)
			{
				// Setup textviews
				TextView team1Name = (TextView) view.findViewById(R.id.team1_name_prevMatches);
				TextView team2Name = (TextView) view.findViewById(R.id.team2_name_prevMatches);
				TextView date = (TextView) view.findViewById(R.id.date_prevMatches);
				TextView team1Score = (TextView) view.findViewById(R.id.team1_score_prevMatches);
				TextView team2Score = (TextView) view.findViewById(R.id.team2_score_prevMatches);

				// Set text on textviews.
				team1Name.setText(match.getTeam1());
				team2Name.setText(match.getTeam2());
				date.setText(match.getDate());

				// Set the scores which depend on sport type.
				String sport = match.getSport();
				int match_id = match.getMatchID();
				factsDbAdapter.open();
				if ("Football".equals(sport))
				{ // If sport is football set
					// score.
					team1Score.setText("" + factsDbAdapter.countTypesWithMatchID(match_id, "Goal", match.getTeam1()));
					team2Score.setText("" + factsDbAdapter.countTypesWithMatchID(match_id, "Goal", match.getTeam2()));

					// If sport is gaelic or hurling set score.
				}
				else if ("Gaelic".equals(sport) || "Hurling".equals(sport))
				{

					int goal1 = factsDbAdapter.countTypesWithMatchID(match_id, "Goal", match.getTeam1());
					int goal2 = factsDbAdapter.countTypesWithMatchID(match_id, "Goal", match.getTeam2());
					int point1 = factsDbAdapter.countTypesWithMatchID(match_id, "Point", match.getTeam1());
					int point2 = factsDbAdapter.countTypesWithMatchID(match_id, "Point", match.getTeam2());
					team1Score.setText(goal1 + ":" + point1);
					team2Score.setText(goal2 + ":" + point2);

				}
				else if ("Rugby".equals(sport))
				{ // If sport is rugby set
					// score.
					int team1TotalScore = 0;
					int team2TotalScore = 0;
					int trySc = 0;
					int conversion = 0;
					int penalty = 0;
					int dropGoal = 0;

					// Team1 get total score.
					trySc = factsDbAdapter.countTypesWithMatchID(match_id, "Try", match.getTeam1());
					conversion = factsDbAdapter.countTypesWithMatchID(match_id, "Conversion", match.getTeam1());
					penalty = factsDbAdapter.countTypesWithMatchID(match_id, "Penalty", match.getTeam1());
					dropGoal = factsDbAdapter.countTypesWithMatchID(match_id, "Drop Goal", match.getTeam1());
					team1TotalScore = (trySc * 5) + (conversion * 2) + (penalty * 3) + (dropGoal * 3);

					// Team2 get total score.
					trySc = factsDbAdapter.countTypesWithMatchID(match_id, "Try", match.getTeam2());
					conversion = factsDbAdapter.countTypesWithMatchID(match_id, "Conversion", match.getTeam2());
					penalty = factsDbAdapter.countTypesWithMatchID(match_id, "Penalty", match.getTeam2());
					dropGoal = factsDbAdapter.countTypesWithMatchID(match_id, "Drop Goal", match.getTeam2());
					team2TotalScore = (trySc * 5) + (conversion * 2) + (penalty * 3) + (dropGoal * 3);

					team1Score.setText("" + team1TotalScore);
					team2Score.setText("" + team2TotalScore);
				}
				factsDbAdapter.close();
			}
			return view;
		}
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
				i = new Intent(PreviousMatchesActivity.this, MainMenuActivity.class);
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
					i = new Intent(PreviousMatchesActivity.this, LoginActivity.class);
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
					Intent syncIntent = new Intent(PreviousMatchesActivity.this, SyncService.class);
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
	private class SportSpinnerItemSelectedListener implements OnItemSelectedListener
	{

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{

			// Match event selection.
			String item = parent.getItemAtPosition(pos).toString();
			if (item.equals("Football"))
			{
				sport = "Football";
			}
			else if (item.equals("Gaelic"))
			{
				sport = "Gaelic";
			}
			else if (item.equals("Hurling"))
			{
				sport = "Hurling";
			}
			else if (item.equals("Rugby"))
			{
				sport = "Rugby";
			}

		}

		public void onNothingSelected(AdapterView<?> parent)
		{
			// Do nothing.
		}
	}

}
