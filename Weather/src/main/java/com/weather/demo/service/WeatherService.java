package com.weather.demo.service;

import java.util.List;
import com.weather.demo.model.WeatherNowDTO;
import com.weather.demo.model.WeatherShortTermDTO;

public interface WeatherService {
	
	WeatherNowDTO getCurrentWeather();
	List<WeatherShortTermDTO> getShortTermWeather();
	
}
