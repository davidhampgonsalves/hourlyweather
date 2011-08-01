package com.hourlyweather;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.chrono.ISOChronology;

import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.hourlyweather.forecast.ForecastBackFillUtil;
import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.yrno.forecast.ForecastFetcher;

public class ForecastCacher {
    private static final String FILENAME = "forecast";

    public static HourlyForecast getForecast(Context context, Location location) {
	// get the location and forecast
	DateTime forecastTime;
	{
	    // strip the minutes from the start time
	    MutableDateTime temp = new MutableDateTime();
	    temp.setRounding(ISOChronology.getInstance().hourOfDay(),
		    MutableDateTime.ROUND_FLOOR);
	    forecastTime = temp.toDateTime();
	}

	HourlyForecast forecast = new HourlyForecast(location.getLatitude(),
		location.getLongitude(), forecastTime, 36);
	boolean forecastFetched = ForecastFetcher.getHourlyForecast(forecast);

	// if the forecast wasn't correctly fetched
	if (!forecastFetched)
	    return null;

	ForecastBackFillUtil.backfillForecast(forecast);

	// TODO: I could delegate this to another thread
	setForecast(context, forecast);

	return forecast;
    }

    public static HourlyForecast getCachedForecast(Context context) {
	HourlyForecast cachedForecast = null;
	try {
	    FileInputStream fileReader = context.openFileInput(FILENAME);
	    ObjectInputStream forecastReader = new ObjectInputStream(fileReader);
	    cachedForecast = (HourlyForecast) forecastReader.readObject();
	} catch (Exception e) {
	    Log.e("ForecastCacher", "couldn't read forecast: " + e.getMessage());
	    return null;
	}

	// strip the minutes from the start time
	MutableDateTime now = new MutableDateTime();
	now.setRounding(ISOChronology.getInstance().hourOfDay(),
		MutableDateTime.ROUND_FLOOR);

	if (now.equals(cachedForecast.getStart()))
	    return cachedForecast;
	else
	    return null;
    }

    public static void setForecast(Context context, HourlyForecast forecast) {
	// clear any notifications to the user since we've been able to pull
	// a valid forecast.
	String ns = Context.NOTIFICATION_SERVICE;
	NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(ns);
	notificationManager.cancelAll();

	try {
	    FileOutputStream fileWriter = context.openFileOutput(FILENAME,
		    Context.MODE_PRIVATE);
	    ObjectOutputStream forecastWriter = new ObjectOutputStream(
		    fileWriter);
	    forecastWriter.writeObject(forecast);
	    fileWriter.close();
	} catch (FileNotFoundException e) {
	    Log.e("ForecastCacher", "file not found, couldn't cache forecast: "
		    + e.getMessage());
	} catch (IOException e) {
	    Log.e("ForecastCacher", "io exception, couldn't cache forecast: "
		    + e.getMessage());
	}
    }
}
