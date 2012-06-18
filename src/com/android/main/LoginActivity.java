package com.android.main;

import com.android.management.Management;
import com.android.send.SyncService;
import com.jonathanblood.main.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	
	/**
	 * Whether or not the user is logged in or not.
	 */
	private boolean loggedIn = false;

	/**
	 * Button that allows the user to login.
	 */
	private Button logIn;
	
	/**
	 * EditText which allows the user to enter in a username.
	 */
	private EditText usernameET;
	
	/**
	 * EditText which allows the user to enter in a password.
	 */
	private EditText passwordET;
	
	/**
	 * String containing the username.
	 */
	private String username;
	
	/**
	 * String containing the password.
	 */
	private String password;
	
	/**
	 * Management object which allows users to login.
	 */
	private Management management = new Management();
	
	/**
	 * Used to handle actionbar buttons.
	 */
	private Actionbar actionbar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Actionbar Listener.
		ActionBarClickListener actionBClickListener = new ActionBarClickListener();
		findViewById(R.id.home_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.login_button).setOnClickListener(actionBClickListener);
		findViewById(R.id.sync_button).setOnClickListener(actionBClickListener);
				
		//Handle how the actionbar buttons are displayed.
		actionbar = new Actionbar(this);
		actionbar.loginButtonDisplay(this, this.getWindow().getDecorView());
		actionbar.syncButtonDisplay(this, this.getWindow().getDecorView());

		usernameET = (EditText) this.findViewById(R.id.usernameEditT);
		passwordET = (EditText) this.findViewById(R.id.passwordEditT);
		passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());

		this.logInButton();
	}

	/**
	 * Application clean up on close.
	 */
	protected void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * Login button creation.
	 */
	private void logInButton()
	{
		logIn = (Button) this.findViewById(R.id.loginButton);
		logIn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				loginCheck();
			}
		});
	}

	/**
	 * Login to user.
	 */
	private void loginCheck()
	{
		username = this.getEditTextValue(usernameET);
		password = this.getEditTextValue(passwordET);

		// Only check login if we have access to the internet.
		if (management.isConnectedToInternet(getApplicationContext()))
		{

			int userIDRec = management.login(username, password);
			if (userIDRec != 0) // login is valid.
			{ 

				// Save preference state so we can load these preferences
				// when activity is called again.
				SharedPreferences settings = getSharedPreferences("LOGIN_DETAILS", 0);
				SharedPreferences.Editor editor = settings.edit();

				// Add login data we would like to save.
				editor.putBoolean("loggedIn", true);
				editor.putInt("userID", userIDRec);
				editor.commit();

				// End the login Activity as it's no longer needed since we are logged in now.
				Toast.makeText(this, "Log in successful.", Toast.LENGTH_LONG).show();
				LoginActivity.this.finish();

			}
			else
			{
				Toast.makeText(this, "Log in failed.", Toast.LENGTH_LONG).show();
			}

		}

	}

	/**
	 * Gets the value of editText and validates EditText to see if they are empty.
	 * 
	 * @param editText
	 * @return String containing value of editText.
	 */
	private String getEditTextValue(EditText editText)
	{
		String value = editText.getText().toString();
		if (value.length() == 0)
		{
			editText.setError("Please enter a value");
		}
		return value;
	}
	
	// Actionbar onClick Listener.
		private class ActionBarClickListener implements OnClickListener
		{
			@Override
			public void onClick(View v)
			{
				Intent i = null;
				switch (v.getId())
				{
				case R.id.home_button:
					i = new Intent(LoginActivity.this, MainMenuActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
						i = new Intent(LoginActivity.this, LoginActivity.class);
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
						Intent syncIntent = new Intent(LoginActivity.this, SyncService.class);
						startService(syncIntent);
					}
					break;
				}
				if (i != null)
				{
					startActivity(i);
				}
			}
		}

}
