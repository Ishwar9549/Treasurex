package com.treasurex.datafetch.commodity_service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to fetch Livestock commodity prices using Twelve Data API.
 * All values are returned in INR with a 1-day interval.
 */
@Service
@Slf4j
public class LivestockCommodityApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String API_KEY = "2e640904e0024e1a8928e8d0294a071e"; // TwelveData Key
	private final String BASE_URL = "https://api.twelvedata.com/time_series?symbol=";

	/**
	 * Generic method to fetch livestock commodity data.
	 */
	private Object fetch(String symbol) {
		String url = BASE_URL + symbol + "&interval=1day&apikey=" + API_KEY + "&convert_to=INR";

		log.debug("Fetching livestock commodity data for symbol: {}", symbol);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
		return response.getBody();
	}

	// --- Livestock commodity methods ---

	public Object getCheeseFutures() {
		log.debug("Fetching Cheese Futures (CHE)");
		return fetch("CHE");
	}

	public Object getMilkFutures() {
		log.debug("Fetching Milk Class III Futures (DA)");
		return fetch("DA");
	}

	public Object getLiveCattle() {
		log.debug("Fetching Live Cattle Futures (LC1)");
		return fetch("LC1");
	}

	public Object getFeederCattle() {
		log.debug("Fetching Feeder Cattle Futures (FC1)");
		return fetch("FC1");
	}

	public Object getLeanHogs() {
		log.debug("Fetching Lean Hogs Futures (LH1)");
		return fetch("LH1");
	}
}
