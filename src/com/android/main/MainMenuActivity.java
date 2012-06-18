package com.android.main;

import com.android.record.FootballPreMatchInfoActivity;
import com.android.record.GaelicPreMatchInfoActivity;
import com.android.record.HurlingPreMatchInfoActivity;
import com.android.record.RugbyPreMatchInfoActivity;
import com.android.send.SyncService;
import com.android.statistics.StatisticsMenuActivity;
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
 * Main menu Activity.
 * 
 * @author Jonathan
 * 
 */
public class MainMenuActivity extends Activity
{

	/**
	 * Checks whether user is logged in or not.
	 */
	private boolean loggedIn = false;
	
	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionBarOperations;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		// Main menu click listener.
		MainMenuClickListener dBClickListener = new MainMenuClickListener();
		findViewById(R.id.home_button).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_football_button).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_gaelic_button).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_hurling_button).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_rugby_button).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_statistics_button).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_exit_button).setOnClickListener(dBClickListener);
		findViewById(R.id.login_button).setOnClickListener(dBClickListener);
		findViewById(R.id.sync_button).setOnClickListener(dBClickListener);
		
		//Handle how the actionbar buttons are displayed.
		actionBarOperations = new Actionbar(getApplicationContext());
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());

	}

	/** Called when activity is called back into foreground. */
	@Override
	public void onResume()
	{
		super.onResume();

		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
	}

	/** Called when activity is called back into foreground. */
	@Override
	public void onRestart()
	{
		super.onRestart();
		actionBarOperations.loginButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());
		actionBarOperations.syncButtonDisplay(this.getApplicationContext(), this.getWindow().getDecorView());

	}

	// Main Menu onClick Listener.
	private class MainMenuClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent i = null;
			switch (v.getId())
			{
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
				finish(); // Closes main menu activity.
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
					i = new Intent(MainMenuActivity.this, LoginActivity.class);
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
					Intent syncIntent = new Intent(MainMenuActivity.this, SyncService.class);
					startService(syncIntent);
				}
				break;
			default:
				break;
			}
			if (i != null)
				startActivity(i);
		}
	}
}