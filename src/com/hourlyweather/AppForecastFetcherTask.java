package com.hourlyweather;

import android.content.DialogInterface;
import android.location.Location;

import com.hourlyweather.appwidget.HourlyWeatherWidget;
import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.location.LocationResolver;
import com.hourlyweather.util.NotificationUtil;

/**
 * Pull an hourly forecast for a given location and populates the passed in
 * ForecastListAdaptor when completed
 * 
 * @author dhgonsalves
 */
public class AppForecastFetcherTask extends ForecastFetcherTask {
    private HourlyWeather hourlyWeather;

    public AppForecastFetcherTask(HourlyWeather hourlyWeather) {
	super(hourlyWeather);
	this.hourlyWeather = hourlyWeather;
    }

    public AppForecastFetcherTask(HourlyWeather hourlyWeather, Location location) {
	super(hourlyWeather, location);
	this.hourlyWeather = hourlyWeather;
    }

    /**
     * If a valid forecast has been pulled then it will be cached and the
     * ForecastAdapterList and the AppWidgets will be updated
     */
    @Override
    protected void onPostExecute(HourlyForecast forecast) {
	loadingDialog.dismiss();
	
	if (location == null) {
	    NotificationUtil.popErrorDialog(context,
		    "Location not avaliable",
		    "Press ok to try and refresh your current position",
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			    new LocationResolver(hourlyWeather).execute();
			}
		    });
	} else if (forecast == null) {
	    // error has occured, alert the user
	    NotificationUtil.popNetworkErrorDialog(context);
	} else {
	    // update the list with the new forecast
	    hourlyWeather.setForecast(forecast);
	    // update the app widgets
	    HourlyWeatherWidget.updateWidgets(forecast, context);
	}
    }

}
