package com.hourlyweather;

import java.text.DecimalFormat;

import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.content.ContentResolver;

import com.hourlyweather.forecast.ForecastHour;

/**
 * Formats the Forecast details based on the current settings
 * @author dhgonsalves
 *
 */
public class ForecastFormatter {

    private SettingsManager settingsManager;
    private DateTimeFormatter dateFormat;
    private DateTimeFormatter twelveHourClockFormat;
    private DateTimeFormatter twentyFourHourClockFormat;
    private DecimalFormat oneDecimalFormat;

    public ForecastFormatter(SettingsManager settingsManager) {
	this.settingsManager = settingsManager;
	
	//setup the clock type formatters
	twelveHourClockFormat = new DateTimeFormatterBuilder().appendPattern("hh:00 aa").toFormatter();
	twentyFourHourClockFormat = new DateTimeFormatterBuilder().appendPattern("HH:00").toFormatter();
	
	//setup the date formatter
	dateFormat = new DateTimeFormatterBuilder().appendPattern("EEEE, MMMM dd").toFormatter();
	
	oneDecimalFormat  = new DecimalFormat("#.#");
    }

    /**
     * Returns a string thats is formatted to meet the current settings
     * 
     * @param forecastTime
     * @return
     */
    public String formatTemperature(ForecastHour forecastHour) {
	//convert celsius to fahrenheit and format
	String tempString;
	if (settingsManager.isTemperatureUnitFarenheit())
	    tempString =  oneDecimalFormat.format(1.8 * forecastHour.getTemp() + 32) + "\u2109";
	else
	    tempString =  forecastHour.getTemp() + "\u2103";
	
	//if not format as celsius
	return "temp: " + tempString;
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

    public String formatDate(MutableDateTime forecastDate)
    {
	return dateFormat.print(forecastDate);
    }
    
    public String formatPrecipitation(ForecastHour forecastHour)
    {
	String precipitation;
	if(forecastHour.getPrecipitation() != null)
	{
	    if(settingsManager.isMeasurementSystemMetric())
		precipitation = oneDecimalFormat.format(forecastHour.getPrecipitation()) + " mm";
	    else
		precipitation = oneDecimalFormat.format(forecastHour.getPrecipitation() * 0.0393700787) + "\"";
	}else
	    precipitation =  "none";
	
	return "precip: " + precipitation;
    }
    
    public String formatWindSpeed(ForecastHour forecastHour)
    {
	String windSpeed;
	if(settingsManager.isMeasurementSystemMetric())
	    //convert m/s to km/h
	    windSpeed = oneDecimalFormat.format(forecastHour.getWindSpeed() * 3.6) + " km/h";
	else
	    windSpeed = oneDecimalFormat.format(forecastHour.getWindSpeed() * 2.23693629) + " mph";
	
	return "wind: " + windSpeed;
    }
}
