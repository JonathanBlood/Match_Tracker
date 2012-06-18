package com.android.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;

import com.jonathanblood.main.R;

/**
 * Class to handle actionbar operations.
 * 
 * @author Jonathan
 * 
 */
public class Actionbar 
{

	/**
	 * Constructor: that takes in just the context.
	 * 
	 * @param context
	 */
	public Actionbar(Context context){}

	/**
	 * Determines whether the login button should be displayed as logged in or not logged out.
	 * 
	 * @param context
	 * @param view
	 * @return
	 */
	public boolean loginButtonDisplay(Context aContext, View view)
	{
		boolean loggedIn;

		SharedPreferences settings = aContext.getSharedPreferences("LOGIN_DETAILS", Context.MODE_WORLD_WRITEABLE);
		loggedIn = settings.getBoolean("loggedIn", false);
		settings.getInt("userID", 0);
		ImageButton loginButton = (ImageButton) view.findViewById(R.id.login_button);

		if (loggedIn)
			loginButton.setImageResource(R.drawable.login_logged_in);
		else
			loginButton.setImageResource(R.drawable.login);

		return loggedIn;

	}

	/**
	 * Determines whether the sync button should be shown as syncing or not.
	 * 
	 * @param context
	 * @param view
	 */
	public void syncButtonDisplay(Context aContext, View view)
	{
		SharedPreferences serviceSetting = aContext.getSharedPreferences("SERVICE_DETAILS",
				Context.MODE_WORLD_WRITEABLE);
		ImageButton syncButton = (ImageButton) view.findViewById(R.id.sync_button);

		if (serviceSetting.getBoolean("serviceStarted", false))
			syncButton.setImageResource(R.drawable.sync_selected);
		else
			syncButton.setImageResource(R.drawable.sync);
	}
}
