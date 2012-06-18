package com.android.statistics;

import com.android.main.Actionbar;
import com.android.main.LoginActivity;
import com.android.main.MainMenuActivity;
import com.android.send.SyncService;
import com.jonathanblood.main.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Statistics menu Activity.
 * 
 * @author Jonathan
 */
public class StatisticsMenuActivity extends Activity
{

	// Login information
	private boolean loggedIn = false;
	
	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionBarOperations = new Actionbar(this);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics_menu);

		// Statistics menu click listener.
		StatisticsMenuClickListener dBClickListener = new StatisticsMenuClickListener();
		findViewById(R.id.home_button).setOnClickListener(dBClickListener);
		findViewById(R.id.login_button).setOnClickListener(dBClickListener);
		findViewById(R.id.leaderboard_button).setOnClickListener(dBClickListener);
		findViewById(R.id.player_statistics_button).setOnClickListener(dBClickListener);
		findViewById(R.id.previous_matches_button).setOnClickListener(dBClickListener);
		findViewById(R.id.sync_button).setOnClickListener(dBClickListener);

		//Handle how the actionbar buttons are displayed.
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());

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

	// Statistics Menu onClick Listener.
	private class StatisticsMenuClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent i = null;
			switch (v.getId())
			{
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
					i = new Intent(StatisticsMenuActivity.this, LoginActivity.class);
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
					Intent syncIntent = new Intent(StatisticsMenuActivity.this, SyncService.class);
					startService(syncIntent);
				}
				break;
			default:
				break;
			}
			if (i != null)
			{
				startActivity(i);
			}
		}
	}
}
