package com.jonathanbloodmatchtracker.statistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jonathanbloodmatchtracker.main.MainMenuActivity;
import com.jonathanbloodmatchtracker.main.R;

/**
 * Statistics menu Activity.
 *
 * @author Jonathan
 */
public class StatisticsMenuActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_menu);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.statistics_menu));

        // Statistics menu click listener.
        StatisticsMenuClickListener dBClickListener = new StatisticsMenuClickListener();
        findViewById(R.id.home_button).setOnClickListener(dBClickListener);
        findViewById(R.id.leaderboard_button).setOnClickListener(dBClickListener);
        findViewById(R.id.player_statistics_button).setOnClickListener(dBClickListener);
        findViewById(R.id.previous_matches_button).setOnClickListener(dBClickListener);

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

    // Statistics Menu onClick Listener.
    private class StatisticsMenuClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.home_button:
                    i = new Intent(StatisticsMenuActivity.this, MainMenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case R.id.leaderboard_button:
                    i = new Intent(StatisticsMenuActivity.this, LeaderboardActivity.class);
                    break;
                case R.id.player_statistics_button:
                    i = new Intent(StatisticsMenuActivity.this, PlayerStatsActivity.class);
                    break;
                case R.id.previous_matches_button:
                    i = new Intent(StatisticsMenuActivity.this, PreviousMatchesActivity.class);
                    break;
                default:
                    break;
            }
            if (i != null) {
                startActivity(i);
            }
        }
    }
}
