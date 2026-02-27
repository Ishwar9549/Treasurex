package com.treasurex.datafetch.stock_service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to fetch NSE stock data using Twelve Data API. All values returned in
 * INR with 1-day interval.
 */
@Service
@Slf4j
public class NseStockApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String API_KEY = "2e640904e0024e1a8928e8d0294a071e";
	private final String BASE_URL = "https://api.twelvedata.com/time_series?symbol=";

	/**
	 * Generic method to fetch stock/index data from TwelveData.
	 */
	private Object fetch(String symbol) {
		String url = BASE_URL + symbol + "&interval=1day&apikey=" + API_KEY + "&convert_to=INR";

		log.debug("Fetching NSE stock/index data for symbol: {}", symbol);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
		return response.getBody();
	}

	// --- NSE Indices ---

	public Object getNifty50() {
		log.debug("Fetching Nifty 50 (^NSEI)");
		return fetch("^NSEI");
	}

	public Object getBankNifty() {
		log.debug("Fetching Bank Nifty (NIFTY_BANK)");
		return fetch("NIFTY_BANK");
	}

	public Object getNiftyFin() {
		log.debug("Fetching Nifty Financial Services (NIFTY_FIN_SERVICE)");
		return fetch("NIFTY_FIN_SERVICE");
	}

	public Object getNiftyIt() {
		log.debug("Fetching Nifty IT (NIFTY_IT)");
		return fetch("NIFTY_IT");
	}

	public Object getNiftyMidCap() {
		log.debug("Fetching Nifty Midcap 50 (NIFTY_MIDCAP_50)");
		return fetch("NIFTY_MIDCAP_50");
	}

	public Object getNiftyNext50() {
		log.debug("Fetching Nifty Next 50 (NIFTY_NEXT_50)");
		return fetch("NIFTY_NEXT_50");
	}

	// --- NSE Market movers ---

	public Object getGainers() {
		log.debug("Fetching NSE top gainers");
		// TODO: replace "" with actual symbol/endpoint supported by TwelveData
		return fetch("NSE_GAINERS");
	}

	public Object getLosers() {
		log.debug("Fetching NSE top losers");
		// TODO: replace "" with actual symbol/endpoint supported by TwelveData
		return fetch("NSE_LOSERS");
	}

	// --- Live Stock Quote ---

	public Object getStockQuote(String symbol) {
		log.debug("Fetching stock quote for symbol: {}", symbol);
		return fetch(symbol);
	}
}
