package com.hourlyweather.util;

import java.util.HashMap;

import com.hourlyweather.R;
import com.hourlyweather.forecast.ForecastHour;

/**
 * Handles the mapping from weather strings to icon resource ids
 * @author dhgonsalves
 *
 */
public class ForecastIconUtil {
    private static final HashMap<String, Integer> icons = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> nightIcons = new HashMap<String, Integer>();
    
    static {
	icons.put("SUN", R.drawable.sun );
	icons.put("LIGHTCLOUD", R.drawable.lightcloud );
	icons.put("PARTLYCLOUD", R.drawable.partlycloud );
	icons.put("CLOUD", R.drawable.cloud );
	icons.put("LIGHTRAINSUN", R.drawable.lightrainsun );
	icons.put("LIGHTRAINTHUNDERSUN", R.drawable.lightrainthundersun );
	icons.put("SLEETSUN", R.drawable.sleetsun );
	icons.put("SNOWSUN", R.drawable.snowsun );
	icons.put("LIGHTRAIN", R.drawable.lightrain );
	icons.put("RAIN", R.drawable.rain );
	icons.put("RAINTHUNDER", R.drawable.rainthunder );
	icons.put("SLEET", R.drawable.sleet );
	icons.put("SNOW", R.drawable.snow );
	icons.put("SNOWTHUNDER", R.drawable.snowthunder );
	icons.put("FOG", R.drawable.fog );
	
	nightIcons.put("SUN", R.drawable.night_sun );
	nightIcons.put("PARTLYCLOUD", R.drawable.night_partlycloud );
	nightIcons.put("LIGHTRAINSUN", R.drawable.night_lightrainsun );
	nightIcons.put("LIGHTRAINTHUNDERSUN", R.drawable.night_lightrainthundersun );
	nightIcons.put("SLEETSUN", R.drawable.night_sleetsun );
	nightIcons.put("SNOWSUN", R.drawable.night_snowsun );
    }
    
    /**
     * returns a drawable resource id based on a weather string
     * @param weather
     * @return
     */
    public static Integer getIconId(ForecastHour forecast)
    {
	if(!forecast.isSunUp())
	{    
	    Integer iconId = nightIcons.get(forecast.getSymbol());
	    if(iconId != null)
		return iconId;
	}   
	return icons.get(forecast.getSymbol());
    }
}
