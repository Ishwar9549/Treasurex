package com.treasurex.datafetch.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Service to fetch Livestock commodity prices using Twelve Data API.
// All values returned in INR and 1-day interval.
@Service
public class LivestockCommodityApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String API_KEY = "2e640904e0024e1a8928e8d0294a071e"; // TwelveData Key

	private final String BASE_URL = "https://api.twelvedata.com/time_series?symbol=";

	private Object fetch(String symbol) {

		String url = BASE_URL + symbol + "&interval=1day&apikey=" + API_KEY + "&convert_to=INR";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

		return response.getBody();
	}

	public Object getCheeseFutures() {
		return fetch("CHE"); // Cheese
	}

	public Object getMilkFutures() {
		return fetch("DA"); // Milk Class III
	}

	public Object getLiveCattle() {
		return fetch("LC1"); // Live Cattle
	}

	public Object getFeederCattle() {
		return fetch("FC1"); // Feeder Cattle
	}

	public Object getLeanHogs() {
		return fetch("LH1"); // Lean Hogs
	}
}
