package com.hourlyweather.forecast;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;

public class HourlyForecast implements Serializable {
    private static final long serialVersionUID = 1L;
    
    Double lat, lon;

    ForecastHour[] forecastHours;
    DateTime start;
    int hours;
    DateTimeZone timeZone;

    public HourlyForecast(double lat, double lon, DateTime start, int hours) {
	this.lat = lat;
	this.lon = lon;
	this.start = start;
	this.hours = hours;
	timeZone = DateTimeZone.getDefault();

	forecastHours = new ForecastHour[hours];
    }

    public HourlyForecast(double lat, double lon, DateTime start, int hours, DateTimeZone timeZone) {
	this(lat, lon, start, hours);
	
	if(timeZone != null) {
	    this.timeZone = timeZone;
	    this.start = start.withZone(timeZone);
	}
    }
    
    public HourlyForecast(double lat, double lon, DateTime start, int hours, Integer timeZoneOffset) {
	this(lat, lon, start, hours);
	
	if(timeZoneOffset != null) {
	    timeZone = DateTimeZone.forOffsetHours(timeZoneOffset);
	    this.start = start.withZone(timeZone);
	}
    }
    
    public Double getLat() {
	return lat;
    }

    public void setLat(Double lat) {
	this.lat = lat;
    }

    public Double getLon() {
	return lon;
    }

    public void setLon(Double lon) {
	this.lon = lon;
    }

    public ForecastHour[] getForecastHours() {
	return forecastHours;
    }

    public void setForecastHours(ForecastHour[] forecastHours) {
	this.forecastHours = forecastHours;
    }

    public DateTime getStart() {
	return start;
    }

    public void setStart(DateTime start) {
	this.start = start;
    }

    public int getHours() {
	return hours;
    }

    public void setHours(int hours) {
	this.hours = hours;
    }
    
    public DateTimeZone getTimeZone() {
	return timeZone;
    }

    public int getSize() {
	return hours;
    }

    public ForecastHour get(int i) {
	return forecastHours[i];
    }

    private int getHourIndex(DateTime dateTime) {
	// calculate the difference from the forecast time to the
	// start period to map it to the array
	int hoursFromStart = Hours.hoursBetween(start, dateTime).getHours();
	if (hoursFromStart > -1 && hoursFromStart < hours)
	    // this hour is relevant to our forecast
	    return hoursFromStart;

	// if the hour falls outside the forecast then return -1
	return Hours.hoursBetween(start, dateTime).getHours();
    }

    public void set(int i, ForecastHour forecastHour) {
	if (i < hours && i >= 0)
	    forecastHours[i] = forecastHour;
    }

    /**
     * Convenience method to add a forecast hour using an hour span
     * 
     * @param i
     *            the hour index
     * @param forecastHour
     */
    public void add(DateTime from, DateTime to, ForecastHour forecastHour) {

	// check if these hours apply to our range
	if (!to.isAfter(start))
	    return;

	// use the forecast start if the spans starts before our range
	if (from.isBefore(start))
	    from = start;

	int spanStartIndex = getHourIndex(from);

	// if the span is after our
	if (spanStartIndex > hours)
	    return;

	int spanEndIndex = Hours.hoursBetween(from, to).getHours()
		+ spanStartIndex;

	// make sure the span end isn't past our hour range
	if (spanEndIndex >= hours)
	    spanEndIndex = hours - 1;

	for (int i = spanStartIndex; i <= spanEndIndex; i++)
	    if (forecastHours[i] == null)
		forecastHours[i] = forecastHour;
	    else 
		forecastHours[i] = new ForecastHour(forecastHours[i],
			forecastHour);
    }

    /**
     * mark an hour as containing a sun change dependent on isGoingUp
     * 
     * @param isGoingUp
     * @param hourIndex
     */
    public void markHourAsSunChange(boolean isGoingUp, int hourIndex) {
	if (hourIndex < hours && hourIndex >= 0) {
	    if (forecastHours[hourIndex] == null)
		forecastHours[hourIndex] = new ForecastHour();
	    forecastHours[hourIndex].setSunUp(isGoingUp);
	}
    }

    /**
     * Returns if the forecast is populated with at least one hour
     * 
     * @return
     */
    public boolean isPopulated() {
	for (ForecastHour forecastHour : forecastHours)
	    if (forecastHour != null)
		return true;
	return false;
    }
}
