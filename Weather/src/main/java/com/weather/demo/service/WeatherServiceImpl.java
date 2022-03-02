package com.weather.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weather.demo.model.WeatherDAO;
import com.weather.demo.model.WeatherNowDTO;
import com.weather.demo.model.WeatherShortTermDTO;

@Service
public class WeatherServiceImpl implements WeatherService {

	@Autowired
	private WeatherDAO dao;
	
	@Override
	public WeatherNowDTO getCurrentWeather() {
		
		WeatherNowDTO dto = new WeatherNowDTO();
		
		dao.setCurrentWeatherInfo(dto);
		dao.setSkyInfo(dto);
		dao.setForecastVersion(dto);
		
		return dto;
	}

	@Override
	public List<WeatherShortTermDTO> getShortTermWeather() {
		
		List<WeatherShortTermDTO> list = new ArrayList<WeatherShortTermDTO>();
		
		dao.setShortTermWeatherInfo(list);
		
		return list;
	}

}
