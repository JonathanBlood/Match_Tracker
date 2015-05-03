package com.jonathanbloodmatchtracker.statistics;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jonathanbloodmatchtracker.database.FactsDbAdapter;
import com.jonathanbloodmatchtracker.main.MainMenuActivity;
import com.jonathanbloodmatchtracker.main.PlayerStats;
import com.jonathanbloodmatchtracker.main.R;

import java.util.ArrayList;

/**
 * Displays player stats in a list view.
 *
 * @author Jonathan
 */
public class PlayerStatsActivity extends ListActivity {

    private PlayerStatsAdapter playerStatsAdapter;
    private FactsDbAdapter factsDbAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<String> players;
    private ArrayList<PlayerStats> playerStats;

    /**
     * Set the listview values.
     */
    private Runnable setListViewValues = new Runnable() {

        @Override
        public void run() {
            if (playerStats.size() > 0) { // Add playerStats to listview.
                playerStatsAdapter.notifyDataSetChanged();
                // Add player info to adapter.
                for (int z = 0; z < playerStats.size(); z++) {
                    PlayerStats player = playerStats.get(z);

                    if (!"".equals(player.getName()))// If player name is not empty.
                        playerStatsAdapter.add(player);
                }
            }
            // When done dismiss progress Dialog.
            progressDialog.dismiss();
            playerStatsAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_player_stats);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.playerStatistics));

        // Actionbar Listener.
        ActionBarClickListener actionBClickListener = new ActionBarClickListener();
        findViewById(R.id.home_button).setOnClickListener(actionBClickListener);

        //Row colours of listview.
        factsDbAdapter                  = new FactsDbAdapter(this);
        players                         = new ArrayList<>();
        playerStats                     = new ArrayList<>();
        ArrayList<PlayerStats> empty    = new ArrayList<>();
        this.playerStatsAdapter         = new PlayerStatsAdapter(this, R.layout.statistics_player_stats_row, empty);
        setListAdapter(this.playerStatsAdapter);

        // Set up the ListView.
        this.setupListView();
    }

    /**
     * Called when activity is called back into foreground.
     */
    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Called when activity is called back into foreground.
     */
    @Override
    public void onRestart() {
        super.onRestart();
    }

    /**
     * Method to setup the listview by running the setup in its own thread and
     * use a progressDialog while it populates the listview.
     */
    private void setupListView() {
        // Set leaderboard info to listview.
        Runnable setPlayerStatsInfo = new Runnable() {
            @Override
            public void run() {
                getPlayerInfoFromDatabase();
            }
        };
        Thread thread = new Thread(null, setPlayerStatsInfo, "MagentoBackground");
        thread.start();
        progressDialog = ProgressDialog.show(PlayerStatsActivity.this, getString(R.string.pleaseWait), getString(R.string.populatingPlayers), true);
    }

    /**
     * Get the player information from the database.
     */
    private void getPlayerInfoFromDatabase() {
        factsDbAdapter.open();
        try {
            players = factsDbAdapter.getColumnValues(FactsDbAdapter.KEY_PLAYER1, false);
            // Go through players and get info needed to calculate player rank.
            for (int i = 0; i < players.size(); i++) {
                String player = players.get(i);
                int goals = factsDbAdapter.countTypesWithPlayer(getString(R.string.event_goal), player);
                int points = factsDbAdapter.countTypesWithPlayer(getString(R.string.event_point), player);
                int tryValues = factsDbAdapter.countTypesWithPlayer(getString(R.string.event_try), player);
                int conversions = factsDbAdapter.countTypesWithPlayer(getString(R.string.event_conversion), player);
                int penaltys = factsDbAdapter.countTypesWithPlayer(getString(R.string.event_penalty), player);
                int dropGoals = factsDbAdapter.countTypesWithPlayer(getString(R.string.event_dropgoal), player);

                // Now we add the player and it's player rank to the PlayerRank
                // Arraylist.
                playerStats.add(new PlayerStats(player, goals, points, tryValues, conversions, dropGoals, penaltys));
            }
        } finally {
            factsDbAdapter.close();
        }
        runOnUiThread(setListViewValues);
    }

    /**
     * Actionbar click listener.
     *
     * @author Jonathan
     */
    private class ActionBarClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.home_button:
                    i = new Intent(PlayerStatsActivity.this, MainMenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }
            if (i != null) {
                startActivity(i);
            }
        }
    }

    /**
     * Custom adapter for player stats listview.
     *
     * @author Jonathan
     */
    private class PlayerStatsAdapter extends ArrayAdapter<PlayerStats> {

        private ArrayList<PlayerStats> playerStats;

        public PlayerStatsAdapter(Context context, int textViewResourceId, ArrayList<PlayerStats> playerStats) {
            super(context, textViewResourceId, playerStats);
            this.playerStats = playerStats;
        }

        /**
         * Overwrite getView method.
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.statistics_player_stats_row, parent, false);
            }

            // Set TeamInfo for adapter.
            PlayerStats player = playerStats.get(position);
            if (player != null) {
                // Setup textviews
                TextView playerNameTV   = (TextView) view.findViewById(R.id.player_name_playerStat);
                TextView goalsTV        = (TextView) view.findViewById(R.id.goals_playerStat);
                TextView pointsTV       = (TextView) view.findViewById(R.id.points_playerStat);
                TextView triesTV        = (TextView) view.findViewById(R.id.tries_playerStat);
                TextView conversionsTV  = (TextView) view.findViewById(R.id.conversions_playerStat);
                TextView dropGoalsTV    = (TextView) view.findViewById(R.id.dropGoals_playerStat);
                TextView penaltiesTV    = (TextView) view.findViewById(R.id.penalties_playerStat);

                // Set text for each text view.
                playerNameTV.setText(player.getName());
                goalsTV.setText(player.getGoals() + " " + getString(R.string.event_goal));
                pointsTV.setText(player.getPoints() + " " + getString(R.string.event_point));
                triesTV.setText(player.getTries() + " " + getString(R.string.event_try));
                conversionsTV.setText(player.getConversions() + " " + getString(R.string.event_conversion));
                dropGoalsTV.setText(player.getDropGoals() + " " + getString(R.string.event_dropgoal));
                penaltiesTV.setText(player.getPenalties() + " " + getString(R.string.event_penalty));
            }
            return view;
        }
    }

}
