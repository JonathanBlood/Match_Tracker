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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jonathanbloodmatchtracker.database.TeamDbAdapter;
import com.jonathanbloodmatchtracker.main.MainMenuActivity;
import com.jonathanbloodmatchtracker.main.R;
import com.jonathanbloodmatchtracker.main.TeamInfo;

import java.util.ArrayList;

/**
 * Displays leaderboard in a listview.
 *
 * @author Jonathan
 */
public class LeaderboardActivity extends ListActivity {

    private LeaderboardAdapter leaderboardAdapter;
    private TeamDbAdapter teamDbAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<TeamInfo> teams;
    private String sport = "Football";
    private Runnable setListViewValues = new Runnable() {

        @Override
        public void run() {
            if (teams.size() > 0) {
                leaderboardAdapter.notifyDataSetChanged();
                // Add teaminfo to adapter.
                for (int i = 0; i < teams.size(); i++) {
                    leaderboardAdapter.add(teams.get(i));
                }
            }
            // When done dismiss progress Dialog.
            progressDialog.dismiss();
            leaderboardAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_leaderboard);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.leaderboard));

        // Actionbar Listener.
        ActionBarClickListener actionBClickListener = new ActionBarClickListener();
        findViewById(R.id.home_button).setOnClickListener(actionBClickListener);

        //Row colours of listview.

        // Sport spinner setup.
        Spinner sportSpinner = (Spinner) findViewById(R.id.sportSpinnerLeaderboard);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Sport_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(adapter);
        sportSpinner.setOnItemSelectedListener(new SportSpinnerItemSelectedListener());

        teamDbAdapter = new TeamDbAdapter(this);
        ArrayList<TeamInfo> empty = new ArrayList<>();
        teams = new ArrayList<>();
        this.leaderboardAdapter = new LeaderboardAdapter(this, R.layout.statistics_leaderboard_row, empty);
        setListAdapter(this.leaderboardAdapter);

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
        Runnable setLeaderboardInfo = new Runnable() {
            @Override
            public void run() {
                getTeamInfoFromDatabase();
            }
        };
        Thread thread = new Thread(null, setLeaderboardInfo, "MagentoBackground");
        thread.start();
        progressDialog = ProgressDialog.show(LeaderboardActivity.this, getString(R.string.pleaseWait), getString(R.string.populatingLeaderboards), true);
    }

    /**
     * Get the team information from the database.
     */
    private void getTeamInfoFromDatabase() {
        teamDbAdapter.open();
        try {
            teams = teamDbAdapter.getAllValuesForGivenSport(TeamDbAdapter.KEY_TOTALPOINTS, sport);
        } finally {
            teamDbAdapter.close();
        }
        runOnUiThread(setListViewValues);
    }

    /**
     * Custom adapter for leaderboard listview.
     *
     * @author Jonathan
     */
    private class LeaderboardAdapter extends ArrayAdapter<TeamInfo> {

        private ArrayList<TeamInfo> teams;

        public LeaderboardAdapter(Context context, int textViewResourceId, ArrayList<TeamInfo> teams) {
            super(context, textViewResourceId, teams);
            this.teams = teams;
        }

        /**
         * Overwrite getView method.
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.statistics_leaderboard_row, parent, false);
            }

            // Set TeamInfo for adapter.
            TeamInfo team = teams.get(position);
            if (team != null) {
                // Setup textviews
                TextView positionLeaderboard    = (TextView) view.findViewById(R.id.position_leaderboard);
                TextView name                   = (TextView) view.findViewById(R.id.team_name_leaderboard);
                TextView totalPoints            = (TextView) view.findViewById(R.id.total_points_leaderboard);
                TextView wins                   = (TextView) view.findViewById(R.id.wins_leaderboard);
                TextView draws                  = (TextView) view.findViewById(R.id.draws_leaderboard);
                TextView losses                 = (TextView) view.findViewById(R.id.losses_leaderboard);

                // Set text on textviews.
                int pos = position + 1;
                positionLeaderboard.setText(Integer.toString(pos));
                name.setText(team.getName());
                totalPoints.setText(Integer.toString(team.getTotalPoints()));
                wins.setText(Integer.toString(team.getWins()));
                draws.setText(Integer.toString(team.getDraws()));
                losses.setText(Integer.toString(team.getLosses()));
            }
            return view;
        }
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
                    i = new Intent(LeaderboardActivity.this, MainMenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }
            if (i != null) {
                startActivity(i);
            }
        }
    }

    // Spinner itemSelected listener.
    private class SportSpinnerItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            // Match event selection.
            String item = parent.getItemAtPosition(pos).toString();
            switch (item) {
                case "Sort by sport":
                    // Do nothing.
                    break;
                case "Football":
                    sport = getString(R.string.sports_football);
                    leaderboardAdapter.clear();
                    setupListView();
                    leaderboardAdapter.notifyDataSetChanged();
                    break;
                case "Gaelic":
                    sport = getString(R.string.sports_gaelic);
                    leaderboardAdapter.clear();
                    setupListView();
                    leaderboardAdapter.notifyDataSetChanged();
                    break;
                case "Hurling":
                    sport = getString(R.string.sports_hurling);
                    leaderboardAdapter.clear();
                    setupListView();
                    leaderboardAdapter.notifyDataSetChanged();
                    break;
                case "Rugby":
                    sport = getString(R.string.sports_Rugby);
                    leaderboardAdapter.clear();
                    setupListView();
                    leaderboardAdapter.notifyDataSetChanged();
                    break;
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

}
