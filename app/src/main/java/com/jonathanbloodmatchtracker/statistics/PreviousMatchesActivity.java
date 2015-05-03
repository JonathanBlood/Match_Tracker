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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.jonathanbloodmatchtracker.database.FactsDbAdapter;
import com.jonathanbloodmatchtracker.database.MatchDbAdapter;
import com.jonathanbloodmatchtracker.main.MainMenuActivity;
import com.jonathanbloodmatchtracker.main.MatchInfo;
import com.jonathanbloodmatchtracker.main.R;

import java.util.ArrayList;

public class PreviousMatchesActivity extends ListActivity {

    private PreviousMatchesAdapter prevMatchesAdapter;
    private MatchDbAdapter matchDbAdapter;
    private ProgressDialog progressDialog;
    private CheckBox dateCheckBox;
    private CheckBox teamNameCheckBox;
    private BootstrapButton submitBN;
    private ArrayList<MatchInfo> matches;
    private String sport        = "Football";
    private boolean date        = true; // boolean if to sort query by date.
    private boolean teamName    = false; // boolean if to sort query by teamName.

    private Runnable setListViewValues = new Runnable() {

        @Override
        public void run() {
            if (matches.size() > 0) {
                prevMatchesAdapter.notifyDataSetChanged();
                // Add teaminfo to adapter.
                for (int i = 0; i < matches.size(); i++) {
                    prevMatchesAdapter.add(matches.get(i));
                }
            }
            // When done dismiss progress Dialog.
            progressDialog.dismiss();
            prevMatchesAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_previous_matches);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.previousMatches));

        // Actionbar Listener.
        ActionBarClickListener actionBClickListener = new ActionBarClickListener();
        findViewById(R.id.home_button).setOnClickListener(actionBClickListener);

        // Sport spinner setup.
        Spinner sportSpinner                = (Spinner) findViewById(R.id.sportSpinnerPrevMatches);
        ArrayAdapter<CharSequence> adapter  = ArrayAdapter.createFromResource(this, R.array.Sport_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(adapter);
        sportSpinner.setOnItemSelectedListener(new SportSpinnerItemSelectedListener());

        // Checkbox and submit button setup.
        dateCheckBox        = (CheckBox) findViewById(R.id.dateCheckBoxPrevMatches);
        teamNameCheckBox    = (CheckBox) findViewById(R.id.teamNameCheckBoxPrevMatches);
        submitBN            = (BootstrapButton) this.findViewById(R.id.submitBNPrevMatches);
        this.submitButton();

        matchDbAdapter              = new MatchDbAdapter(this);
        ArrayList<MatchInfo> empty  = new ArrayList<>();
        matches                     = new ArrayList<>();
        this.prevMatchesAdapter     = new PreviousMatchesAdapter(this, R.layout.statistics_previous_matches_row, empty);
        setListAdapter(this.prevMatchesAdapter);

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
     * On item clicked from listview.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
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
     * Method to setup the listview by running the setup in its own thread and
     * use a progressDialog while it populates the listview.
     */
    private void setupListView() {
        // Set prev match info to listview.
        Runnable setPrevMatchInfo = new Runnable() {
            @Override
            public void run() {
                getMatchInfoFromDatabase();
            }
        };
        Thread thread = new Thread(null, setPrevMatchInfo, "MagentoBackground");
        thread.start();
        progressDialog = ProgressDialog.show(PreviousMatchesActivity.this, getString(R.string.pleaseWait), getString(R.string.populatingMatches), true);
    }

    /**
     * Get the match information from the database.
     */
    private void getMatchInfoFromDatabase() {
        matchDbAdapter.open();
        try {
            matches = matchDbAdapter.getPrevMatches(date, teamName, sport);
        } finally {
            matchDbAdapter.close();
        }

        runOnUiThread(setListViewValues);
    }

    /**
     * Undo event button listener method.
     */
    private void submitButton() {
        submitBN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Checkbox selection and submit new listview query.
                date        = dateCheckBox.isChecked();
                teamName    = teamNameCheckBox.isChecked();

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
    private class PreviousMatchesAdapter extends ArrayAdapter<MatchInfo> {
        private ArrayList<MatchInfo> matches;
        private FactsDbAdapter factsDbAdapter;

        public PreviousMatchesAdapter(Context context, int textViewResourceId, ArrayList<MatchInfo> matches) {
            super(context, textViewResourceId, matches);
            this.matches    = matches;
            factsDbAdapter  = new FactsDbAdapter(context);
        }

        /**
         * Overwrite getView method.
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.statistics_previous_matches_row, parent, false);
            }

            // Set MatchInfo for adapter.
            MatchInfo match = matches.get(position);
            if (match != null) {
                // Setup textviews
                TextView team1Name  = (TextView) view.findViewById(R.id.team1_name_prevMatches);
                TextView team2Name  = (TextView) view.findViewById(R.id.team2_name_prevMatches);
                TextView date       = (TextView) view.findViewById(R.id.date_prevMatches);
                TextView team1Score = (TextView) view.findViewById(R.id.team1_score_prevMatches);
                TextView team2Score = (TextView) view.findViewById(R.id.team2_score_prevMatches);

                // Set text on textviews.
                team1Name.setText(match.getTeam1());
                team2Name.setText(match.getTeam2());
                date.setText(match.getDate());

                // Set the scores which depend on sport type.
                String sport            = match.getSport();
                int match_id            = match.getMatchID();
                String goalStr          = getString(R.string.event_goal);
                String pointStr         = getString(R.string.event_point);
                String tryStr           = getString(R.string.event_try);
                String conversionStr    = getString(R.string.event_conversion);
                String penaltyStr       = getString(R.string.event_penalty);
                String dropGoalStr      = getString(R.string.event_dropgoal);

                factsDbAdapter.open();
                try {
                    switch (sport) {
                        case "Football":
                            team1Score.setText(Integer.toString(factsDbAdapter.countTypesWithMatchID(match_id, goalStr, match.getTeam1())));
                            team2Score.setText(Integer.toString(factsDbAdapter.countTypesWithMatchID(match_id, goalStr, match.getTeam2())));
                            break;
                        case "Gaelic":
                        case "Hurling":
                            int goal1 = factsDbAdapter.countTypesWithMatchID(match_id, goalStr, match.getTeam1());
                            int goal2 = factsDbAdapter.countTypesWithMatchID(match_id, goalStr, match.getTeam2());
                            int point1 = factsDbAdapter.countTypesWithMatchID(match_id, pointStr, match.getTeam1());
                            int point2 = factsDbAdapter.countTypesWithMatchID(match_id, pointStr, match.getTeam2());
                            team1Score.setText(goal1 + ":" + point1);
                            team2Score.setText(goal2 + ":" + point2);
                            break;
                        case "Rugby":
                            int team1TotalScore;
                            int team2TotalScore;
                            int trySc;
                            int conversion;
                            int penalty;
                            int dropGoal;

                            // Team1 get total score.
                            trySc = factsDbAdapter.countTypesWithMatchID(match_id, tryStr, match.getTeam1());
                            conversion = factsDbAdapter.countTypesWithMatchID(match_id, conversionStr, match.getTeam1());
                            penalty = factsDbAdapter.countTypesWithMatchID(match_id, penaltyStr, match.getTeam1());
                            dropGoal = factsDbAdapter.countTypesWithMatchID(match_id, dropGoalStr, match.getTeam1());
                            team1TotalScore = (trySc * 5) + (conversion * 2) + (penalty * 3) + (dropGoal * 3);

                            // Team2 get total score.
                            trySc = factsDbAdapter.countTypesWithMatchID(match_id, tryStr, match.getTeam2());
                            conversion = factsDbAdapter.countTypesWithMatchID(match_id, conversionStr, match.getTeam2());
                            penalty = factsDbAdapter.countTypesWithMatchID(match_id, penaltyStr, match.getTeam2());
                            dropGoal = factsDbAdapter.countTypesWithMatchID(match_id, dropGoalStr, match.getTeam2());
                            team2TotalScore = (trySc * 5) + (conversion * 2) + (penalty * 3) + (dropGoal * 3);

                            team1Score.setText(Integer.toString(team1TotalScore));
                            team2Score.setText(Integer.toString(team2TotalScore));
                            break;
                    }
                } finally {
                    factsDbAdapter.close();
                }
            }
            return view;
        }
    }

    // Actionbar onClick Listener.
    private class ActionBarClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.home_button:
                    i = new Intent(PreviousMatchesActivity.this, MainMenuActivity.class);
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
                    break;
                case "Gaelic":
                    sport =  getString(R.string.sports_gaelic);
                    break;
                case "Hurling":
                    sport =  getString(R.string.sports_hurling);
                    break;
                case "Rugby":
                    sport =  getString(R.string.sports_Rugby);
                    break;
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

}
