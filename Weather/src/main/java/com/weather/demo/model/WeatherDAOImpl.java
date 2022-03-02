package com.weather.demo.model;

import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.weather.demo.api.WeatherAPI;

@Repository
public class WeatherDAOImpl implements WeatherDAO {
	
	@Autowired
	private WeatherAPI weatherAPI;
	
	//초단기실황 - PTY, REH, RN1, T1H, UUU, VEC, VVV WSD 가져오는 메서드
	public void setCurrentWeatherInfo(WeatherNowDTO dto){
		
		HttpURLConnection apiConnection = null;
		
		//커넥션 생성
		try {
			
			apiConnection = weatherAPI.getApiConnection(weatherAPI.buildURL("UltraSrtNcst"));
			//커넥션으로부터 JSON 스트림 받아오기 및 "item" 단계까지 파싱
			JSONArray currentWeather = parseJsonStreamToItem(weatherAPI.getJsonStreamFromApi(apiConnection));
			
			//초단기실황 기준 파싱 후 dto에 필요값 저장
			for(int i=0; i<currentWeather.size(); i++) {
				
				//dto 객체로 변환 후 카테고리별 데이터 가공
				JSONObject item = (JSONObject) currentWeather.get(i);
		
				if(item.get("category").toString().equals("PTY")) {			//강수형태
					
					int pty = Integer.parseInt(item.get("obsrValue").toString());
					
					if(pty == 0)
						dto.setPTY("없음");
					else if(pty == 1)
						dto.setPTY("비");
						else if(pty == 2)
						dto.setPTY("비/눈");
					else if(pty == 3)
						dto.setPTY("눈");
					else if(pty == 5)
						dto.setPTY("빗방울");
					else if(pty == 6)
						dto.setPTY("빗방울눈날림");
					else if(pty == 7)
						dto.setPTY("눈날림");
				
				}else if(item.get("category").toString().equals("REH")) {	//습도
					dto.setREH(item.get("obsrValue").toString() + "%");
				}else if(item.get("category").toString().equals("RN1")) {	//1시간 강수량
					
					double rn1 = Double.parseDouble(item.get("obsrValue").toString());
					
					if(rn1 >= 50.0)
						dto.setRN1("50.0mm 이상");
					else if(rn1 >= 30.0)
						dto.setRN1("30.0 ~ 50.0mm");
					else if(rn1 >= 1.0)
						dto.setRN1(rn1 + "mm");
					else if(rn1 >= 0.1)
						dto.setRN1("1.0mm 미만");
					else
						dto.setRN1("강수없음");
					  
				}else if(item.get("category").toString().equals("T1H")) {	//기온
					dto.setT1H(Math.round(Double.parseDouble(item.get("obsrValue").toString())) + "°");	// 단위: ° or ℃
				}else if(item.get("category").toString().equals("UUU")) {	//동서바람성분
					dto.setUUU(item.get("obsrValue").toString() + "m/s");
				}else if(item.get("category").toString().equals("VEC")) {	//풍향
					
					double degree = Double.parseDouble(item.get("obsrValue").toString());
					int direction = (int) Math.floor((degree + 22.5*0.5) / 22.5);	//16방위 변환식 - 단기예보 가이드 23페이지
					String vec = "";
					
					if(direction == 0) vec = "북"; else if(direction == 1) vec = "북북동";
					else if(direction == 2) vec = "북동"; else if(direction == 3) vec = "동북동";
					else if(direction == 4) vec = "동"; else if(direction == 5) vec = "동남동";
					else if(direction == 6) vec = "남동"; else if(direction == 7) vec = "남남동";
					else if(direction == 8) vec = "남"; else if(direction == 9) vec = "남남서";
					else if(direction == 10) vec = "남서"; else if(direction == 11) vec = "서남서";
					else if(direction == 12) vec = "서"; else if(direction == 13) vec = "서북서";
					else if(direction == 14) vec = "북서"; else if(direction == 15) vec = "북북서";
					else if(direction == 16) vec = "북";
					
					dto.setVEC(vec);
					
				}else if(item.get("category").toString().equals("VVV")) {	//남북바람성분
					dto.setVVV(item.get("obsrValue").toString() + "m/s");
				}else if(item.get("category").toString().equals("WSD")) {	//풍속
					dto.setWSD(item.get("obsrValue").toString() + "m/s");
				}
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//커넥션 해제
			apiConnection.disconnect();
		}

	}//setCurrentWeatherInfo() ends
	
	//초단기예보에서 SKY값 가져오는 메서드
	public void setSkyInfo(WeatherNowDTO dto) {
		
		HttpURLConnection apiConnection = null;
		
		try {
			
			//커넥션 생성
			apiConnection = weatherAPI.getApiConnection(weatherAPI.buildURL("UltraSrtFcst"));
			
			//커넥션으로부터 JSON 스트림 받아오기 및 "item" 단계까지 파싱
			JSONArray weatherForecast = parseJsonStreamToItem(weatherAPI.getJsonStreamFromApi(apiConnection));
		  
			//초단기예보 기준 파싱 후 dto에 필요값 저장
			for(int i=0; i<weatherForecast.size(); i++) {
				
				//dto 객체로 변환 후 카테고리별 데이터 가공
				JSONObject item = (JSONObject) weatherForecast.get(i);
		
				if(item.get("category").toString().equals("SKY")) {
					
					String sky = item.get("fcstValue").toString();
					
					if(sky.equals("1"))
						dto.setSKY("맑음");
					else if(sky.equals("3"))
						dto.setSKY("구름많음");
					else if(sky.equals("4"))
						dto.setSKY("흐림");
					
					break;
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//커넥션 해제
			apiConnection.disconnect();
		}
	
	}//setSkyInfo() ends

	//버전정보 - baseDate, baseTime 가져오는 메서드
	public void setForecastVersion(WeatherNowDTO dto) {
		
		HttpURLConnection apiConnection = null;
		
		try {
			
			//커넥션 생성
			apiConnection = weatherAPI.getApiConnection(weatherAPI.buildURL("FcstVersion"));
			
			//커넥션으로부터 JSON 스트림 받아오기 및 "item" 단계까지 파싱
			JSONArray forecastVersion = parseJsonStreamToItem(weatherAPI.getJsonStreamFromApi(apiConnection));
			
			//예보버전 기준 파싱 후 dto에 필요값 저장 
			for(int i=0; i<forecastVersion.size(); i++) {
				
				JSONObject item = (JSONObject) forecastVersion.get(i);
				
				if(item.get("filetype").toString().equals("ODAM")) {
					
					String version = item.get("version").toString();
					
					String year = version.substring(2, 4);
					String month = version.substring(4, 6);
					String date = version.substring(6, 8);
					String hour = version.substring(8, 10);
					String minute = version.substring(10, 12);
					
					if(Integer.parseInt(hour) > 12) {
						hour = "오후 " + hour;
					}else {
						hour = "오전 " + hour;
					}
					
					String day = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
					
					dto.setBaseDate(year + "/" + month + "/" + date + " " + day);
					dto.setBaseTime(hour + ":" + minute);
					
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//커넥션 해제
			apiConnection.disconnect();
		}
  	
	}//setForecastVersion() ends
	
	//단기예보 - WeatherShorTerm DTO 가져오는 메서드
	public void setShortTermWeatherInfo(List<WeatherShortTermDTO> list) {
		
		HttpURLConnection apiConnection = null;
		
		try {
			
			//커넥션 생성
			apiConnection = weatherAPI.getApiConnection(weatherAPI.buildURL("VilageFcst"));
			
			//커넥션으로부터 JSON 스트림 받아오기 및 "item" 단계까지 파싱
			JSONArray shortTermWeather = parseJsonStreamToItem(weatherAPI.getJsonStreamFromApi(apiConnection));
			
			//단기예보 기준 파싱 후 dto에 필요값 저장
			String fcstTime = "";
			
			WeatherShortTermDTO dto = null;
			
			for(int i=0; i<shortTermWeather.size(); i++) {
				
				//하나의 item 가져오기
				JSONObject item = (JSONObject) shortTermWeather.get(i);
				
				if(!fcstTime.equals(item.get("fcstTime").toString())){
					
					dto = new WeatherShortTermDTO();
					fcstTime = item.get("fcstTime").toString();
				}
				
				//카테고리값 받아오기
				if(item.get("category").toString().equals("POP")) { 
					dto.setPOP(item.get("fcstValue").toString()); 
				}else if(item.get("category").toString().equals("PTY")) {
					dto.setPTY(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("PCP")) {
					dto.setPCP(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("REH")) {
					dto.setREH(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("SNO")) {
					dto.setSNO(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("SKY")) {
					dto.setSKY(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("TMP")) {
					dto.setTMP(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("VEC")) {
					dto.setVEC(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("WSD")) {
					dto.setWSD(item.get("fcstValue").toString());
				}
				
				JSONObject itemToCompare = null;
				
				if(i != shortTermWeather.size()-1) {
					 
					itemToCompare = (JSONObject) shortTermWeather.get(i + 1);
					 
					 if(!fcstTime.equals(itemToCompare.get("fcstTime"))) {
							
						 	dto.setFcstTime(item.get("fcstTime").toString());
							dto.setFcstDate(item.get("fcstDate").toString());
							list.add(dto);
							fcstTime = item.get("fcstTime").toString();
					 }
					 
				}else {
					
					dto.setFcstTime(item.get("fcstTime").toString());
					dto.setFcstDate(item.get("fcstDate").toString());
					list.add(dto);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//커넥션 해제
			apiConnection.disconnect();
		}
		
	}//setShortTermWeatherInfo() ends
	
	public JSONArray parseJsonStreamToItem(String stream) {
		
		JSONArray jsonArray = null;
		
		try {
			//item 단계까지 파싱
			JSONParser jsonParser = new JSONParser(); 
			JSONObject jsonObject = (JSONObject) jsonParser.parse(stream);
			JSONObject response = (JSONObject) jsonObject.get("response");
			JSONObject body = (JSONObject) response.get("body");
			JSONObject items = (JSONObject) body.get("items");
			
			jsonArray = (JSONArray) items.get("item"); 
			
		} catch (ParseException e) {

			e.printStackTrace();
		}		
		
		return jsonArray;
	}
	
}
