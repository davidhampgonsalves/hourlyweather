package com.hourlyweather;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.location.LocationUtil;

/**
 * task that handles asynchronous pulling of a forecast as well as notifying
 * user of progress
 * 
 * @author dhgonsalves
 * 
 */
public abstract class ForecastFetcherTask extends
	AsyncTask<Void, String, HourlyForecast> {

    protected Context context;
    protected ProgressDialog loadingDialog;
    protected Location location;

    public ForecastFetcherTask(Context context) {
	this.context = context;
	loadingDialog = new ProgressDialog(context);
	loadingDialog.setCancelable(false);

	location = null;
    }

    public ForecastFetcherTask(Context context, Location location) {
	this(context);
	this.location = location;
    }

    @Override
    protected void onPreExecute() {
	if (location == null)
	    loadingDialog.setMessage("Determining Location");
	else
	    loadingDialog.setMessage("Loading Forecast");
	loadingDialog.show();
    }

    @Override
    protected HourlyForecast doInBackground(Void... v) {
	
	System.out.println("before location: " + location);
	if (location == null) {
	    // get the last know location
	    location = LocationUtil.getBestLastKnownLocation(context);
	    if (location == null)
		return null;
	}
	System.out.println("after location: " + location);
	publishProgress("Loading Forecast");
	HourlyForecast forecast = ForecastCacher.getForecast(context, location);

	if (forecast != null)
	    publishProgress("Prepairing Forecast");

	// if there were a network related error forecast will be null
	return forecast;
    }

}
