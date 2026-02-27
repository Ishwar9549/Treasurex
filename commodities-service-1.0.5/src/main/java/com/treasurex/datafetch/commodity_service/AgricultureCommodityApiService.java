package com.treasurex.datafetch.commodity_service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to fetch Agriculture commodity prices from Twelve Data API.
 * API used: https://api.twelvedata.com/time_series
 * All values are converted to INR with a 1-day interval.
 */
@Service
@Slf4j
public class AgricultureCommodityApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String API_KEY = "2e640904e0024e1a8928e8d0294a071e"; // TwelveData API key
	private final String BASE_URL = "https://api.twelvedata.com/time_series?symbol=";

	/**
	 * Fetch commodity data from Twelve Data API.
	 */
	private Object fetch(String symbol) {
		String url = BASE_URL + symbol + "&interval=1day&apikey=" + API_KEY + "&convert_to=INR";

		log.debug("Fetching commodity data for symbol: {}", symbol);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
		return response.getBody();
	}

	// --- Agriculture commodity methods ---

	public Object getWheat() {
		log.debug("Fetching Wheat futures");
		return fetch("W_1");
	}

	public Object getCorn() {
		log.debug("Fetching Corn futures");
		return fetch("C_1");
	}

	public Object getSoybean() {
		log.debug("Fetching Soybean futures");
		return fetch("S_1");
	}

	public Object getCoffee() {
		log.debug("Fetching Coffee futures");
		return fetch("KC1");
	}

	public Object getSugar() {
		log.debug("Fetching Sugar futures");
		return fetch("SB1");
	}

	public Object getCotton() {
		log.debug("Fetching Cotton futures");
		return fetch("CT1");
	}

	public Object getCocoa() {
		log.debug("Fetching Cocoa futures");
		return fetch("CC1");
	}
}
