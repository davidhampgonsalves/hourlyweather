package com.hourlyweather.appwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.hourlyweather.ForecastCacher;
import com.hourlyweather.ForecastFetcherInt;
import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.location.LocationUtil;
import com.hourlyweather.util.NetworkUtil;
import com.hourlyweather.util.NotificationUtil;

/**
 * Configuration task for the hourly weather widget that handles the initial
 * forecast poll and setup
 * 
 * @author dhgonsalves
 * 
 */
public class HourlyWidgetConfigure extends Activity implements
	ForecastFetcherInt {

    private Integer appWidgetId;

    @Override
    protected void onCreate(Bundle icicle) {
	super.onCreate(icicle);
	setResult(RESULT_CANCELED);

	appWidgetId = null;
	Intent intent = getIntent();
	Bundle extras = intent.getExtras();
	if (extras != null)
	    appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
		    AppWidgetManager.INVALID_APPWIDGET_ID);

	if (appWidgetId != null) {
	    HourlyForecast cachedForecast = ForecastCacher
		    .getCachedForecast(this);

	    // use the cached forecast if its still current
	    if (cachedForecast != null)
		forecastLoaded(cachedForecast);
	    else {

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// check location settings
		if (!LocationUtil.isDeviceLocationAware(this, locationManager))
		    NotificationUtil.popLocationSettingsDialog(this);
		// check the network settings
		else if (!NetworkUtil.isNetworkAvailable(this))
		    NotificationUtil.popNetworkErrorDialog(this);
		else
		    // start the update task which will show the loading
		    // indicator to the user while loading the forecast
		    new WidgetForecastFetcherTask(this).execute();
	    }
	}
    }

    /**
     * fetch the forecast for the passed in location
     * 
     * @param location
     */
    public void fetchForecast(Location location) {
	if (location != null)
	    new WidgetForecastFetcherTask(this, location).execute();
    }

    /**
     * Completes the configuration activity and schedules the forecast updates
     * 
     * @param forecast
     */
    public void forecastLoaded(HourlyForecast forecast) {
	Intent resultValue = new Intent();
	resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	
	setResult(RESULT_OK, resultValue);
	finish();	

	// populate the widget with the new forecast
	WidgetScrollList.populateList(this, forecast);
    }
}
