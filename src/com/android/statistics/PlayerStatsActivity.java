package com.android.statistics;

import java.util.ArrayList;

import com.android.database.FactsDbAdapter;
import com.android.main.Actionbar;
import com.android.main.LoginActivity;
import com.android.main.MainMenuActivity;
import com.android.main.PlayerStats;
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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays player stats in a list view.
 * 
 * @author Jonathan
 * 
 */
public class PlayerStatsActivity extends ListActivity
{
	
	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionBarOperations = new Actionbar(this);

	// Login information
	private boolean loggedIn = false;
	private PlayerStatsAdapter playerStatsAdapter;
	private FactsDbAdapter factsDbAdapter;
	private Runnable setPlayerStatsInfo;
	private ProgressDialog progressDialog;
	private ArrayList<String> players;
	private ArrayList<PlayerStats> playerStats;
	private ArrayList<PlayerStats> empty;
	private int[] colors;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_player_stats);

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
		
		factsDbAdapter = new FactsDbAdapter(this);
		players = new ArrayList<String>();
		playerStats = new ArrayList<PlayerStats>();
		empty = new ArrayList<PlayerStats>();
		this.playerStatsAdapter = new PlayerStatsAdapter(this, R.layout.statistics_player_stats_row, empty);
		setListAdapter(this.playerStatsAdapter);

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
		setPlayerStatsInfo = new Runnable()
		{
			@Override
			public void run()
			{
				getPlayerInfoFromDatabase();
			}
		};
		Thread thread = new Thread(null, setPlayerStatsInfo, "MagentoBackground");
		thread.start();
		progressDialog = ProgressDialog.show(PlayerStatsActivity.this, "Please wait....", "Populating Players", true);
	}

	/**
	 * Get the player information from the database.
	 */
	private void getPlayerInfoFromDatabase()
	{
		factsDbAdapter.open();
		players = factsDbAdapter.getColumnValues(FactsDbAdapter.KEY_PLAYER1, false);
		// Go through players and get info needed to calculate player rank.
		for (int i = 0; i < players.size(); i++)
		{
			String player = players.get(i);
			int goals = factsDbAdapter.countTypesWithPlayer("Goal", player);
			int points = factsDbAdapter.countTypesWithPlayer("Point", player);
			int tryValues = factsDbAdapter.countTypesWithPlayer("Try", player);
			int conversions = factsDbAdapter.countTypesWithPlayer("Conversion", player);
			int penaltys = factsDbAdapter.countTypesWithPlayer("Penalty", player);
			int dropGoals = factsDbAdapter.countTypesWithPlayer("Drop Goal", player);

			// Now we add the player and it's player rank to the PlayerRank
			// Arraylist.
			playerStats.add(new PlayerStats(player, goals, points, tryValues, conversions, dropGoals, penaltys));
		}
		factsDbAdapter.close();
		runOnUiThread(setListViewValues);
	}

	/**
	 * Set the listview values.
	 */
	private Runnable setListViewValues = new Runnable()
	{

		@Override
		public void run()
		{
			if (playerStats.size() > 0)
			{ // Add playerStats to listview.
				playerStatsAdapter.notifyDataSetChanged();
				// Add player info to adapter.
				for (int z = 0; z < playerStats.size(); z++)
				{
					PlayerStats player = playerStats.get(z);

					if (!"".equals(player.getName()))// If player name is not
						// empty.
						playerStatsAdapter.add(player);
				}
			}
			// When done dismiss progress Dialog.
			progressDialog.dismiss();
			playerStatsAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * Actionbar click listener.
	 * 
	 * @author Jonathan
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
				i = new Intent(PlayerStatsActivity.this, MainMenuActivity.class);
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
					i = new Intent(PlayerStatsActivity.this, LoginActivity.class);
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
					Intent syncIntent = new Intent(PlayerStatsActivity.this, SyncService.class);
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
	 * Custom adapter for player stats listview.
	 * 
	 * @author Jonathan
	 */
	private class PlayerStatsAdapter extends ArrayAdapter<PlayerStats>
	{

		private ArrayList<PlayerStats> playerStats;

		public PlayerStatsAdapter(Context context, int textViewResourceId, ArrayList<PlayerStats> playerStats)
		{
			super(context, textViewResourceId, playerStats);
			this.playerStats = playerStats;
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
				view = li.inflate(R.layout.statistics_player_stats_row, null);

				// Set colour of rows.
				int colorPos = position % colors.length;
				view.setBackgroundColor(colors[colorPos]);
			}

			// Set TeamInfo for adapter.
			PlayerStats player = playerStats.get(position);
			if (player != null)
			{
				// Setup textviews
				TextView playerNameTV = (TextView) view.findViewById(R.id.player_name_playerStat);
				TextView goalsTV = (TextView) view.findViewById(R.id.goals_playerStat);
				TextView pointsTV = (TextView) view.findViewById(R.id.points_playerStat);
				TextView triesTV = (TextView) view.findViewById(R.id.tries_playerStat);
				TextView conversionsTV = (TextView) view.findViewById(R.id.conversions_playerStat);
				TextView dropGoalsTV = (TextView) view.findViewById(R.id.dropGoals_playerStat);
				TextView penaltiesTV = (TextView) view.findViewById(R.id.penalties_playerStat);

				// Set text for each text view.
				playerNameTV.setText(player.getName());
				goalsTV.setText(player.getGoals() + " Goal");
				pointsTV.setText(player.getPoints() + " Point");
				triesTV.setText(player.getTries() + " Try");
				conversionsTV.setText(player.getConversions() + " Conversion");
				dropGoalsTV.setText(player.getDropGoals() + " Drop Goal");
				penaltiesTV.setText(player.getPenalties() + " Penalty");
			}
			return view;
		}
	}

}
