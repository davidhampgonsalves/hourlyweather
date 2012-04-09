package com.hourlyweather.forecast;

import java.io.Serializable;
import java.util.HashMap;

public class ForecastHour implements Serializable {

    private static final long serialVersionUID = 1L;
    private Double temp;
    private Double precipitation, windSpeed;
    private String symbol;
    private Boolean isSunUp;
    
    private static final HashMap<String, Integer> symbolCodes;
    
    static {
	symbolCodes = new HashMap<String, Integer>(); 
	symbolCodes.put("SUN", 1);
	symbolCodes.put("LIGHTCLOUD", 2);
	symbolCodes.put("PARTLYCLOUD", 3);
	symbolCodes.put("CLOUD", 4);
	symbolCodes.put("LIGHTRAINSUN", 5);
	symbolCodes.put("LIGHTRAINTHUNDERSUN", 6);
	symbolCodes.put("SLEETSUN", 7);
	symbolCodes.put("SNOWSUN", 8);
	symbolCodes.put("LIGHTRAIN", 9);
	symbolCodes.put("RAIN", 10);
	symbolCodes.put("RAINTHUNDER", 11);
	symbolCodes.put("SLEET", 12);
	symbolCodes.put("SNOW", 13);
	symbolCodes.put("SNOWTHUNDER", 14);
	symbolCodes.put("FOG", 15);
    }

    public ForecastHour() {
	precipitation = null;
	temp = null;
	symbol = null;
    }

    public ForecastHour(ForecastHour existing, ForecastHour updated) {

	this.symbol = updated.getSymbol() == null ? existing.getSymbol()
		: updated.getSymbol();

	this.temp = updated.getTemp() == null ? existing.getTemp() : updated
		.getTemp();

	this.precipitation = updated.getPrecipitation() == null ? existing
		.getPrecipitation() : updated.getPrecipitation();

	this.windSpeed = updated.getWindSpeed() == null ? existing
		.getWindSpeed() : updated.getWindSpeed();

	this.isSunUp = updated.isSunUp() == null ? existing.isSunUp() : updated
		.isSunUp();
    }

    public Double getTemp() {
	return temp;
    }

    public void setTemp(String temp) {
	try {
	    this.temp = Double.parseDouble(temp);
	} catch (Exception e) {
	    // nothing we can do now, the api may have changed
	}
    }

    /*
     * 1 SUN 2 LIGHTCLOUD 3 PARTLYCLOUD 4 CLOUD 5 LIGHTRAINSUN 6
     * LIGHTRAINTHUNDERSUN 7 SLEETSUN 8 SNOWSUN 9 LIGHTRAIN 10 RAIN 11
     * RAINTHUNDER 12 SLEET 13 SNOW 14 SNOWTHUNDER 15 FOG
     */
    public void setTemp(Double temp) {
	this.temp = temp;
    }

    public String getSymbol() {
	return symbol;
    }
    
    public int getSymbolCode() {
	return symbolCodes.get(symbol);
    }

    public void setSymbol(String symbol) {
	this.symbol = symbol;
    }

    public void setPrecipitation(Double precipitation) {
	this.precipitation = precipitation;
    }

    public Double getPrecipitation() {
	return precipitation;
    }

    public void setWindSpeed(Double windSpeed) {
	this.windSpeed = windSpeed;
    }

    public Double getWindSpeed() {
	return windSpeed;
    }

    public void setSunUp(Boolean isSunUp) {
	this.isSunUp = isSunUp;
    }

    public Boolean isSunUp() {
	return isSunUp;
    }

    /**
     * combines the current forecast hour with the passed in parameter. The
     * passed in objects parameters take presidence
     * 
     * @param forecastHour
     */
    public void combine(ForecastHour forecastHour) {
	if (forecastHour == null)
	    return;

	if (forecastHour.getTemp() != null)
	    temp = forecastHour.getTemp();

	if (forecastHour.getSymbol() != null)
	    symbol = forecastHour.getSymbol();

	if (forecastHour.getPrecipitation() != null)
	    precipitation = forecastHour.getPrecipitation();

	if (forecastHour.isSunUp() != null)
	    isSunUp = forecastHour.isSunUp();
    }

    /**
     * fills in any unpopulated fields with the passed in forecastHours values
     * 
     * @param forecastHour
     */
    public void backFill(ForecastHour forecastHour) {
	if (forecastHour == null)
	    return;

	if (temp == null)
	    temp = forecastHour.getTemp();

	if (symbol == null)
	    symbol = forecastHour.getSymbol();

	if (precipitation == null)
	    precipitation = forecastHour.getPrecipitation();

	if (windSpeed == null)
	    windSpeed = forecastHour.getWindSpeed();

	if (isSunUp == null)
	    isSunUp = forecastHour.isSunUp();
    }

    public boolean complete() {
	if (temp != null && symbol != null && isSunUp != null
		&& precipitation != null && windSpeed != null)
	    return true;

	return false;
    }
}
