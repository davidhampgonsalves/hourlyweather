package com.hourlyweather;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.MutableDateTime;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hourlyweather.forecast.ForecastHour;
import com.hourlyweather.forecast.HourlyForecast;
import com.hourlyweather.util.ForecastIconUtil;

/**
 * Simple forecast adapter that displays the hourly forecast while applying the
 * current settings and format options
 * 
 * @author dhgonsalves
 * 
 */
public class ForecastListAdapter extends ArrayAdapter<ForecastHour> {

    private HourlyForecast forecast;
    private ForecastFormatter forecastFormatter;

    public ForecastListAdapter(Context context) {
	super(context, R.layout.hourlyweather_forecast_item);
    }

    public ForecastListAdapter(Context context, HourlyForecast forecast) {
	super(context, R.layout.hourlyweather_forecast_item, forecast
		.getForecastHours());
	this.forecast = forecast;
    }

    public void setForecast(HourlyForecast forecast) {
	this.forecast = forecast;
	notifyDataSetChanged();
    }

    public DateTime getForecastTime() {
	if (forecast != null)
	    return forecast.getStart();
	return null;
    }

    @Override
    public ForecastHour getItem(int position) {
	if (position < forecast.getForecastHours().length)
	    return forecast.getForecastHours()[position];
	else
	    return null;
    }

    @Override
    public int getCount() {
	if (forecast != null && forecast.getForecastHours() != null)
	    return forecast.getForecastHours().length;
	return 0;
    }

    @Override
    public boolean isEmpty() {
	if (forecast == null || forecast.getForecastHours() == null)
	    return true;
	else
	    return forecast.getForecastHours().length == 0;
    }

    @Override
    public boolean hasStableIds() {
	return true;
    }

    @Override
    public boolean isEnabled(int position) {
	return false;
    }

    /**
     * gets the view of a forecast hour which includes the weather symbol and
     * the daylight indicator
     */
    public View getView(int id, View view, ViewGroup viewGroup) {
	ViewHolder holder;
	if (view == null) {
	    LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
		    Context.LAYOUT_INFLATER_SERVICE);
	    view = (LinearLayout) vi.inflate(
		    R.layout.hourlyweather_forecast_item, null);

	    holder = new ViewHolder();
	    holder.icon = (ImageView) view.findViewById(R.id.icon);
	    holder.time = (TextView) view.findViewById(R.id.time);
	    holder.temp = (TextView) view.findViewById(R.id.temp);
	    holder.date = (TextView) view.findViewById(R.id.date);
	    holder.precipitation = (TextView) view
		    .findViewById(R.id.precipitation);
	    holder.windSpeed = (TextView) view.findViewById(R.id.wind_speed);
	    view.setTag(holder);
	} else
	    holder = (ViewHolder) view.getTag();

	// get the time that corresponds to this forecast
	MutableDateTime forecastTime = new MutableDateTime(forecast.getStart());
	forecastTime.addHours(id);

	// show or hide the date area
	if (id == 0 || forecastTime.get(DateTimeFieldType.hourOfDay()) == 0) {
	    // set the date if this is a day change case
	    holder.date.setVisibility(View.VISIBLE);
	    holder.date.setText(forecastFormatter.formatDate(forecastTime));
	} else
	    holder.date.setVisibility(View.GONE);

	ForecastHour forecastHour = getItem(id);
	// get the symbol to represent this hours forecast
	if (forecastHour.getSymbol() != null)
	    holder.icon.setImageResource(ForecastIconUtil
		    .getIconId(forecastHour));

	// set the background to reflect daylight
	int backgroundColorId, textColorId;
	if (id == 0) {
	    holder.time.setText("now");
	    textColorId = R.color.day_text;
	    backgroundColorId = R.color.current;
	} else {
	    // use alt contols the alternating colors to use
	    boolean useAlt = id % 2 == 0 ? true : false;
	    // set the day night
	    if (forecastHour.isSunUp()) {
		if (useAlt)
		    backgroundColorId = R.color.day_alt;
		else
		    backgroundColorId = R.color.day;
		textColorId = R.color.day_text;
	    } else {
		if (useAlt)
		    backgroundColorId = R.color.night_alt;
		else
		    backgroundColorId = R.color.night;
		textColorId = R.color.night_text;
	    }
	    holder.time.setText(forecastFormatter.formatTime(forecastTime,
		    getContext().getContentResolver()));
	}

	// set the text and background colors
	Resources resources = getContext().getResources();
	holder.temp.setTextColor(resources.getColor(textColorId));
	holder.time.setTextColor(resources.getColor(textColorId));
	holder.precipitation.setTextColor(resources.getColor(textColorId));
	holder.windSpeed.setTextColor(resources.getColor(textColorId));
	view.setBackgroundColor(resources.getColor(backgroundColorId));

	// set the forecasts temperature
	holder.temp.setText(forecastFormatter.formatTemperature(forecastHour));
	// set the precipitation
	holder.precipitation.setText(forecastFormatter
		.formatPrecipitation(forecastHour));
	// set the wind speed
	holder.windSpeed.setText(forecastFormatter
		.formatWindSpeed(forecastHour));

	return view;
    }

    static class ViewHolder {
	public TextView temp;
	public TextView time;
	public TextView precipitation;
	public TextView windSpeed;
	public ImageView icon;
	public TextView date;
    }

    public void setFormatter(ForecastFormatter forecastFormatter) {
	this.forecastFormatter = forecastFormatter;
    }
}