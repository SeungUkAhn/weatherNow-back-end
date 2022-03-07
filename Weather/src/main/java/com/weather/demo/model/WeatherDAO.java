package com.weather.demo.model;
import java.util.List;
import org.json.simple.JSONArray;

public interface WeatherDAO {
	
	
	void setCurrentWeatherInfo(WeatherNowDTO dto);
	void setSkyInfo(WeatherNowDTO dto);
	void setForecastVersion(WeatherNowDTO dto);
	void setShortTermWeatherInfo(List<WeatherShortTermDTO> list);
	JSONArray parseJsonStreamToItem(String stream);
	String convertCode(String code, String value);
}
