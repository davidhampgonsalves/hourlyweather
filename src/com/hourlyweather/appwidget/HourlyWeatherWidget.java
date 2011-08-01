package com.hourlyweather.appwidget;

import org.joda.time.MutableDateTime;
import org.joda.time.chrono.ISOChronology;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;

import com.hourlyweather.ForecastCacher;
import com.hourlyweather.forecast.HourlyForecast;

/**
 * A AppWidget for hourly Weather that displays a scrollable list of the current
 * hourly forecast. Pressing the forecast will launch the main application at
 * the current forecast position.
 * 
 * @author dhgonsalves
 */
public class HourlyWeatherWidget extends AppWidgetProvider {
    public static final String ACTION_FORECAST_UPDATE = "com.hourlyweather.widget.FORECAST_UPDATE";
    public static final String EXTRA_FORECAST = "com.hourlyweather.forecast";
    public static final String RETRIED_POLL = "com.hourlyweather.forecast.retry";

    @Override
    public void onEnabled(Context context) {
	super.onEnabled(context);
	scheduleForecastUpdates(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	    int[] appWidgetIds) {
	super.onUpdate(context, appWidgetManager, appWidgetIds);

	// check if the forecast has been loaded yet
	if (ForecastCacher.getCachedForecast(context) == null)
	    // run the forecast update service right now
	    context.startService(new Intent(context,
		    ForecastUpdateService.class));
	else {
	    WidgetScrollList.populateList(context,
		    ForecastCacher.getCachedForecast(context));
	}
    }

    public static void scheduleForecastUpdates(Context context) {
	// before scheduling forecast updates cancel any existing ones first as
	// a precaution
	disableForecastUpdates(context);
	
	// set up the update service
	PendingIntent updateForecastIntent = PendingIntent.getService(context,
		0, new Intent(context, ForecastUpdateService.class), 0);

	AlarmManager alarmManager = (AlarmManager) context
		.getSystemService(Context.ALARM_SERVICE);

	// get the next hour to schedule the update service
	MutableDateTime startTime = new MutableDateTime(
		System.currentTimeMillis());
	startTime.setRounding(ISOChronology.getInstance().hourOfDay(),
		MutableDateTime.ROUND_CEILING);

	// schedule the forecastUpdateService to run every hour
	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		startTime.getMillis(), AlarmManager.INTERVAL_HOUR,
		updateForecastIntent);
	System.out.println("updates scheduled!!");
    }

    public static void retryForecastUpdates(Context context) {
	// set up the update service
	Intent intent = new Intent(context, ForecastUpdateService.class);
	intent.setData((Uri.parse("foobar://" + SystemClock.elapsedRealtime())));
	intent.putExtra(RETRIED_POLL, true);
	PendingIntent updateForecastIntent = PendingIntent.getService(context,
		0, intent, 0);

	AlarmManager alarmManager = (AlarmManager) context
		.getSystemService(Context.ALARM_SERVICE);

	// get the next hour to schedule the update service
	MutableDateTime startTime = new MutableDateTime();
	startTime.addMinutes(5);

	// schedule the forecastUpdateService to run every hour
	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		startTime.getMillis(), AlarmManager.INTERVAL_HOUR,
		updateForecastIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
	super.onReceive(context, intent);

	// handle forecast updates notifications from the app
	if (ACTION_FORECAST_UPDATE.equals(intent.getAction())) {
	    HourlyForecast forecast = (HourlyForecast) intent.getExtras().get(
		    EXTRA_FORECAST);
	    WidgetScrollList.populateList(context, forecast);
	}
    }

    /**
     * Send a intent with a new forecast to force the widgets to update
     * 
     * @param forecast
     * @param context
     */
    public static void updateWidgets(HourlyForecast forecast, Context context) {
	Intent updateWidget = new Intent(
		HourlyWeatherWidget.ACTION_FORECAST_UPDATE);
	updateWidget.putExtra(HourlyWeatherWidget.EXTRA_FORECAST, forecast);
	context.sendBroadcast(updateWidget);
    }

    @Override
    public void onDisabled(Context context) {
	super.onDisabled(context);
	disableForecastUpdates(context);
    }

    /**
     * cancels any pending scheduled forecast updates
     * 
     * @param context
     */
    private static void disableForecastUpdates(Context context) {
	AlarmManager alarmManager = (AlarmManager) context
		.getSystemService(Context.ALARM_SERVICE);

	PendingIntent updateForecastIntent = PendingIntent.getService(context,
		0, new Intent(context, ForecastUpdateService.class), 0);

	alarmManager.cancel(updateForecastIntent);
	
	System.out.println("updates cancelled");
    }
}
