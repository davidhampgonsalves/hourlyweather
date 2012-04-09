package com.hourlyweather.yrno.sunrise;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.yrno.XmlParserUtil;

public class SunriseFetcher {
    private static final DateTimeFormatter df = DateTimeFormat.forPattern(
	    "YYYY-MM-dd'T'HH:mm:SS'Z'").withZone(DateTimeZone.UTC);

    public static void addSunlightDurationToForecast(HourlyForecast forecast) {
	XmlPullParser xpp;
	try {
	    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    xpp = factory.newPullParser();
	} catch (XmlPullParserException e) {
	    System.out.println("error creating xml parser: " + e.getMessage());
	    return;
	}

	InputStream input = getSunriseData(forecast);
	try {
	    xpp.setInput(new BufferedReader(new InputStreamReader(input)));
	    int eventType = xpp.getEventType();
	    while (eventType != XmlPullParser.END_DOCUMENT) {
		if (eventType == XmlPullParser.START_TAG
			&& "sun".equalsIgnoreCase(xpp.getName())) {

		    // found the sun rise data, now get the duration
		    String riseString = XmlParserUtil.getAttributeByName(xpp,
			    "rise");
		    String setString = XmlParserUtil.getAttributeByName(xpp,
			    "set");
		    if (riseString != null && setString != null) {
			DateTime rise = df.parseDateTime(riseString).withZone(forecast.getTimeZone());
			DateTime set = df.parseDateTime(setString).withZone(forecast.getTimeZone());
			setSunStateTimesToForecast(forecast, rise, set);
		    }
		    break;
		}

		eventType = xpp.next();
	    }
	    input.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static DateTime roundToTheHour(DateTime rise) {
	MutableDateTime tem = rise.toMutableDateTime();
	tem.setRounding(ISOChronology.getInstance().hourOfDay(),
		MutableDateTime.ROUND_HALF_EVEN);
	return tem.toDateTime();
    }

    /**
     * adds the suns state changes(rise and set) to the forecast
     * 
     * @param forecast
     * @param rise
     * @param set
     */
    private static void setSunStateTimesToForecast(HourlyForecast forecast,
	    DateTime rise, DateTime set) {
	// round sun set and rise times before applying to forecast
	rise = roundToTheHour(rise);
	set = roundToTheHour(set);

	// check if the start time is day or night
	int hour = forecast.getStart().getHourOfDay();
	int riseHour = rise.getHourOfDay();
	int setHour = set.getHourOfDay();
	// if hours cross midnight add 24 hours to account for the roll over
	if (riseHour > setHour) {
	    hour += 24;
	    setHour += 24;
	}
	// mark as day or night
	forecast.markHourAsSunChange(hour > riseHour && hour < setHour, 0);

	// reset the hour and setHour fields if they were changed
	if (hour >= 24) {
	    setHour = set.getHourOfDay();
	    hour = forecast.getStart().getHourOfDay();
	}
	int diff = 0;
	for (int i = 0; diff < forecast.getHours(); i++) {
	    // mark the sun rises if they are in the future
	    diff = riseHour + (24 * i) - hour;
	    if (diff >= 0)
		forecast.markHourAsSunChange(true, diff);
	    // mark the sun sets in the future
	    diff = setHour + (24 * i) - hour;
	    if (diff >= 0)
		forecast.markHourAsSunChange(false, diff);
	}
    }

    private static InputStream getSunriseData(HourlyForecast forecast) {

	// pull the sunrise xml from the yr.no api
	try {
	    URL sunriseUrl = new URL(
		    "http://api.yr.no/weatherapi/sunrise/1.0/?lat="
			    + forecast.getLat() + ";lon=" + forecast.getLon()
			    + ";date="
			    + forecast.getStart().toString("YYYY-MM-dd"));
	    return sunriseUrl.openStream();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;

    }

}
