package com.treasurex.datafetch.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MetalCommodityApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String API_KEY = "goldapi-4qy1smisgl24g-io";

	private String BASE_URL = "https://www.goldapi.io/api/";

	//common method to fetch price  
	private Object fetch(String symbol) {

		String url = BASE_URL + symbol;

		HttpHeaders headers = new HttpHeaders();

		headers.set("x-access-token", API_KEY);

		headers.set("Content-Type", "application/json");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

		return response.getBody();
	}

	// Getting Gold Price
	public Object getGoldRate() {
		return fetch("XAU/INR");
	}

	// Getting Silver Price
	public Object getSilverRate() {
		return fetch("XAG/INR");
	}

	// Getting Platinum Price
	public Object getPlatinumRate() {
		return fetch("XPT/INR");
	}

	// Getting Palladium Price
	public Object getPalladiumRate() {
		return fetch("XPD/INR");
	}
}
