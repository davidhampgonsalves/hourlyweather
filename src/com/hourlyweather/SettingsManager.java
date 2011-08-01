package com.hourlyweather;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

/**
 * Provides access to the current user settings for hourly weather. This is just
 * a thin wrapper around the android shared prefs api.
 * 
 * @author dhgonsalves
 * 
 */
public class SettingsManager {

    public static final String[] temperatureUnits = new String[] { "Celcius",
	    "Fahrenheit" };
    public static final String[] measurementSystems = new String[] { "Metric",
	    "British" };
    private static final String TEMPERATURE_UNIT = "temperature unit";
    private static final String MEASUREMENT_SYSTEM = "measurement_system";

    private ForecastListAdapter forecastAdapter;
    private SharedPreferences prefs;

    public SettingsManager(SharedPreferences prefs) {
	forecastAdapter = null;
	this.prefs = prefs;
    }

    public SettingsManager(ForecastListAdapter forecastAdapter,
	    SharedPreferences prefs) {
	this.forecastAdapter = forecastAdapter;
	this.prefs = prefs;
    }

    public int getClockType(ContentResolver contentResolver) {
	int clockType;
	try {
	    // get this each time so it will reflect any changes that the user
	    // makes
	    clockType = Settings.System.getInt(contentResolver,
		    Settings.System.TIME_12_24);
	} catch (SettingNotFoundException e) {
	    clockType = 12;
	}

	return clockType;
    }

    public int getTemperatureUnit() {
	return prefs.getInt(TEMPERATURE_UNIT, 0);
    }

    public void setTemperatureUnit(int temperatureUnit) {
	prefs.edit().putInt(TEMPERATURE_UNIT, temperatureUnit).commit();
	if (forecastAdapter != null)
	    forecastAdapter.notifyDataSetChanged();
    }

    public String[] getTemperatureUnitNames() {
	return temperatureUnits;
    }

    public boolean isTemperatureUnitFarenheit() {
	return getTemperatureUnit() == 1;
    }

    public void setMeasurementSystem(int measurementSystemIndex) {
	prefs.edit().putInt(MEASUREMENT_SYSTEM, measurementSystemIndex)
		.commit();
	forecastAdapter.notifyDataSetChanged();
    }

    public int getMeasurementSystem() {
	return prefs.getInt(MEASUREMENT_SYSTEM, 0);
    }

    public boolean isMeasurementSystemMetric() {
	return prefs.getInt(MEASUREMENT_SYSTEM, 0) == 0;
    }

    public String[] getMeasurementSystemNames() {
	return measurementSystems;
    }

    public boolean isClockType12(ContentResolver contentResolver) {
	if (getClockType(contentResolver) == 12)
	    return true;
	return false;
    }
}
