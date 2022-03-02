package com.weather.demo.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public interface WeatherAPI {
	
	HttpURLConnection getApiConnection(URL url) throws IOException;
	URL buildURL(String serviceName) throws MalformedURLException;
	String getJsonStreamFromApi(HttpURLConnection connection) throws IOException;
	
}
