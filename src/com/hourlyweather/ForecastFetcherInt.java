package com.hourlyweather;

import android.location.Location;

/**
 * Common interface for any object that can fetch a forecast given a Location
 * 
 * @author dhgonsalves
 * 
 */
public interface ForecastFetcherInt {
    public void fetchForecast(Location location);
}
