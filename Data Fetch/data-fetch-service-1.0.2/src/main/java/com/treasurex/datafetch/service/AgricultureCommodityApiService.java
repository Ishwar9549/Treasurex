package com.treasurex.datafetch.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Service to fetch Agriculture commodity prices from Twelve Data API.
// API used: https://api.twelvedata.com/time_series
// All values are converted to INR and interval is 1 day.
@Service
public class AgricultureCommodityApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String API_KEY = "2e640904e0024e1a8928e8d0294a071e"; // TwelveData key

	private final String BASE_URL = "https://api.twelvedata.com/time_series?symbol=";

	// Common fetch method
	private Object fetch(String symbol) {

		// TwelveData time_series for 1 day interval and INR
		String url = BASE_URL + symbol + "&interval=1day&apikey=" + API_KEY + "&convert_to=INR";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

		return response.getBody();
	}

	public Object getWheat() {
		return fetch("W_1"); // Wheat futures
	}

	public Object getCorn() {
		return fetch("C_1"); // Corn futures
	}

	public Object getSoybean() {
		return fetch("S_1"); // Soybean futures
	}

	public Object getCoffee() {
		return fetch("KC1"); // Coffee futures
	}

	public Object getSugar() {
		return fetch("SB1"); // Sugar futures
	}

	public Object getCotton() {
		return fetch("CT1"); // Cotton futures
	}

	public Object getCocoa() {
		return fetch("CC1"); // Cocoa futures
	}
}
