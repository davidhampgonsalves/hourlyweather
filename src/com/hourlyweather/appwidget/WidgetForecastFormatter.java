package com.hourlyweather.appwidget;

import java.text.DecimalFormat;

import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.content.ContentResolver;

import com.hourlyweather.SettingsManager;
import com.hourlyweather.forecast.ForecastHour;

/**
 * Formats the Forecast details based on the current settings
 * @author dhgonsalves
 *
 */
public class WidgetForecastFormatter {

    private SettingsManager settingsManager;
    private DateTimeFormatter twelveHourClockFormat;
    private DateTimeFormatter twentyFourHourClockFormat;
    private DecimalFormat oneDecimalFormat;

    public WidgetForecastFormatter(SettingsManager settingsManager) {
	this.settingsManager = settingsManager;
	
	//setup the clock type formatters
	twelveHourClockFormat = new DateTimeFormatterBuilder().appendPattern("hh aa").toFormatter();
	twentyFourHourClockFormat = new DateTimeFormatterBuilder().appendPattern("HH:00").toFormatter();
	
	oneDecimalFormat  = new DecimalFormat("#.#");
    }

    /**
     * Returns a string thats is formatted to meet the current settings
     * 
     * @param forecastTime
     * @return
     */
    public String formatTemperature(ForecastHour forecastHour) {
	//convert celcius to farenheit and format
	if (settingsManager.isTemperatureUnitFarenheit())
	    return oneDecimalFormat.format(1.8 * forecastHour.getTemp() + 32) + "\u2109";

	//if not format as celcius
	return forecastHour.getTemp() + "\u2103";
    }

    /**
     * Returns a string thats is formatted to meet the current settings
     * 
     * @param forecastTime
     * @return
     */
    public String formatTime(MutableDateTime forecastTime, ContentResolver contentResolver) {
	//format as 12 hour clock if configured
	if (settingsManager.isClockType12(contentResolver))
	    return twelveHourClockFormat.print(forecastTime);
	    
	//if not then format as a 24 hour clock 
	return twentyFourHourClockFormat.print(forecastTime);
    }

}
