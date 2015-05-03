package com.jonathanbloodmatchtracker.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jonathanbloodmatchtracker.record.FootballPreMatchInfoActivity;
import com.jonathanbloodmatchtracker.record.GaelicPreMatchInfoActivity;
import com.jonathanbloodmatchtracker.record.HurlingPreMatchInfoActivity;
import com.jonathanbloodmatchtracker.record.RugbyPreMatchInfoActivity;
import com.jonathanbloodmatchtracker.statistics.StatisticsMenuActivity;

/**
 * Main menu Activity.
 *
 * @author Jonathan
 */
public class MainMenuActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        //Set Dashboard title
        TextView dashboardTitle = (TextView) findViewById(R.id.dashboard_title);
        dashboardTitle.setText(getString(R.string.title_home));

        //Main menu click listener.
        MainMenuClickListener dBClickListener = new MainMenuClickListener();
        findViewById(R.id.home_button).setOnClickListener(dBClickListener);
        findViewById(R.id.dashboard_football_button).setOnClickListener(dBClickListener);
        findViewById(R.id.dashboard_gaelic_button).setOnClickListener(dBClickListener);
        findViewById(R.id.dashboard_hurling_button).setOnClickListener(dBClickListener);
        findViewById(R.id.dashboard_rugby_button).setOnClickListener(dBClickListener);
        findViewById(R.id.dashboard_statistics_button).setOnClickListener(dBClickListener);
        findViewById(R.id.dashboard_exit_button).setOnClickListener(dBClickListener);

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


    //Main Menu onClick Listener.
    private class MainMenuClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.home_button:
                    i = new Intent(MainMenuActivity.this, MainMenuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case R.id.dashboard_football_button:
                    i = new Intent(MainMenuActivity.this, FootballPreMatchInfoActivity.class);
                    break;
                case R.id.dashboard_gaelic_button:
                    i = new Intent(MainMenuActivity.this, GaelicPreMatchInfoActivity.class);
                    break;
                case R.id.dashboard_hurling_button:
                    i = new Intent(MainMenuActivity.this, HurlingPreMatchInfoActivity.class);
                    break;
                case R.id.dashboard_rugby_button:
                    i = new Intent(MainMenuActivity.this, RugbyPreMatchInfoActivity.class);
                    break;
                case R.id.dashboard_statistics_button:
                    i = new Intent(MainMenuActivity.this, StatisticsMenuActivity.class);
                    break;
                case R.id.dashboard_exit_button:
                    finish(); //Closes main menu activity.
                    break;
                default:
                    break;
            }
            if (i != null) startActivity(i);
        }
    }
}