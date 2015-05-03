package com.jonathanbloodmatchtracker.record;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.jonathanbloodmatchtracker.database.FactsDbAdapter;
import com.jonathanbloodmatchtracker.database.MatchDbAdapter;
import com.jonathanbloodmatchtracker.database.TeamDbAdapter;
import com.jonathanbloodmatchtracker.main.MainMenuActivity;
import com.jonathanbloodmatchtracker.main.MatchEvent;
import com.jonathanbloodmatchtracker.main.R;

import java.util.ArrayList;

/**
 * This class is responsible for the recording of Football matches.
 *
 * @author Jonathan
 */
public class FootballMatchActivity extends Activity {

    private static final String SPORT   = "Football";
    private static final String WIN     = "Win";
    private static final String LOSS    = "Loss";
    private static final String DRAW    = "Draw";
    boolean firstHalf                   = true;

    private BootstrapButton startBn;
    private BootstrapButton stopBn;
    private BootstrapButton undoEventBn;
    private BootstrapButton finishMatchBn;
    private TextView matchInfoTV;
    private TextView team1ScoreTV;
    private TextView team2ScoreTV;
    private Chronometer matchChron; //Match timer.
    // One player event dialog variables.
    private Dialog onePlayerEventDG;
    private RadioButton team1EventRBN;
    private RadioButton team2EventRBN;
    private AutoCompleteTextView playerOneNameEventAutoTV;
    private ArrayAdapter<String> playerAdapter;
    private String[] players;
    // Two player event dialog variables.
    private Dialog twoPlayerEventDG;
    private RadioButton team1EventTwoRBN;
    private RadioButton team2EventTwoRBN;
    private AutoCompleteTextView playerOneNameEventTwoAutoTV;
    private AutoCompleteTextView playerTwoNameEventTwoAutoTV;
    // Stop watch variables.
    private long timeElapsed = 0; //Used to keep track of the time elapsed since timer started.
    private boolean isPausedFirstRun = false, isPaused = false;
    private String currTime = "";   //String representation of the current time.
    private long currTimeMilli = 0; //Variable to hold the current time in milliseconds.
    // Match info variables.
    private String team1;
    private String team2;
    private String venue;
    private String refereeName;
    private String type;
    private String leagueCupName;
    private String matchInfo = "";
    private String eventTime;
    private String eventTeamSel;
    private String eventPlayer1Name;
    private String eventPlayer2Name;
    private String eventType;
    private String halfTime;
    private String fullTime;
    private int team1OverallScore = 0;
    private int team2OverallScore = 0;

    private ArrayList<MatchEvent> matchEvents; //Store the match events.
    private MatchDbAdapter matchDbAdapter; //Database adapter for Match table.
    private FactsDbAdapter factsDbAdapter; //Database adapter for Facts table.
    private TeamDbAdapter teamDbAdapter; //Database adapter for Team table.

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.football_match);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.football_activity));

        //Actionbar Listener.
        ActionBarClickListener actionBClickListener = new ActionBarClickListener();
        findViewById(R.id.home_button).setOnClickListener(actionBClickListener);

        // Retrieve Parameters.
        Intent getParameters    = getIntent();
        team1                   = getParameters.getStringExtra("Team1");
        team2                   = getParameters.getStringExtra("Team2");
        venue                   = getParameters.getStringExtra("Venue");
        leagueCupName           = getParameters.getStringExtra("LeagueCupName");
        refereeName             = getParameters.getStringExtra("RefereeName");
        int minutesPerHalf      = getParameters.getIntExtra("MinutesPerHalf", 45);
        type                    = getParameters.getStringExtra("Type");

        //Set half time and full time value.
        if (minutesPerHalf < 10) { //Half time check.
            halfTime = "0" + Integer.toString(minutesPerHalf) + ":00";
            if (minutesPerHalf * 2 < 10) { //Full time check.
                fullTime = "0" + Integer.toString(minutesPerHalf * 2) + ":00";
            } else {
                fullTime = Integer.toString(minutesPerHalf * 2) + ":00";
            }

        } else {
            halfTime = Integer.toString(minutesPerHalf) + ":00";
            fullTime = Integer.toString(minutesPerHalf * 2) + ":00";
        }

        // Score/Team name elements.
        TextView team1NameTV    = (TextView) this.findViewById(R.id.team1NameTVFM);
        team1ScoreTV            = (TextView) this.findViewById(R.id.team1GoalTVFM);
        TextView team2NameTV    = (TextView) this.findViewById(R.id.team2NameTVFM);
        team2ScoreTV            = (TextView) this.findViewById(R.id.team2GoalTVFM);
        team1NameTV.setText(team1);
        team2NameTV.setText(team2);
        team1ScoreTV.setText(Integer.toString(team1OverallScore));
        team2ScoreTV.setText(Integer.toString(team2OverallScore));

        //Database adapter setup.
        matchDbAdapter  = new MatchDbAdapter(this);
        factsDbAdapter  = new FactsDbAdapter(this);
        teamDbAdapter   = new TeamDbAdapter(this);

        //Get players from database for autocomplete input.
        factsDbAdapter.open();
        ArrayList<String> plyrs = factsDbAdapter.getColumnValues(FactsDbAdapter.KEY_PLAYER1, false);
        players                 = plyrs.toArray(new String[plyrs.size()]);
        factsDbAdapter.close();

        // Dialog setup for when match event(one player) is called.
        onePlayerEventDG = new Dialog(FootballMatchActivity.this);
        onePlayerEventDG.setContentView(R.layout.one_player_event_dialog);
        onePlayerEventDG.setTitle(getString(R.string.eventDetails));
        onePlayerEventDG.setCancelable(true);
        this.onePlayerEventDialogSetup();

        // Dialog setup for when match event(two player) is called.
        twoPlayerEventDG = new Dialog(FootballMatchActivity.this);
        twoPlayerEventDG.setContentView(R.layout.two_player_event_dialog);
        twoPlayerEventDG.setTitle(getString(R.string.eventDetails));
        twoPlayerEventDG.setCancelable(true);
        this.twoPlayerEventDialogSetup();

        matchEvents = new ArrayList<>();

        // Timer elements
        startBn     = (BootstrapButton) this.findViewById(R.id.startButtonFM);
        matchChron  = (Chronometer) findViewById(R.id.chronometerFM);
        stopBn      = (BootstrapButton) this.findViewById(R.id.stopButtonFM);
        stopBn.setEnabled(false);
        this.startButton();
        this.stopButton();
        this.onChronTick();

        // Match events elements.
        Spinner matchEventSpinner           = (Spinner) findViewById(R.id.matchEventSpinnerFM);
        ArrayAdapter<CharSequence> adapter  = ArrayAdapter.createFromResource(this, R.array.footballMatchEvent_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchEventSpinner.setAdapter(adapter);
        matchEventSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener());

        matchInfoTV     = (TextView) this.findViewById(R.id.matchInfoTVFM);
        undoEventBn     = (BootstrapButton) this.findViewById(R.id.undoEventButtonFM);
        finishMatchBn   = (BootstrapButton) this.findViewById(R.id.finishMatchFM);
        this.undoEventButton();
        this.finishMatchButton();

    }

    /**
     * Called when activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        onePlayerEventDG.dismiss();
        twoPlayerEventDG.dismiss();
    }

    /**
     * Called when activity is in the background.
     */
    @Override
    public void onPause() {
        super.onPause();
        currTimeMilli = System.currentTimeMillis();
        onePlayerEventDG.dismiss();
        twoPlayerEventDG.dismiss();
    }

    /**
     * Called when activity is called back into foreground.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!isPaused) timeElapsed += System.currentTimeMillis() - currTimeMilli;

    }

    /**
     * Called when activity is hidden from screen.
     */
    @Override
    public void onStop() {
        super.onStop();
        currTimeMilli = System.currentTimeMillis();
    }

    /**
     * Called when activity is called back into foreground.
     */
    @Override
    public void onRestart() {
        super.onRestart();
        if (!isPaused) timeElapsed += System.currentTimeMillis() - currTimeMilli;
    }

    /**
     * The set up of one player event dialog.
     */
    private void onePlayerEventDialogSetup() {

        team1EventRBN = (RadioButton) onePlayerEventDG.findViewById(R.id.team1SelEvent);
        team2EventRBN = (RadioButton) onePlayerEventDG.findViewById(R.id.team2SelEvent);
        team1EventRBN.setText(team1);
        team2EventRBN.setText(team2);
        team1EventRBN.setChecked(true);

        // Team1 text view and input set up.
        playerAdapter               = new ArrayAdapter<>(this, R.layout.list_team, players);
        playerOneNameEventAutoTV    = (AutoCompleteTextView) onePlayerEventDG.findViewById(R.id.player1EventNameAUTOTV);
        playerOneNameEventAutoTV.setAdapter(playerAdapter);

        // Submit Match Event button.
        BootstrapButton createMatchEventBN = (BootstrapButton) onePlayerEventDG.findViewById(R.id.createMatchEventBN);
        createMatchEventBN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get team selected from radio buttons.
                if (team1EventRBN.isChecked()) eventTeamSel = team1EventRBN.getText().toString();
                else if (team2EventRBN.isChecked())
                    eventTeamSel = team2EventRBN.getText().toString();

                // Set player event happened to.
                eventPlayer1Name = playerOneNameEventAutoTV.getText().toString().trim();

                // Create match event and store it in list of match events.
                MatchEvent matchEvent = new MatchEvent(eventTime, eventType, eventPlayer1Name, eventTeamSel);

                //ifgGoal event update GUI to display new score.
                if (getString(R.string.event_goal).equals(eventType)) updateMatchScore(eventTeamSel);

                // Add match event to list of match events so far.
                matchEvents.add(matchEvent);
                matchInfo += matchEvent.toString();
                matchInfoTV.setText(matchInfo);
                playerOneNameEventAutoTV.setText("");
                onePlayerEventDG.hide();
            }
        });

        // Hide Match Event button.
        BootstrapButton hideMatchEventBN = (BootstrapButton) onePlayerEventDG.findViewById(R.id.hideMatchEventBN);
        hideMatchEventBN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerOneNameEventAutoTV.setText("");
                onePlayerEventDG.hide();
            }
        });
    }

    /**
     * The set up of two player event dialog.
     */
    private void twoPlayerEventDialogSetup() {

        team1EventTwoRBN = (RadioButton) twoPlayerEventDG.findViewById(R.id.team1SelEventTwo);
        team2EventTwoRBN = (RadioButton) twoPlayerEventDG.findViewById(R.id.team2SelEventTwo);
        team1EventTwoRBN.setText(team1);
        team2EventTwoRBN.setText(team2);
        team1EventTwoRBN.setChecked(true);

        //Player one.
        playerAdapter = new ArrayAdapter<>(this, R.layout.list_team, players);
        playerOneNameEventTwoAutoTV = (AutoCompleteTextView) twoPlayerEventDG.findViewById(R.id.player1EventTwoNameAUTOTV);
        playerOneNameEventTwoAutoTV.setAdapter(playerAdapter);

        //Player two.
        playerTwoNameEventTwoAutoTV = (AutoCompleteTextView) twoPlayerEventDG.findViewById(R.id.player2EventTwoNameAUTOTV);
        playerTwoNameEventTwoAutoTV.setAdapter(playerAdapter);

        // Submit Match Event button.
        BootstrapButton createMatchEventTwoBN = (BootstrapButton) twoPlayerEventDG.findViewById(R.id.createMatchEventTwoBN);
        createMatchEventTwoBN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get team selected from radio buttons.
                if (team1EventTwoRBN.isChecked())
                    eventTeamSel = team1EventTwoRBN.getText().toString();
                else if (team2EventTwoRBN.isChecked())
                    eventTeamSel = team2EventTwoRBN.getText().toString();

                // Set player1 and player 2 players involved in  event.
                eventPlayer1Name = playerOneNameEventTwoAutoTV.getText().toString().trim();
                eventPlayer2Name = playerTwoNameEventTwoAutoTV.getText().toString().trim();

                // Create match event and store it in list of match events.
                MatchEvent matchEvent = new MatchEvent(eventTime, eventType, eventPlayer1Name, eventPlayer2Name, eventTeamSel);

                //if Goal event update GUI to display new score.
                if (getString(R.string.event_goal).equals(eventType)) updateMatchScore(eventTeamSel);

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
        BootstrapButton hideMatchEventTwoBN = (BootstrapButton) twoPlayerEventDG.findViewById(R.id.hideMatchEventTwoBN);
        hideMatchEventTwoBN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerOneNameEventTwoAutoTV.setText("");
                playerTwoNameEventTwoAutoTV.setText("");
                twoPlayerEventDG.hide();
            }
        });
    }

    //Method to rebuild match event string after an event is undone.
    private void reBuildMatchEventsString() {
        StringBuilder matchInfo = new StringBuilder();

        for (MatchEvent mEvent : matchEvents) {
            matchInfo.append( mEvent.toString());
        }

        matchInfoTV.setText(matchInfo);
    }

    /**
     * Start button listener method.
     */
    private void startButton() {
        startBn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
    }

    /**
     * Stop button listener method.
     */
    private void stopButton() {
        stopBn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });
    }

    /**
     * Undo event button listener method.
     */
    private void undoEventButton() {
        undoEventBn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check to see if there is an event to move.
                if (matchEvents.size() >= 1) {
                    //Remove last element. I.E last event.
                    MatchEvent aMatchEvent = matchEvents.remove(matchEvents.size() - 1);

                    //Check if the event is a goal so we can then undo the score.
                    if (getString(R.string.event_goal).equals(aMatchEvent.getType())) {
                        undoMatchScore(aMatchEvent.getTeam());
                    }

                    //Rebuild Match event string with removed element.
                    reBuildMatchEventsString();
                }
            }
        });
    }

    /**
     * Finish Match button listener method.
     */
    private void finishMatchButton() {
        finishMatchBn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int matchid;
                MatchEvent event;

                //Add match to match table in database.
                matchDbAdapter.open();
                matchDbAdapter.addMatch(SPORT, team1, team2, venue, type, leagueCupName, refereeName);
                matchid = matchDbAdapter.getLastMatchID();
                matchDbAdapter.close();

                //Add match facts to Facts table in database.
                factsDbAdapter.open();
                for (int i = 0; i < matchEvents.size(); i++) {
                    event = matchEvents.get(i);
                    factsDbAdapter.addMatchFact(matchid, event.getType(), event.getPlayer1(), event.getPlayer2(), event.getTeam(), event.getTime());
                }

                factsDbAdapter.close();

                //Add teams to Team table in database if they don't exist else update them.
                teamDbAdapter.open();
                String team1Status;
                String team2Status;
                if (team1OverallScore > team2OverallScore) {//Team 1 won match.
                    team1Status = WIN;
                    team2Status = LOSS;
                } else if (team1OverallScore == team2OverallScore) {//Draw
                    team1Status = DRAW;
                    team2Status = DRAW;
                } else {//Team 1 lost the game.
                    team1Status = LOSS;
                    team2Status = WIN;
                }
                addOrUpdateTeam(teamDbAdapter, team1, team1Status);
                addOrUpdateTeam(teamDbAdapter, team2, team2Status);
                teamDbAdapter.close();

                //Finished saving match so bring user back to home screen.
                Toast.makeText(v.getContext(), getString(R.string.matchSaved), Toast.LENGTH_LONG).show();
                Intent i = new Intent(FootballMatchActivity.this, MainMenuActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }


    /**
     * This method adds or updates a team in the Team table of the database.
     *
     * @param teamAdapter team adapter
     * @param team team
     * @param status if team won, lost or drew.
     */
    @SuppressWarnings("static-access")
    private void addOrUpdateTeam(TeamDbAdapter teamAdapter, String team, String status) {
        String teamExists               = teamAdapter.getColumnValueForTeamStr(teamAdapter.KEY_NAME, team);
        String sportExistsForGivenTeam  = teamAdapter.getColumnValueForTeamStr(teamAdapter.KEY_SPORT, team);

        //Team exists so update. We check team name and sport as we could have teams in
        //different sports with the same name.
        if (team.equals(teamExists) && SPORT.equals(sportExistsForGivenTeam)) {

            switch (status) {
                case WIN: {//Team won the match.
                    int totalWins   = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_WINS, team);
                    int totalPoints = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_TOTALPOINTS, team);
                    teamAdapter.updateSingleColumn(teamExists, teamAdapter.KEY_WINS, totalWins + 1);
                    teamAdapter.updateSingleColumn(teamExists, teamAdapter.KEY_TOTALPOINTS, totalPoints + 3);
                    break;
                }
                case DRAW: {//Draw
                    int totalDraws  = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_DRAWS, team);
                    int totalPoints = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_TOTALPOINTS, team);
                    teamAdapter.updateSingleColumn(team, teamAdapter.KEY_DRAWS, totalDraws + 1);
                    teamAdapter.updateSingleColumn(team, teamAdapter.KEY_TOTALPOINTS, totalPoints + 1);
                    break;
                }
                default: //Team lost the game.
                    int totalLosses = teamAdapter.getColumnValueForTeamInt(teamAdapter.KEY_LOSSES, team);
                    teamAdapter.updateSingleColumn(team, teamAdapter.KEY_LOSSES, totalLosses + 1);
                    break;
            }

        } else { //Team doesn't exist so add team.

            switch (status) {
                case WIN:
                    teamAdapter.addTeam(team, SPORT, 1, 0, 0, 3);
                    break;
                case DRAW:
                    teamAdapter.addTeam(team, SPORT, 0, 0, 1, 1);
                    break;
                default: //Team lost the game.
                    teamAdapter.addTeam(team, SPORT, 0, 1, 0, 0);
                    break;
            }
        }
    }

    /**
     * If goal is called it updates the score of the match.
     *
     * @param aTeam team
     */
    private void updateMatchScore(String aTeam) {
        if (team1.equals(aTeam)) {
            team1OverallScore++;
            team1ScoreTV.setText(Integer.toString(team1OverallScore));
        } else if (team2.equals(aTeam)) {
            team2OverallScore++;
            team2ScoreTV.setText(Integer.toString(team2OverallScore));
        }
    }

    /**
     * If undo button is called and type == goal then undo the score for the team.
     *
     * @param aTeam team
     */
    private void undoMatchScore(String aTeam) {
        if (team1.equals(aTeam)) {
            team1OverallScore--;
            team1ScoreTV.setText(Integer.toString(team1OverallScore));
        } else if (team2.equals(aTeam)) {
            team2OverallScore--;
            team2ScoreTV.setText(Integer.toString(team2OverallScore));
        }
    }

    /**
     * Start the timer of the chronometer.
     */
    private void startTimer() {
        startBn.setEnabled(false);
        stopBn.setEnabled(true);
        isPaused = false;
        if (isPausedFirstRun) { //Timer being resumed, just start timer again.
            matchChron.start();
        } else { //First time timer is started.
            matchChron.setBase(SystemClock.elapsedRealtime());
            matchChron.start();
        }
    }

    /**
     * Stop the timer of the chronometer.
     */
    private void stopTimer() {
        startBn.setEnabled(true);
        stopBn.setEnabled(false);
        isPaused = true;
        matchChron.stop();
        isPausedFirstRun = true;
    }

    /**
     * Method to play an alarm sound to signal half time or full time.
     */
    private void playAlarm() {
        MediaPlayer mp = MediaPlayer.create(FootballMatchActivity.this, R.raw.buzzer);
        mp.start();
        mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
    }

    /**
     * Deals with chronometer tick listener.
     */
    private void onChronTick() {
        matchChron.setOnChronometerTickListener(new OnChronometerTickListener() {

            public void onChronometerTick(Chronometer arg0) {

                long minutes;
                long seconds;
                String minutesStr;
                String secondsStr;
                if (isPausedFirstRun) { //If timer has being resumed from a pause state.

                    //Calculate minutes and seconds.
                    minutes = ((timeElapsed - matchChron.getBase()) / 1000) / 60;
                    seconds = ((timeElapsed - matchChron.getBase()) / 1000) % 60;
                    minutesStr = Long.toString(minutes);
                    secondsStr = Long.toString(seconds);

                    if (minutes < 10) minutesStr = "0" + minutesStr;
                    if (seconds < 10) secondsStr = "0" + secondsStr;

                    timeElapsed = timeElapsed + 1000;
                    currTime = minutesStr + ":" + secondsStr;
                    arg0.setText(currTime);

                } else { //If timer hasn't being paused yet.

                    //Calculate minutes and seconds.
                    minutes = ((SystemClock.elapsedRealtime() - matchChron.getBase()) / 1000) / 60;
                    seconds = ((SystemClock.elapsedRealtime() - matchChron.getBase()) / 1000) % 60;
                    minutesStr = Long.toString(minutes);
                    secondsStr = Long.toString(seconds);

                    if (minutes < 10) minutesStr = "0" + minutesStr;
                    if (seconds < 10) secondsStr = "0" + secondsStr;

                    timeElapsed = SystemClock.elapsedRealtime();
                    currTime = minutesStr + ":" + secondsStr;
                    arg0.setText(currTime);
                }

                //Half time.
                if (currTime.equals(halfTime) && firstHalf) {
                    firstHalf = false;
                    stopTimer();
                    playAlarm();
                }

                //Full time.
                if (currTime.equals(fullTime) && !firstHalf) {
                    stopTimer();
                    playAlarm();
                }

            }

        });
    }

    //Actionbar onClick Listener.
    private class ActionBarClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.home_button:
                    i = new Intent(FootballMatchActivity.this, MainMenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }

            if (i != null) {
                startActivity(i);
            }
        }
    }

    //Spinner itemSelected listener.
    private class SpinnerItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            //Match event selection.
            eventTime = currTime;

            if(pos != 0) {
                eventType = parent.getItemAtPosition(pos).toString();
                if (getString(R.string.event_substitution).equals(eventType)) {
                    twoPlayerEventDG.show();
                } else {
                    onePlayerEventDG.show();
                }
            }

            //Set selection to none.
            parent.setSelection(0);

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }
}
