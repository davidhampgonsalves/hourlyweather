package com.hourlyweather.appwidget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.hourlyweather.ForecastCacher;
import com.hourlyweather.R;
import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.location.LocationUtil;

/**
 * Updates the app widget on the hour to maintain a current weather forecast
 * @author dhgonsalves
 *
 */
public class ForecastUpdateService extends Service {

    private final int NOTIFICATION_LOCATION_ERR = 1;
    private final int NOTIFICATION_NETWORK_ERR = 2;

    @Override
    public void onStart(Intent intent, int startId) {
	Location location = LocationUtil
		.getBestLastKnownLocation(getApplicationContext());

	if (location == null) {
	    createLocationErrorNotification();
	    return;
	}

	// get the forecast
	HourlyForecast forecast = ForecastCacher.getForecast(
		this.getApplicationContext(), location);

	if (forecast == null) {
	    // if we havn't already tried reschedule this poll in 2 minutes
	    if (intent.getExtras() != null
		    && intent.getExtras().getBoolean(
			    HourlyWeatherWidget.RETRIED_POLL))
		HourlyWeatherWidget.retryForecastUpdates(this
			.getApplicationContext());
	    createNetworkErrorNotification();
	    return;
	}

	// populate the widget with the new forecast
	WidgetScrollList.populateList(this.getApplicationContext(), forecast);
    }

    /**
     * Creates a location error notification message to get the user to reviews
     * their location settings and alert them that there is an issue
     */
    public void createLocationErrorNotification() {

	String ticker = "Hourly Weather location issue";
	String title = "Your location couldn't be determined";
	String text = "Please check your location settings.";

	Intent locationSettingsIntent = new Intent(
		android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

	createNotification(NOTIFICATION_LOCATION_ERR, ticker, title, text,
		locationSettingsIntent);
    }

    /**
     * Creates a network error notification message to get the user to reviews
     * their network settings and alert them that there is an issue
     */
    public void createNetworkErrorNotification() {

	String ticker = "Hourly Weather network issue";
	String title = "Your forecast couldn't be pulled";
	String text = "Please check your network settings.";

	Intent networkSettingsIntent = new Intent(
		android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
	createNotification(NOTIFICATION_NETWORK_ERR, ticker, title, text,
		networkSettingsIntent);
    }

    /**
     * Creates a notification to be displayed now with the passed in text fields
     * and intent
     * 
     * @param notificationId
     * @param ticker
     * @param title
     * @param text
     * @param actionIntent
     */
    public void createNotification(int notificationId, String ticker,
	    String title, String text, Intent actionIntent) {

	String ns = Context.NOTIFICATION_SERVICE;
	NotificationManager notificationManager = (NotificationManager) getSystemService(ns);

	Notification notification = new Notification(R.drawable.icon, ticker,
		System.currentTimeMillis());

	PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0,
		actionIntent, 0);

	notification.setLatestEventInfo(getApplicationContext(), title, text,
		actionPendingIntent);
	notificationManager.notify(notificationId, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }
}
