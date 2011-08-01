package com.hourlyweather.location;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Utilities for testing and determining location
 * 
 * @author dhgonsalves
 * 
 */
public class LocationUtil {

    private static final String WAS_USER_TOLD_ABOUT_OPTIMAL_SETTINGS = "user was told about optimal settings";

    /**
     * Gets the most recent last known location which is the best one to use
     * since the weather doesn't rely on a specific location.
     * 
     * @param context
     * @param locationManager
     * @return
     */
    public static Location getBestLastKnownLocation(Context context) {
	LocationManager locationManager = (LocationManager) context
		.getSystemService(Context.LOCATION_SERVICE);

	List<String> providers = locationManager.getAllProviders();
	Location location = null, temLocation;
	for (String provider : providers) {
	    temLocation = locationManager.getLastKnownLocation(provider);

	    if (location == null
		    || (temLocation != null && location.getTime() < temLocation
			    .getTime()))
		location = temLocation;
	}

	return location;
    }

    /**
     * Returns if the device is location aware at this moment
     * 
     * @param context
     * @return
     */
    public static boolean isDeviceLocationAware(Context context,
	    LocationManager locationManager) {

	List<String> providers = locationManager.getAllProviders();
	for (String provider : providers)
	    if (locationManager.isProviderEnabled(provider))
		return true;

	return false;
    }

    /**
     * checks if the current location settings are optimal for hourly weather
     * usage
     * 
     * @return
     */
    public static boolean areLocationSettingsOptimal(
	    LocationManager locationManager) {
	// check if the location settings are set to use use wireless networks
	return !locationManager
		.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * returns if the user was told about the optimum location settings for hourly weather or not
     * @param context
     * @return
     */
    public static boolean wasUserToldAboutOptimalSettings(Context context) {
	return context.getSharedPreferences("com.hourlyweather",
		Context.MODE_PRIVATE).getBoolean(
		WAS_USER_TOLD_ABOUT_OPTIMAL_SETTINGS, false);
    }

    /**
     * marks that the user was already told about the optimum location settings for hourly weather
     * @param context
     * @return
     */
    public static void userWasToldAboutOptimalSettings(Context context) {
	context.getSharedPreferences("com.hourlyweather", Context.MODE_PRIVATE)
		.edit().putBoolean(WAS_USER_TOLD_ABOUT_OPTIMAL_SETTINGS, true)
		.commit();
    }
}
