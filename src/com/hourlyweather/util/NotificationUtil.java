package com.hourlyweather.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.hourlyweather.R;

/**
 * Utilities for notifying the user of various events
 * @author dhgonsalves
 *
 */
public class NotificationUtil {
    /**
     * display message to user about changing their location settings and start
     * the location security intent
     */
    public static void popErrorDialog(final Context context, String title,
	    String message, final Intent actionIntent) {
	Builder alertBuilder = createErrorDialog(context, title, message);
	alertBuilder.setPositiveButton("ok",
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			// start the intent
			context.startActivity(actionIntent);
		    }
		});
	alertBuilder.create().show();
    }

    /**
     * display message to user about changing their location settings and start
     * the location security intent
     */
    public static void popErrorDialog(final Context context, String title,
	    String message, final DialogInterface.OnClickListener onClick) {
	Builder alertBuilder = createErrorDialog(context, title, message);
	alertBuilder.setPositiveButton("ok", onClick);
	alertBuilder.create().show();
    }

    /**
     * display message to user about their current network connectivity or settings
     */
    public static void popNetworkErrorDialog(final Context context) {
	String title = "Network Issue";
	String message ="Please check your network settings/connectivity";
	Intent locationSettingsIntent = new Intent(
		android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
	popErrorDialog(context, title, message,
		locationSettingsIntent);
    }
    
    /**
     * display message to user about changing their location settings and start
     * the location security intent
     */
    public static void popLocationSettingsDialog(Context context) {
	String title = "Location Settings";
	String message = "Your location settings need to be enabled to pull your forecast.";
	Intent locationSettingsIntent = new Intent(
		android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	popErrorDialog(context, title, message,
		locationSettingsIntent);
    }
    
    public static void popLocationNetworkDisabledDialog(Context context) {
	String title = "Location Settings";
	String message = "location resolution works better if you enable \"use wireless networks\".\nThis friendly reminder wont be displayed again.";
	Intent locationSettingsIntent = new Intent(
		android.provider.Settings.ACTION_SECURITY_SETTINGS);
	popErrorDialog(context, title, message,
		locationSettingsIntent);
    }


    private static Builder createErrorDialog(Context context, String title,
	    String message) {
	// create a dialog builder with the passed in params
	Builder alertBuilder = new AlertDialog.Builder(context);
	alertBuilder.setMessage(message);
	alertBuilder.setCancelable(false);
	alertBuilder.setTitle(title);
	alertBuilder.setIcon(R.drawable.icon);
	return alertBuilder;
    }
}
