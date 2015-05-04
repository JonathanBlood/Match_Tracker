package com.jonathanbloodmatchtracker.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jonathanbloodmatchtracker.database.MatchDbAdapter;
import com.jonathanbloodmatchtracker.database.TeamDbAdapter;
import com.jonathanbloodmatchtracker.main.MainMenuActivity;
import com.jonathanbloodmatchtracker.main.R;
import com.jonathanbloodmatchtracker.main.Utility;

import java.util.ArrayList;

/**
 * Activity to allow the user to enter pre match information such as teams,
 * venues ect.
 *
 * @author Jonathan
 */
public class HurlingPreMatchInfoActivity extends Activity {

    private AutoCompleteTextView team1AutoTV;
    private AutoCompleteTextView team2AutoTV;
    private AutoCompleteTextView venueAutoTV;
    private AutoCompleteTextView refereeNameAutoET;
    private AutoCompleteTextView leagueCupNameAutoTV;
    private EditText numOfPlayersET;
    private EditText minutesPerHalfET;
    private EditText goalValueET;
    private EditText pointValueET;
    private String type;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hurling_pre_match_info);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.hurling__pre_activity));

        // Actionbar Listener.
        ActionBarClickListener actionBClickListener = new ActionBarClickListener();
        findViewById(R.id.home_button).setOnClickListener(actionBClickListener);

        // Database adapter setup.
        MatchDbAdapter matchDbAdapter   = new MatchDbAdapter(this);
        TeamDbAdapter teamDbAdapter     = new TeamDbAdapter(this);

        // Get info from database for autocomplete input suggestions.
        // Team suggestions.
        teamDbAdapter.open();
        ArrayList<String> teamsAL   = teamDbAdapter.getColumnValuesStr(TeamDbAdapter.KEY_NAME, false);
        String[] teamsArr           = teamsAL.toArray(new String[teamsAL.size()]);
        teamDbAdapter.close();

        matchDbAdapter.open();
        // Referee suggestions.
        ArrayList<String> refAL     = matchDbAdapter.getColumnValues(MatchDbAdapter.KEY_REFEREE, false);
        String[] refereesArr        = refAL.toArray(new String[refAL.size()]);
        // Venue suggestions.
        ArrayList<String> venueAL   = matchDbAdapter.getColumnValues(MatchDbAdapter.KEY_VENUE, false);
        String[] venueArr           = venueAL.toArray(new String[venueAL.size()]);
        // League/Cup name suggestions.
        ArrayList<String> leagueCupNameAL   = matchDbAdapter.getColumnValues(MatchDbAdapter.KEY_TYPENAME, false);
        String[] leagueCupNameArr           = leagueCupNameAL.toArray(new String[leagueCupNameAL.size()]);
        matchDbAdapter.close();

        // Adapter setup for AutoCompleteTextViews.
        ArrayAdapter<String> teamAdapter            = new ArrayAdapter<>(this, R.layout.list_team, teamsArr);
        ArrayAdapter<String> venueAdapter           = new ArrayAdapter<>(this, R.layout.list_team, venueArr);
        ArrayAdapter<String> refereeAdapter         = new ArrayAdapter<>(this, R.layout.list_team, refereesArr);
        ArrayAdapter<String> leagueCupNameAdapter   = new ArrayAdapter<>(this, R.layout.list_team, leagueCupNameArr);

        // Team1 text view and input set up.
        team1AutoTV = (AutoCompleteTextView) findViewById(R.id.team1AutoTVHurling);
        team1AutoTV.setAdapter(teamAdapter);

        // Team2 text view and input set up.
        team2AutoTV = (AutoCompleteTextView) findViewById(R.id.team2AutoTVHurling);
        team2AutoTV.setAdapter(teamAdapter);

        // Venue input.
        venueAutoTV = (AutoCompleteTextView) findViewById(R.id.venueAutoTVHurling);
        venueAutoTV.setAdapter(venueAdapter);

        goalValueET         = (EditText) this.findViewById(R.id.goalValueETGaelic);
        pointValueET        = (EditText) this.findViewById(R.id.pointValueETGaelic);
        numOfPlayersET      = (EditText) this.findViewById(R.id.numOfPlayersETHurling);
        minutesPerHalfET    = (EditText) this.findViewById(R.id.minutesPerHalfETHurling);

        // Referee input
        refereeNameAutoET = (AutoCompleteTextView) findViewById(R.id.refereeNameAutoTVHurling);
        refereeNameAutoET.setAdapter(refereeAdapter);

        // League/Cup Name input.
        leagueCupNameAutoTV = (AutoCompleteTextView) findViewById(R.id.leagueCupNameAutoTVHurling);
        leagueCupNameAutoTV.setAdapter(leagueCupNameAdapter);

        type = getString(R.string.friendlyString);
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
     * Event type Selection (Cup, Friendly, League)
     * @param view Radio views
     */
    public void typeSelection(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.friendlyRadioBNHurling:
                if (checked) {
                    type = ((RadioButton) view).getText().toString();
                    leagueCupNameAutoTV.getText().clear();
                    leagueCupNameAutoTV.setEnabled(false);
                }
                break;
            case R.id.leagueRadioBNHurling:
                if (checked) {
                    type = ((RadioButton) view).getText().toString();
                    leagueCupNameAutoTV.setEnabled(true);
                }
                break;
            case R.id.cupRadioBNHurling:
                if (checked) {
                    type = ((RadioButton) view).getText().toString();
                    leagueCupNameAutoTV.setEnabled(true);
                }
                break;
        }
    }


    /**
     * Create match logic
     * @param view Create Match button
     */
    public void createMatch(View view) {

        // Check to see if all input elements validate correctly.
        if (validateForm()) {
            String team1            = team1AutoTV.getText().toString().trim();
            String team2            = team2AutoTV.getText().toString().trim();
            String venue            = venueAutoTV.getText().toString().trim();
            int goalValue           = Integer.parseInt(goalValueET.getText().toString().trim());
            int pointValue          = Integer.parseInt(pointValueET.getText().toString().trim());
            String leagueCupName    = leagueCupNameAutoTV.getText().toString().trim();
            String refereeName      = refereeNameAutoET.getText().toString().trim();
            int numOfPlayers        = Integer.parseInt(numOfPlayersET.getText().toString().trim());
            int minutesPerHalf      = Integer.parseInt(minutesPerHalfET.getText().toString().trim());

            // Launch match Activity and pass data to that Activity.
            Intent matchIntent = new Intent(HurlingPreMatchInfoActivity.this, HurlingMatchActivity.class);
            matchIntent.putExtra("Team1", team1);
            matchIntent.putExtra("Team2", team2);
            matchIntent.putExtra("Venue", venue);
            matchIntent.putExtra("GoalValue", goalValue);
            matchIntent.putExtra("PointValue", pointValue);
            matchIntent.putExtra("LeagueCupName", leagueCupName);
            matchIntent.putExtra("RefereeName", refereeName);
            matchIntent.putExtra("NumOfPlayers", numOfPlayers);
            matchIntent.putExtra("MinutesPerHalf", minutesPerHalf);
            matchIntent.putExtra("Type", type);
            startActivity(matchIntent);
        } else { // Form didn't validate proper.
            Toast.makeText(getApplicationContext(), getString(R.string.fixvalues), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Validate all the user inputs from this activity.
     *
     * @return true if all validation was successful.
     */
    private boolean validateForm() {
        Context context = getApplicationContext();
        return Utility.validateAutoTextViewValue(team1AutoTV, context) && Utility.validateAutoTextViewValue(team2AutoTV, context) &&
                Utility.validateEditTextValue(numOfPlayersET, context) && Utility.validateEditTextValue(minutesPerHalfET, context) &&
                Utility.validateEditTextValue(goalValueET, context) && Utility.validateEditTextValue(pointValueET, context);
    }

    /**
     * Actionbar listener
     */
    private class ActionBarClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.home_button:
                    i = new Intent(HurlingPreMatchInfoActivity.this, MainMenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }
            if (i != null) {
                startActivity(i);
            }
        }
    }

}
