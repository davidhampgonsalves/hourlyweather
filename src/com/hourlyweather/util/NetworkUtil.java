package com.hourlyweather.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utilities for testing network connectivity
 * @author dhgonsalves
 *
 */
public class NetworkUtil {
    /**
     * Checks if there is an active connection
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
	ConnectivityManager connectivityManager = (ConnectivityManager) context
		.getSystemService(Context.CONNECTIVITY_SERVICE);
	
	NetworkInfo activeNetworkInfo = connectivityManager
		.getActiveNetworkInfo();
	
	return activeNetworkInfo != null;
    }
}
