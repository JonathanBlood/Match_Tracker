package com.jonathanbloodmatchtracker.statistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jonathanbloodmatchtracker.database.FactsDbAdapter;
import com.jonathanbloodmatchtracker.main.MainMenuActivity;
import com.jonathanbloodmatchtracker.main.MatchEvent;
import com.jonathanbloodmatchtracker.main.R;

import java.util.ArrayList;

/**
 * Class to show Match information in an activity.
 *
 * @author Jonathan
 */
public class MatchActivity extends Activity {

    private int match_id;
    private String sport;
    private FactsDbAdapter factsDbAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_match);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.previousMatch));

        // Actionbar Listener.
        ActionBarClickListener actionBClickListener = new ActionBarClickListener();
        findViewById(R.id.home_button).setOnClickListener(actionBClickListener);

        // Retrieve Match Parameters.
        Intent getParameters    = getIntent();
        match_id                = getParameters.getIntExtra("match_id", 0);
        String date             = getParameters.getStringExtra("date");
        sport                   = getParameters.getStringExtra("sport");
        String team1            = getParameters.getStringExtra("team1");
        String team2            = getParameters.getStringExtra("team2");
        String venue            = getParameters.getStringExtra("venue");
        String type             = getParameters.getStringExtra("type");
        String type_name        = getParameters.getStringExtra("type_name");
        String referee          = getParameters.getStringExtra("referee");

        // DatabaseAdapter for match events.
        factsDbAdapter = new FactsDbAdapter(this);

        // Textview setup.
        TextView team1NameTV    = (TextView) this.findViewById(R.id.team1NameStatMatch);
        TextView team2NameTV    = (TextView) this.findViewById(R.id.team2NameStatMatch);
        TextView team1ScoreTV   = (TextView) this.findViewById(R.id.team1ScoreStatMatch);
        TextView team2ScoreTV   = (TextView) this.findViewById(R.id.team2ScoreStatMatch);
        TextView matchIDTV      = (TextView) this.findViewById(R.id.matchIDStatMatch);
        TextView dateTV         = (TextView) this.findViewById(R.id.dateStatMatch);
        TextView sportTV        = (TextView) this.findViewById(R.id.sportStatMatch);
        TextView venueTV        = (TextView) this.findViewById(R.id.venueStatMatch);
        TextView refereeTV      = (TextView) this.findViewById(R.id.refereeStatMatch);
        TextView typeTV         = (TextView) this.findViewById(R.id.typeStatMatch);
        TextView typeNameTV     = (TextView) this.findViewById(R.id.typeNameStatMatch);
        TextView matchEventsTV  = (TextView) this.findViewById(R.id.matchEventsStatMatch);

        // Set text to textview.
        team1NameTV.setText(team1);
        team2NameTV.setText(team2);
        team1ScoreTV.setText(calculateScore(team1));
        team2ScoreTV.setText(calculateScore(team2));
        matchIDTV.setText(Integer.toString(match_id));
        dateTV.setText(date);
        sportTV.setText(sport);
        venueTV.setText(venue);
        refereeTV.setText(referee);
        typeTV.setText(type);
        typeNameTV.setText(type_name);
        String matchFactsStr = "";
        factsDbAdapter.open();
        ArrayList<MatchEvent> matchEvents = factsDbAdapter.getAllFactsForGivenMatchID(match_id);
        factsDbAdapter.close();
        for (int i = 0; i < matchEvents.size(); i++) {
            MatchEvent matchEvt = matchEvents.get(i);
            matchFactsStr += matchEvt.toString();
        }
        matchEventsTV.setText(matchFactsStr);

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
     * Calculate a teams score.
     *
     * @param aTeam Team name
     * @return score
     */
    private String calculateScore(String aTeam) {
        String score = "";
        factsDbAdapter.open();
        try {
            switch (sport) {
                case "Football":
                    score = "" + factsDbAdapter.countTypesWithMatchID(match_id, getString(R.string.event_goal), aTeam);
                    break;
                case "Gaelic":
                case "Hurling":
                    int goal = factsDbAdapter.countTypesWithMatchID(match_id, getString(R.string.event_goal), aTeam);
                    int point = factsDbAdapter.countTypesWithMatchID(match_id, getString(R.string.event_point), aTeam);
                    score = goal + ":" + point;

                    break;
                case "Rugby":  // If sport is rugby set
                    // score.
                    int totalScore;
                    int trySc;
                    int conversion;
                    int penalty;
                    int dropGoal;

                    // Team1 get total score.
                    trySc = factsDbAdapter.countTypesWithMatchID(match_id, getString(R.string.event_try), aTeam);
                    conversion = factsDbAdapter.countTypesWithMatchID(match_id, getString(R.string.event_conversion), aTeam);
                    penalty = factsDbAdapter.countTypesWithMatchID(match_id, getString(R.string.event_penalty), aTeam);
                    dropGoal = factsDbAdapter.countTypesWithMatchID(match_id, getString(R.string.event_dropgoal), aTeam);
                    totalScore = (trySc * 5) + (conversion * 2) + (penalty * 3) + (dropGoal * 3);
                    score = Integer.toString(totalScore);
                    break;
            }
        } finally {
            factsDbAdapter.close();
        }
        return score;
    }

    /**
     * Actionbar onClick Listener.
     *
     * @author Jonathan
     */
    private class ActionBarClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.home_button:
                    i = new Intent(MatchActivity.this, MainMenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }
            if (i != null) {
                startActivity(i);
            }
        }
    }

}
