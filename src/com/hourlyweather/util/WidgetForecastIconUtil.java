package com.hourlyweather.util;

import java.util.HashMap;

import com.hourlyweather.R;
import com.hourlyweather.forecast.ForecastHour;

/**
 * This class handles the mapping from weather strings to icon resource ids for
 * the widget
 * 
 * @author dhgonsalves
 * 
 */
public class WidgetForecastIconUtil {
    private static final HashMap<String, Integer> icons = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> nightIcons = new HashMap<String, Integer>();

    static {
	icons.put("SUN", R.drawable.widget_sun);
	icons.put("LIGHTCLOUD", R.drawable.widget_lightcloud);
	icons.put("PARTLYCLOUD", R.drawable.widget_partlycloud);
	icons.put("CLOUD", R.drawable.widget_cloud);
	icons.put("LIGHTRAINSUN", R.drawable.widget_lightrainsun);
	icons.put("LIGHTRAINTHUNDERSUN", R.drawable.widget_lightrainthundersun);
	icons.put("SLEETSUN", R.drawable.widget_sleetsun);
	icons.put("SNOWSUN", R.drawable.widget_snowsun);
	icons.put("LIGHTRAIN", R.drawable.widget_lightrain);
	icons.put("RAIN", R.drawable.widget_rain);
	icons.put("RAINTHUNDER", R.drawable.widget_rainthunder);
	icons.put("SLEET", R.drawable.widget_sleet);
	icons.put("SNOW", R.drawable.widget_snow);
	icons.put("SNOWTHUNDER", R.drawable.widget_snowthunder);
	icons.put("FOG", R.drawable.widget_fog);

	nightIcons.put("SUN", R.drawable.widget_night_sun);
	nightIcons.put("PARTLYCLOUD", R.drawable.widget_night_partlycloud);
	nightIcons.put("LIGHTRAINSUN", R.drawable.widget_night_lightrainsun);
	nightIcons.put("LIGHTRAINTHUNDERSUN",
		R.drawable.widget_night_lightrainthundersun);
	nightIcons.put("SLEETSUN", R.drawable.widget_night_sleetsun);
	nightIcons.put("SNOWSUN", R.drawable.widget_night_snowsun);
    }

    /**
     * returns a drawable resource id based on a weather string
     * 
     * @param weather
     * @return
     */
    public static Integer getIconId(ForecastHour forecast) {
	if (!forecast.isSunUp()) {
	    Integer iconId = nightIcons.get(forecast.getSymbol());
	    if (iconId != null)
		return iconId;
	}
	return icons.get(forecast.getSymbol());
    }
}
