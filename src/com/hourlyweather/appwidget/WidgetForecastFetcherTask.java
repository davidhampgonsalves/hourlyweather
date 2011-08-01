package com.hourlyweather.appwidget;

import android.content.DialogInterface;
import android.location.Location;

import com.hourlyweather.ForecastFetcherTask;
import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.location.LocationResolver;
import com.hourlyweather.util.NotificationUtil;

/**
 * Pull an hourly forecast for a given location and populates the passed in
 * Alerts the widgets configuration activity when completed
 * 
 * @author dhgonsalves
 */
public class WidgetForecastFetcherTask extends
	ForecastFetcherTask {
    private HourlyWidgetConfigure configActivity;

    public WidgetForecastFetcherTask(HourlyWidgetConfigure configActivity) {
	super(configActivity);
	this.configActivity = configActivity;
    }

    public WidgetForecastFetcherTask(HourlyWidgetConfigure configActivity,
	    Location location) {
	super(configActivity, location);
	this.configActivity = configActivity;
    }

    @Override
    protected void onPostExecute(HourlyForecast forecast) {
	loadingDialog.dismiss();
	if (location == null) {
	    NotificationUtil.popErrorDialog(configActivity,
		    "Location not avaliable",
		    "Press ok to try and refresh your current position",
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			    new LocationResolver(configActivity).execute();
			}
		    });
	} else if (forecast == null) {
	    // error has occured, alert the user
	    NotificationUtil.popNetworkErrorDialog(configActivity);
	} else {
	    configActivity.forecastLoaded(forecast);
	}
    }

    @Override
    protected void onProgressUpdate(String... msgs) {
	loadingDialog.setMessage(msgs[0]);
    }
}
