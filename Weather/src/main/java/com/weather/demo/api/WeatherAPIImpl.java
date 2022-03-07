package com.weather.demo.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Repository;
import org.springframework.web.util.UriComponentsBuilder;

@Repository
public class WeatherAPIImpl implements WeatherAPI {
	
	public HttpURLConnection getApiConnection(URL url) throws IOException {
	    
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json"); 
		
		return conn;
	}
	
	//URL을 생성하여 반환해주는 메서드
    public URL buildURL(String serviceName) throws MalformedURLException {
       
    	//시간 설정
    	int minute = LocalTime.now().getMinute();
    	int hour = LocalTime.now().getHour();	
    	
    	//초단기실황, 버전정보, 초단기예보, 단기예보 API 호출 시간 조정 (각각 40분, 40분, 45분, 10분에 시간대별 API 생성됨)
    	if(serviceName.equals("UltraSrtNcst") || serviceName.equals("FcstVersion")) {
    		if(minute < 40) {	//초단기실황, 버전정보
        		hour = LocalTime.now().minusHours(1).getHour();
        	}
    	}else if(serviceName.equals("UltraSrtFcst")) {
    		if(minute < 45) {	//초단기예보
        		hour = LocalTime.now().minusHours(1).getHour();
        	}
    	}else if(serviceName.equals("VilageFcst")) {
    		
    		//단기예보 API 생성 시각 : 2시, 5시, ..., 20시, 23시 (3시간마다 갱신)
    		hour = ((hour - 2) / 3 * 3) + 2;

    		if(hour % 3 == 2 && minute < 10) {	//단기예보
    			
    			hour = hour - 3; //base_time 갱신되는 시각이면서 10분을 안 넘을 경우 3시간 전으로 변경
    		}
    	
    	}
 
    	String base_date = LocalDate.now().toString().replace("-", "");
    	String base_time = LocalTime.of(hour, minute).toString().replace(":", "");
    	
    	//초단기 실황 : (현재 시각) 기준 날씨상태
    	String endPointURI = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/get" + serviceName;
    	String serviceKey = "&serviceKey=BU1AQL1LYTfkypMjkmq5kVxyCrq2NZW1fc%2BITRb38KqoXLV%2BwCqMWxfdS8%2BupU0XoXY%2FQpNs6pdYN56LeGsn5A%3D%3D";
    	
    	String builder = "";
    	
    	if(serviceName.equals("FcstVersion")) {		//예보버전 호출일때 파라미터 설정
    		builder = UriComponentsBuilder.fromUriString(endPointURI)
	                .queryParam("pageNo", "1")
	                .queryParam("numOfRows", "10")
	                .queryParam("dataType", "JSON")
	                .queryParam("ftype", "ODAM")
	                .queryParam("basedatetime", base_date + base_time).toUriString();
    	}else if(serviceName.equals("UltraSrtNcst") || serviceName.equals("UltraSrtFcst")){										//초단기실황, 초단기예보 호출일때 파라미터 설정 
    		
	        builder = UriComponentsBuilder.fromUriString(endPointURI)
	                .queryParam("base_date", base_date)
	                .queryParam("base_time", base_time)
	                .queryParam("pageNo", "1")
	                .queryParam("numOfRows", "60")
	                .queryParam("dataType", "JSON")
	                .queryParam("nx", "57")
	                .queryParam("ny", "128").toUriString();
	        
    	}else if(serviceName.equals("VilageFcst")) {
    		
    		builder = UriComponentsBuilder.fromUriString(endPointURI)
	                .queryParam("base_date", base_date)
	                .queryParam("base_time", base_time)
	                .queryParam("pageNo", "1")
	                .queryParam("numOfRows", "1000")
	                .queryParam("dataType", "JSON")
	                .queryParam("nx", "57")
	                .queryParam("ny", "128").toUriString();
    	}
    	
    	URL url = new URL(builder + serviceKey);
        System.out.println("호출주소 : " + url.toString());
        
        return url;
    }
    
    public String getJsonStreamFromApi(HttpURLConnection connection) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder sb = new StringBuilder();
		
		String temp = "";
       
		while((temp = br.readLine()) != null) {
            sb.append(temp);
        }
		
		br.close();
		
		return sb.toString();
	}
}
