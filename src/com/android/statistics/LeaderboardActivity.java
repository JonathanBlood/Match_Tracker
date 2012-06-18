package com.android.statistics;

import java.util.ArrayList;

import com.android.database.TeamDbAdapter;
import com.android.main.Actionbar;
import com.android.main.LoginActivity;
import com.android.main.MainMenuActivity;
import com.android.main.TeamInfo;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Displays leaderboard in a listview.
 * 
 * @author Jonathan
 * 
 */
public class LeaderboardActivity extends ListActivity
{

	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionBarOperations = new Actionbar(this);
	
	// Login information
	private boolean loggedIn = false;
	private LeaderboardAdapter leaderboardAdapter;
	private TeamDbAdapter teamDbAdapter;
	private Runnable setLeaderboardInfo;
	private ProgressDialog progressDialog;
	private ArrayList<TeamInfo> empty;
	private ArrayList<TeamInfo> teams;
	private Spinner sportSpinner;
	private String sport = "Football";
	private int[] colors;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_leaderboard);

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
		sportSpinner = (Spinner) findViewById(R.id.sportSpinnerLeaderboard);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Sport_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sportSpinner.setAdapter(adapter);
		sportSpinner.setOnItemSelectedListener(new SportSpinnerItemSelectedListener());

		teamDbAdapter = new TeamDbAdapter(this);
		empty = new ArrayList<TeamInfo>();
		teams = new ArrayList<TeamInfo>();
		this.leaderboardAdapter = new LeaderboardAdapter(this, R.layout.statistics_leaderboard_row, empty);
		setListAdapter(this.leaderboardAdapter);

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
	 * Method to setup the listview by running the setup in its own thread and use a progressDialog while it populates
	 * the listview.
	 */
	private void setupListView()
	{
		// Set leaderboard info to listview.
		setLeaderboardInfo = new Runnable()
		{
			@Override
			public void run()
			{
				getTeamInfoFromDatabase();
			}
		};
		Thread thread = new Thread(null, setLeaderboardInfo, "MagentoBackground");
		thread.start();
		progressDialog = ProgressDialog.show(LeaderboardActivity.this, "Please wait....", "Populating leaderboard",
				true);
	}

	/**
	 * Get the team information from the database.
	 */
	private void getTeamInfoFromDatabase()
	{
		teamDbAdapter.open();
		teams = teamDbAdapter.getAllValuesForGivenSport(TeamDbAdapter.KEY_TOTALPOINTS, sport);
		teamDbAdapter.close();
		runOnUiThread(setListViewValues);
	}

	private Runnable setListViewValues = new Runnable()
	{

		@Override
		public void run()
		{
			if (teams.size() > 0)
			{
				leaderboardAdapter.notifyDataSetChanged();
				// Add teaminfo to adapter.
				for (int i = 0; i < teams.size(); i++)
				{
					leaderboardAdapter.add(teams.get(i));
				}
			}
			// When done dismiss progress Dialog.
			progressDialog.dismiss();
			leaderboardAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * Custom adapter for leaderboard listview.
	 * 
	 * @author Jonathan
	 */
	private class LeaderboardAdapter extends ArrayAdapter<TeamInfo>
	{

		private ArrayList<TeamInfo> teams;

		public LeaderboardAdapter(Context context, int textViewResourceId, ArrayList<TeamInfo> teams)
		{
			super(context, textViewResourceId, teams);
			this.teams = teams;
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
				view = li.inflate(R.layout.statistics_leaderboard_row, null);

				// Set colour of rows.
				int colorPos = position % colors.length;
				view.setBackgroundColor(colors[colorPos]);
			}

			// Set TeamInfo for adapter.
			TeamInfo team = teams.get(position);
			if (team != null)
			{
				// Setup textviews
				TextView positionLeaderboard = (TextView) view.findViewById(R.id.position_leaderboard);
				TextView name = (TextView) view.findViewById(R.id.team_name_leaderboard);
				TextView totalPoints = (TextView) view.findViewById(R.id.total_points_leaderboard);
				TextView wins = (TextView) view.findViewById(R.id.wins_leaderboard);
				TextView draws = (TextView) view.findViewById(R.id.draws_leaderboard);
				TextView losses = (TextView) view.findViewById(R.id.losses_leaderboard);

				// Set text on textviews.
				int pos = position + 1;
				positionLeaderboard.setText("" + pos);
				name.setText(team.getName());
				totalPoints.setText(team.getTotalPoints() + " PTS");
				wins.setText(team.getWins() + " WINS");
				draws.setText(team.getDraws() + " DRAWS");
				losses.setText(team.getLosses() + " LOSSES");
			}
			return view;
		}
	}

	/**
	 * Actionbar click listener.
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
				i = new Intent(LeaderboardActivity.this, MainMenuActivity.class);
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
					i = new Intent(LeaderboardActivity.this, LoginActivity.class);
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
					Intent syncIntent = new Intent(LeaderboardActivity.this, SyncService.class);
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
				leaderboardAdapter.clear();
				setupListView();
				leaderboardAdapter.notifyDataSetChanged();
			}
			else if (item.equals("Gaelic"))
			{
				sport = "Gaelic";
				leaderboardAdapter.clear();
				setupListView();
				leaderboardAdapter.notifyDataSetChanged();
			}
			else if (item.equals("Hurling"))
			{
				sport = "Hurling";
				leaderboardAdapter.clear();
				setupListView();
				leaderboardAdapter.notifyDataSetChanged();
			}
			else if (item.equals("Rugby"))
			{
				sport = "Rugby";
				leaderboardAdapter.clear();
				setupListView();
				leaderboardAdapter.notifyDataSetChanged();
			}

		}

		public void onNothingSelected(AdapterView<?> parent)
		{
			// Do nothing.
		}
	}

}
