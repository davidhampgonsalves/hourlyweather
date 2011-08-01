package com.hourlyweather.appwidget;

import org.joda.time.MutableDateTime;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.hourlyweather.ForecastCacher;
import com.hourlyweather.HourlyWeather;
import com.hourlyweather.R;
import com.hourlyweather.SettingsManager;
import com.hourlyweather.forecast.ForecastHour;
import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.util.WidgetForecastIconUtil;

/**
 * A simple scroll list to work around the lack of a core android one. Displays
 * the forecast in the widget and handles scrolling via buttons and launching
 * the main app.
 * 
 * @author dhgonsalves
 * 
 */
public class WidgetScrollList extends Service {

    public static final String NEXT = "NEXT";
    public static final String BACK = "BACK";

    public static final int LIST_SLOT_COUNT = 6;
    private static final String PREF_NAME = "HOURLY_WEATHER_PREFS";
    private static final String PREF_POSITION = "POSITION";

    public static String WIDGET_INDEX = "com.hourlyweather.widget.index";

    @Override
    public void onStart(Intent intent, int startId) {
	System.out.println("called!");
	super.onStart(intent, startId);

	// handle the scroll action
	if (NEXT.equals(intent.getAction()))
	    addToPosition(1);
	else if (BACK.equals(intent.getAction()))
	    addToPosition(-1);

	// update view to reflect position change if cached copy exists
	HourlyForecast forecast = ForecastCacher.getCachedForecast(this);
	if (forecast != null)
	    populateList(this, forecast);
    }

    /**
     * get the current list index
     */
    public static int getPosition(Context context) {
	SharedPreferences config = context.getSharedPreferences(PREF_NAME, 0);
	return config.getInt(PREF_POSITION, 0);
    }

    /**
     * Set the current list index
     */
    public static void setPosition(Context context, int value) {

	SharedPreferences.Editor configEditor = context.getSharedPreferences(
		PREF_NAME, 0).edit();
	configEditor.putInt(PREF_POSITION, value);
	configEditor.commit();
    }

    /**
     * Add the passed value to the current position if it results in a possiable
     * position
     */
    protected void addToPosition(int value) {
	SharedPreferences config = getSharedPreferences(PREF_NAME, 0);

	int position = config.getInt(PREF_POSITION, 0) + value;
	if (position < 0
		|| position >= HourlyWeather.FORECAST_HOUR_SPAN
			- LIST_SLOT_COUNT)
	    return;

	SharedPreferences.Editor configEditor = config.edit();
	configEditor.putInt(PREF_POSITION, position);
	configEditor.commit();
    }

    /**
     * Hooks up the forecast list buttons which are next, back and the forecast
     * area which starts the parent application
     */
    protected static void hookUpListButtons(Context context, RemoteViews views) {
	// add the scroll buttons press handling
	Intent scrollIntent = new Intent(context, WidgetScrollList.class);
	views.setOnClickPendingIntent(
		R.id.next,
		PendingIntent.getService(context, 0,
			scrollIntent.setAction(WidgetScrollList.NEXT), 0));
	views.setOnClickPendingIntent(
		R.id.back,
		PendingIntent.getService(context, 0,
			scrollIntent.setAction(WidgetScrollList.BACK), 0));

	// set up click listener to start parent application if the forecast is
	// clicked and scroll to the current forecast time span
	Intent hourlyWeatherIntent = new Intent(context, HourlyWeather.class);
	hourlyWeatherIntent.setAction(Intent.ACTION_VIEW);
	hourlyWeatherIntent.putExtra(WIDGET_INDEX, getPosition(context));
	hourlyWeatherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	hourlyWeatherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	hourlyWeatherIntent.setData((Uri.parse("foobar://"
		+ SystemClock.elapsedRealtime())));
	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
		hourlyWeatherIntent, 0);

	views.setOnClickPendingIntent(R.id.weather_content, pendingIntent);
    }

    /**
     * Populates the list with the passed in forecast at the current position
     * applying the current settings and formatting options
     */
    public static void populateList(Context context, HourlyForecast forecast) {
	// get the settings to use
	SettingsManager settings = new SettingsManager(
		context.getSharedPreferences("com.hourlyweather",
			Context.MODE_PRIVATE));

	// get the weather formatter
	WidgetForecastFormatter formatter = new WidgetForecastFormatter(
		settings);

	// create the view
	RemoteViews views = new RemoteViews(context.getPackageName(),
		R.layout.widget);
	views.removeAllViews(R.id.weather_content);

	hookUpListButtons(context, views);

	ForecastHour forecastHour;
	int position = WidgetScrollList.getPosition(context);
	for (int j = position; j < WidgetScrollList.LIST_SLOT_COUNT + position; j++) {
	    forecastHour = forecast.getForecastHours()[j];

	    // set the forecast widget type to current, day or night
	    int widgetTypeId;
	    boolean useAlt = j % 2 == 0 ? true : false;
	    if (j == 0)
		widgetTypeId = R.layout.widget_forecast_item_current;
	    else if (forecastHour.isSunUp())
		if (useAlt)
		    widgetTypeId = R.layout.widget_forecast_item_alt;
		else
		    widgetTypeId = R.layout.widget_forecast_item;
	    else if (useAlt)
		widgetTypeId = R.layout.widget_forecast_night_item_alt;
	    else
		widgetTypeId = R.layout.widget_forecast_night_item;
	    // create the forecast hour widget
	    RemoteViews forecastItem = new RemoteViews(
		    context.getPackageName(), widgetTypeId);

	    forecastItem.setTextViewText(R.id.temp,
		    formatter.formatTemperature(forecastHour));

	    // get the time that corresponds to this forecast
	    MutableDateTime forecastTime = new MutableDateTime(
		    forecast.getStart());
	    forecastTime.addHours(j);
	    forecastItem.setTextViewText(
		    R.id.time,
		    formatter.formatTime(forecastTime,
			    context.getContentResolver()));

	    forecastItem.setImageViewResource(R.id.icon,
		    WidgetForecastIconUtil.getIconId(forecastHour));

	    views.addView(R.id.weather_content, forecastItem);
	}

	// Push update for this widget to the home screen
	// ComponentName thisWidget = new
	// ComponentName(context,HourlyWeatherWidget.class);
	ComponentName thisWidget = new ComponentName("com.hourlyweather",
		"com.hourlyweather.appwidget.HourlyWeatherWidget");
	AppWidgetManager manager = AppWidgetManager.getInstance(context);
	manager.updateAppWidget(thisWidget, views);
    }

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }
}
