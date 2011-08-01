package com.hourlyweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.hourlyweather.appwidget.WidgetScrollList;
import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.location.LocationResolver;
import com.hourlyweather.location.LocationUtil;
import com.hourlyweather.util.NetworkUtil;
import com.hourlyweather.util.NotificationUtil;

/**
 * Hourly Weather is an hourly, location aware and super accurate weather
 * forecastign app for Google Android.
 * 
 * @author dhgonsalves
 * 
 */
public class HourlyWeather extends Activity implements ForecastFetcherInt {

    public static final int FORECAST_HOUR_SPAN = 36;
    private ForecastListAdapter forecastAdapter;
    private LocationManager locationManager;
    private SettingsManager settingsManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// build the forecast list and the adapter
	forecastAdapter = new ForecastListAdapter(this);
	ListView forecastList = layoutForecastList();

	// get the starting position, this will be populated if the appwidget
	// launched the activity
	int startingPosition = getIntent().getIntExtra(
		WidgetScrollList.WIDGET_INDEX, 0);

	forecastList.setSelectionFromTop(startingPosition, 15);

	// build the settings manager
	settingsManager = new SettingsManager(forecastAdapter,
		getSharedPreferences("com.hourlyweather", Context.MODE_PRIVATE));
	// add the formatter to the forecast adapter
	forecastAdapter.setFormatter(new ForecastFormatter(settingsManager));

	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	fetchForecast();
    }

    public void fetchForecast(Location location) {
	if (location != null)
	    new AppForecastFetcherTask(this, location).execute();
    }

    private void fetchForecast() {
	// if the widget is enabled then it there will probably already be a
	// cached forecast for this hour
	HourlyForecast cachedForecast = ForecastCacher.getCachedForecast(this);

	// check to see if we need to poll for a new forecast or if the
	// cached on is current already
	if (cachedForecast != null)
	    forecastAdapter.setForecast(cachedForecast);
	else {
	    if (deviceIsReadyToPullForecast(this, locationManager))
		new AppForecastFetcherTask(this).execute();
	}
    }

    public static boolean deviceIsReadyToPullForecast(Context context,
	    LocationManager locationManager) {
	// check location settings
	if (!LocationUtil.isDeviceLocationAware(context, locationManager))
	    NotificationUtil.popLocationSettingsDialog(context);
	// check the network settings
	else if (!NetworkUtil.isNetworkAvailable(context))
	    NotificationUtil.popNetworkErrorDialog(context);
	// check if the location settings are optimal and also if we've already
	// bugged the user about them and if not then display the message
	else if (LocationUtil.areLocationSettingsOptimal(locationManager)
		&& !LocationUtil.wasUserToldAboutOptimalSettings(context)) {
	    NotificationUtil.popLocationNetworkDisabledDialog(context);
	    LocationUtil.userWasToldAboutOptimalSettings(context);
	} else
	    return true;

	return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
	super.onWindowFocusChanged(hasFocus);

	// if the window regained focus and the forecast list is still empty
	// then we still need to try to populate it
	if (hasFocus && forecastAdapter.isEmpty()) {
	    // resume the forecast fetching process
	    fetchForecast();
	}
    }

    /**
     * Override the orientation change to re-layout the forecast list but retain
     * the current forecast
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	layoutForecastList();
    }

    /**
     * Adds the layout and forecast list to the screen and returns the listview
     * used
     */
    public ListView layoutForecastList() {
	setContentView(R.layout.hourlyweather_layout);
	ListView forecastList = (ListView) findViewById(R.id.forecast_list);
	forecastList.setAdapter(forecastAdapter);
	return forecastList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.refresh_loc:
	    if (deviceIsReadyToPullForecast(this, locationManager))
		new LocationResolver(this).execute();
	    return true;
	case R.id.temp_conf:
	    // pop up temperature unit options and save the result
	    AlertDialog.Builder temperatureOptionsBuilder = new AlertDialog.Builder(
		    this);
	    temperatureOptionsBuilder.setTitle("Choose a temperature unit");
	    temperatureOptionsBuilder.setSingleChoiceItems(
		    settingsManager.getTemperatureUnitNames(),
		    settingsManager.getTemperatureUnit(),
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
			    settingsManager.setTemperatureUnit(item);
			    dialog.dismiss();
			}
		    });
	    temperatureOptionsBuilder.create().show();
	    return true;
	case R.id.measurement_system_conf:
	    // pop up temperature unit options and save the result
	    AlertDialog.Builder measurementSystemOptionsBuilder = new AlertDialog.Builder(
		    this);
	    measurementSystemOptionsBuilder
		    .setTitle("Choose a measurement system");
	    measurementSystemOptionsBuilder.setSingleChoiceItems(
		    settingsManager.getMeasurementSystemNames(),
		    settingsManager.getMeasurementSystem(),
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
			    settingsManager.setMeasurementSystem(item);
			    dialog.dismiss();
			}
		    });
	    measurementSystemOptionsBuilder.create().show();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    public void setForecast(HourlyForecast forecast) {
	forecastAdapter.setForecast(forecast);
    }
}
