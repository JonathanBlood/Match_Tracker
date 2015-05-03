package com.jonathanbloodmatchtracker.record;

import android.app.Activity;
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

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.jonathanbloodmatchtracker.database.MatchDbAdapter;
import com.jonathanbloodmatchtracker.database.TeamDbAdapter;
import com.jonathanbloodmatchtracker.main.MainMenuActivity;
import com.jonathanbloodmatchtracker.main.R;

import java.util.ArrayList;


/**
 * Activity to allow the user to enter pre match information such as teams,
 * venues ect.
 *
 * @author Jonathan
 */
public class RugbyPreMatchInfoActivity extends Activity {

    /**
     * On clock listen for radio box selection.
     */
    RadioButton.OnClickListener radioBoxOnClickListener = new RadioButton.OnClickListener() {

        @Override
        public void onClick(View v) {

            // Logic to enable/disable league/cup Name autoTV.
            if (friendlyRadioBN.isChecked()) {
                leagueCupNameAutoTV.setEnabled(false);
            } else if (leagueRadioBN.isChecked()) {
                leagueCupNameAutoTV.setEnabled(true);
            } else {
                leagueCupNameAutoTV.setEnabled(true);
            }
        }

    };
    private RadioButton friendlyRadioBN;
    private RadioButton leagueRadioBN;
    private RadioButton cupRadioBN;
    private AutoCompleteTextView team1AutoTV;
    private AutoCompleteTextView team2AutoTV;
    private AutoCompleteTextView venueAutoTV;
    private AutoCompleteTextView refereeNameAutoET;
    private AutoCompleteTextView leagueCupNameAutoTV;
    private EditText numOfPlayersET;
    private EditText minutesPerHalfET;
    private EditText tryValueET;
    private EditText conversionValueET;
    private EditText penaltyValueET;
    private EditText dropGoalValueET;
    private BootstrapButton createMatchBN;
    private int numOfPlayers;
    private int minutesPerHalf;
    private int tryValue;
    private int conversionValue;
    private int penaltyValue;
    private int dropGoalValue;
    private String team1;
    private String team2;
    private String venue;
    private String refereeName;
    private String type;
    private String leagueCupName;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rugby_pre_match_info);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.rugby__pre_activity));

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

        ArrayAdapter<String> teamAdapter            = new ArrayAdapter<>(this, R.layout.list_team, teamsArr);
        ArrayAdapter<String> venueAdapter           = new ArrayAdapter<>(this, R.layout.list_team, venueArr);
        ArrayAdapter<String> refereeAdapter         = new ArrayAdapter<>(this, R.layout.list_team, refereesArr);
        ArrayAdapter<String> leagueCupNameAdapter   = new ArrayAdapter<>(this, R.layout.list_team, leagueCupNameArr);

        // Team1 text view and input set up.
        team1AutoTV = (AutoCompleteTextView) findViewById(R.id.team1AutoTVRugby);
        team1AutoTV.setAdapter(teamAdapter);

        // Team2 text view and input set up.
        team2AutoTV = (AutoCompleteTextView) findViewById(R.id.team2AutoTVRugby);
        team2AutoTV.setAdapter(teamAdapter);

        // Venue input.
        venueAutoTV = (AutoCompleteTextView) findViewById(R.id.venueAutoTVRugby);
        venueAutoTV.setAdapter(venueAdapter);

        tryValueET          = (EditText) this.findViewById(R.id.tryValueETRugby);
        conversionValueET   = (EditText) this.findViewById(R.id.conversionValueETRugby);
        penaltyValueET      = (EditText) this.findViewById(R.id.penaltyValueETRugby);
        dropGoalValueET     = (EditText) this.findViewById(R.id.dropGoalValueETRugby);
        numOfPlayersET      = (EditText) this.findViewById(R.id.numOfPlayersETRugby);
        minutesPerHalfET    = (EditText) this.findViewById(R.id.minutesPerHalfETRugby);

        // Referee input
        refereeNameAutoET = (AutoCompleteTextView) findViewById(R.id.refereeNameAutoTVRugby);
        refereeNameAutoET.setAdapter(refereeAdapter);

        // Type input selection
        friendlyRadioBN     = (RadioButton) this.findViewById(R.id.friendlyRadioBNRugby);
        leagueRadioBN       = (RadioButton) this.findViewById(R.id.leagueRadioBNRugby);
        cupRadioBN          = (RadioButton) this.findViewById(R.id.cupRadioBNRugby);
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
        createMatchBN = (BootstrapButton) this.findViewById(R.id.createMatchBNRugby);
        this.createMatchButton();

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
     * Create match button listener method.
     */
    private void createMatchButton() {
        createMatchBN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check to see if all input elements validate correctly.
                if (validateForm()) {
                    team1               = team1AutoTV.getText().toString().trim();
                    team2               = team2AutoTV.getText().toString().trim();
                    venue               = venueAutoTV.getText().toString().trim();
                    tryValue            = Integer.parseInt(tryValueET.getText().toString().trim());
                    conversionValue     = Integer.parseInt(conversionValueET.getText().toString().trim());
                    penaltyValue        = Integer.parseInt(penaltyValueET.getText().toString().trim());
                    dropGoalValue       = Integer.parseInt(dropGoalValueET.getText().toString().trim());
                    numOfPlayers        = Integer.parseInt(numOfPlayersET.getText().toString().trim());
                    minutesPerHalf      = Integer.parseInt(minutesPerHalfET.getText().toString().trim());
                    leagueCupName       = leagueCupNameAutoTV.getText().toString().trim();
                    refereeName         = refereeNameAutoET.getText().toString().trim();

                    if (friendlyRadioBN.isChecked()) {
                        type = friendlyRadioBN.getText().toString();
                    } else if (leagueRadioBN.isChecked()) {
                        type = leagueRadioBN.getText().toString();
                    } else {
                        type = cupRadioBN.getText().toString();
                    }

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
                } else { // Form didn't validate proper.
                    Toast.makeText(getApplicationContext(), getString(R.string.fixvalues), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /**
     * Validate all the user inputs from this activity.
     *
     * @return true if all validation was successful.
     */
    private boolean validateForm() {
        return validateAutoTextViewValue(team1AutoTV) && validateAutoTextViewValue(team2AutoTV) &&
                validateEditTextValue(numOfPlayersET) && validateEditTextValue(minutesPerHalfET) &&
                validateEditTextValue(tryValueET) && validateEditTextValue(conversionValueET) &&
                validateEditTextValue(penaltyValueET) && validateEditTextValue(dropGoalValueET);
    }

    /**
     * Validate autoCompleteTextView - non empty.
     *
     * @param autoCompleteTextView text
     * @return boolean true if validation was successful.
     */
    private boolean validateAutoTextViewValue(AutoCompleteTextView autoCompleteTextView) {
        String value = autoCompleteTextView.getText().toString();
        if (value.length() == 0) {
            autoCompleteTextView.setError(getString(R.string.validate_name));
            return false;
        }
        return true;
    }

    /**
     * Validate EditText - value is non empty and int.
     *
     * @param editText text
     * @return boolean true if validation was successful.
     */
    private boolean validateEditTextValue(EditText editText) {
        String value = editText.getText().toString();

        // Check for non empty.
        if (value.length() == 0) {
            editText.setError(getString(R.string.validate_number));
            return false;
        }

        // Check that value is an int.
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Actionbar onClick Listener.
    private class ActionBarClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.home_button:
                    i = new Intent(RugbyPreMatchInfoActivity.this, MainMenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }
            if (i != null) {
                startActivity(i);
            }
        }
    }

}
