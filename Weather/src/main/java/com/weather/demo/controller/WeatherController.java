package com.weather.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weather.demo.model.WeatherNowDTO;
import com.weather.demo.model.WeatherShortTermDTO;
import com.weather.demo.service.WeatherService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/")
public class WeatherController {

	@Autowired
	private WeatherService service; 
	
	//현재 날씨를 보여주기 위한 컨트롤러
	@GetMapping("currentweather")
	public WeatherNowDTO currentWeather() throws IOException, ParseException {
		
    	return service.getCurrentWeather();
	}
	
	//단기 날씨 예보를 보여주기 위한 컨트롤러
	@GetMapping("shorttermweather")
	public List<WeatherShortTermDTO> shorttermWeather() throws IOException, ParseException {
		
    	return service.getShortTermWeather();
	}

}