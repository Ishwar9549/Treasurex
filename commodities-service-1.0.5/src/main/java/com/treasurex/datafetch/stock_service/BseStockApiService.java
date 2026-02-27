package com.treasurex.datafetch.stock_service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to fetch BSE stock data using Twelve Data API.
 * API used: https://api.twelvedata.com
 * All values returned in INR with 1-day interval.
 */
@Service
@Slf4j
public class BseStockApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String API_KEY = "2e640904e0024e1a8928e8d0294a071e"; // TwelveData API key
	private final String BASE_URL = "https://api.twelvedata.com/time_series?symbol=";

	/**
	 * Generic method to fetch stock data from TwelveData.
	 */
	private Object fetch(String symbol) {
		String url = BASE_URL + symbol + "&interval=1day&apikey=" + API_KEY + "&convert_to=INR";

		log.debug("Fetching BSE stock data for symbol: {}", symbol);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
		return response.getBody();
	}

	// --- Index / Market data ---

	public Object getSensex() {
		log.debug("Fetching SENSEX index data");
		// TODO: replace "" with actual symbol for SENSEX in TwelveData API
		return fetch("SENSEX");
	}

	public Object getGainers() {
		log.debug("Fetching BSE top gainers");
		// TODO: replace "" with actual symbol or endpoint
		return fetch("BSE_GAINERS");
	}

	public Object getLosers() {
		log.debug("Fetching BSE top losers");
		// TODO: replace "" with actual symbol or endpoint
		return fetch("BSE_LOSERS");
	}

	// --- Individual stock quote ---

	public Object getStockQuote(String symbol) {
		log.debug("Fetching stock quote for symbol: {}", symbol);
		return fetch(symbol);
	}
}
