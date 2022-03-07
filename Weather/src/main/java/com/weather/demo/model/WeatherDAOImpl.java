package com.weather.demo.model;

import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
	@Override
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
					
					String PTY = item.get("obsrValue").toString();
					dto.setPTY(convertCode("PTY", PTY)); 
				
				}else if(item.get("category").toString().equals("REH")) {	//습도
					
					String REH = item.get("obsrValue").toString();
					dto.setREH(convertCode("REH", REH));
					
				}else if(item.get("category").toString().equals("RN1")) {	//1시간 강수량
					
					String RN1 = item.get("obsrValue").toString();
					dto.setRN1(convertCode("RN1", RN1));
					  
				}else if(item.get("category").toString().equals("T1H")) {	//기온
					
					String T1H = item.get("obsrValue").toString();
					dto.setT1H(convertCode("T1H", T1H));
					
				}else if(item.get("category").toString().equals("UUU")) {	//동서바람성분
					
					String UUU = item.get("obsrValue").toString();
					dto.setUUU(convertCode("UUU", UUU));
					
				}else if(item.get("category").toString().equals("VEC")) {	//풍향
					
					String VEC = item.get("obsrValue").toString();
					dto.setVEC(convertCode("VEC", VEC));
					
				}else if(item.get("category").toString().equals("VVV")) {	//남북바람성분
					
					String VVV = item.get("obsrValue").toString();
					dto.setVVV(convertCode("VVV", VVV));
					
				}else if(item.get("category").toString().equals("WSD")) {	//풍속
					
					String WSD = item.get("obsrValue").toString();
					dto.setWSD(convertCode("WSD", WSD));
					
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
	@Override
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
					
					String SKY = item.get("fcstValue").toString();
					dto.setSKY(convertCode("SKY", SKY));
					
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
	@Override
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
	@Override
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
					
					String PTY = item.get("fcstValue").toString();
					dto.setPTY(convertCode("PTY", PTY));
					
				}else if(item.get("category").toString().equals("PCP")) {
					dto.setPCP(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("REH")) {
					
					String REH = item.get("fcstValue").toString();
					dto.setREH(convertCode("REH", REH));
					
				}else if(item.get("category").toString().equals("SNO")) {
					dto.setSNO(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("SKY")) {
					
					String SKY = item.get("fcstValue").toString();
					dto.setSKY(convertCode("SKY", SKY));
					
				}else if(item.get("category").toString().equals("TMP")) {
					dto.setTMP(item.get("fcstValue").toString());
				}else if(item.get("category").toString().equals("VEC")) {
					
					String VEC = item.get("fcstValue").toString();
					dto.setVEC(convertCode("VEC", VEC));
					
				}else if(item.get("category").toString().equals("WSD")) {

					String WSD = item.get("fcstValue").toString();
					dto.setWSD(convertCode("WSD", WSD));
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

	//각 코드를 API문서에 맞게 해석하여 값을 변경하거나 단위를 붙여주는 메서드 
	@Override
	public String convertCode(String code, String value) { 
		
		
		switch(code) {
		
			case "PTY":
				if(value.equals("0"))
					return "없음";
				else if(value.equals("1"))
					return "비";
				else if(value.equals("2"))
					return "비/눈";
				else if(value.equals("3"))
					return "눈";
				else if(value.equals("5"))
					return "빗방울";
				else if(value.equals("6"))
					return "빗방울눈날림";
				else if(value.equals("7"))
					return "눈날림";
			case "REH":
				return value + "%";
			case "RN1" :
				double rn1 = Double.parseDouble(value);
				
				if(rn1 >= 50.0)
					return "50.0mm 이상";
				else if(rn1 >= 30.0)
					return "30.0 ~ 50.0mm";
				else if(rn1 >= 1.0)
					return value + "mm";
				else if(rn1 >= 0.1)
					return "1.0mm 미만";
				else
					return "강수없음";
			case "T1H" :
				return Math.round(Double.parseDouble(value)) + "°";
			case "UUU" :
				return value + "m/s";
			case "VEC" :
				double degree = Double.parseDouble(value);
				int direction = (int) Math.floor((degree + 22.5*0.5) / 22.5);	//16방위 변환식 - 단기예보 가이드 23페이지
				
				if(direction == 0) return "북"; else if(direction == 1) return "북북동";
				else if(direction == 2) return "북동"; else if(direction == 3) return "동북동";
				else if(direction == 4) return "동"; else if(direction == 5) return "동남동";
				else if(direction == 6) return "남동"; else if(direction == 7) return "남남동";
				else if(direction == 8) return "남"; else if(direction == 9) return "남남서";
				else if(direction == 10) return "남서"; else if(direction == 11) return "서남서";
				else if(direction == 12) return "서"; else if(direction == 13) return "서북서";
				else if(direction == 14) return "북서"; else if(direction == 15) return "북북서";
				else if(direction == 16) return "북";
			case "VVV" : 
				return value + "m/s";
			case "WSD" :
				return value + "m/s";
			case "SKY" :
				if(value.equals("1"))
					return "맑음";
				else if(value.equals("3"))
					return "구름많음";
				else if(value.equals("4"))
					return "흐림";
		}
		
		return "-";
	}//convertCode() ends
	
}
