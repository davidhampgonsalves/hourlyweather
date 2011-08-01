package com.hourlyweather.forecast;

public class ForecastBackFillUtil {
    /**
     * Fills in forecast fields that wern't explicitly defined by the webservice
     * response
     * 
     * @param forecast
     */
    public static void backfillForecast(HourlyForecast forecast) {

	// first push the sun up indicators to the unfilled in hours
	ForecastHour[] forecastHours = forecast.getForecastHours();
	for (int i = 1; i < forecastHours.length; i++) {

	    if (forecastHours[i] == null)
		forecastHours[i] = forecastHours[i - 1];
	    else
		forecastHours[i].backFill(forecastHours[i - 1]);
	}

	// populate the initial values if not set, using the first populated
	// values
	ForecastHour firstCompleteForecast = null;
	for (int i = 0; i >= 0 && i < forecastHours.length;)
	    if (firstCompleteForecast == null)
		if (forecastHours[i].complete()) {
		    firstCompleteForecast = forecastHours[i];
		    i--;
		} else
		    i++;
	    else {
		forecastHours[i].backFill(firstCompleteForecast);
		i--;
	    }

    }
}
