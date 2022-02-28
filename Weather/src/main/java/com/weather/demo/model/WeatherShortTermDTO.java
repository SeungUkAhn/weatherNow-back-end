package com.weather.demo.model;

import lombok.Data;

@Data
public class WeatherShortTermDTO {

	private String POP; //강수확률
	private String PTY; //강수형태
	private String PCP; //1시간 강수량(단위: mm)
	private String REH; //습도
	private String SNO; //1시간 신적설(적설량, 단위: cm)
	private String SKY; //하늘상태
	private String TMP; //1시간 기온
	private String VEC;	//풍향
	private String WSD;	//풍속
	private String fcstTime; //예보시간
	private String fcstDate; //예보날짜
}
