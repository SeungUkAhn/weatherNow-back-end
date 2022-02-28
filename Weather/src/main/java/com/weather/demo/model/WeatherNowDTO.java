package com.weather.demo.model;

import lombok.Data;

@Data
public class WeatherNowDTO {
	private String baseDate;
	private	String baseTime;
	private String PTY;
	private String REH;
	private String RN1;
	private String T1H;
	private String UUU;
	private String VEC;
	private String VVV;
	private String WSD;
	private String SKY;
}
