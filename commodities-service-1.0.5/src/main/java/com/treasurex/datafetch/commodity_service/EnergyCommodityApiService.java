package com.treasurex.datafetch.commodity_service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to fetch Energy commodity prices from Twelve Data API. API used:
 * https://api.twelvedata.com/time_series All values are converted to INR with a
 * 1-day interval.
 */
@Service
@Slf4j
public class EnergyCommodityApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String API_KEY = "2e640904e0024e1a8928e8d0294a071e"; // TwelveData API key
	private final String BASE_URL = "https://api.twelvedata.com/time_series?symbol=";

	/**
	 * Generic method to fetch daily energy commodity prices in INR.
	 */
	private Object fetch(String symbol) {
		String url = BASE_URL + symbol + "&interval=1day&apikey=" + API_KEY + "&convert_to=INR";

		log.debug("Fetching energy commodity data for symbol: {}", symbol);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
		return response.getBody();
	}

	// --- Energy commodity methods ---

	public Object getCrudeOil() {
		log.debug("Fetching Crude Oil (CL) prices");
		return fetch("CL");
	}

	public Object getBrentOil() {
		log.debug("Fetching Brent Crude Oil (BZ) prices");
		return fetch("BZ");
	}

	public Object getNaturalGas() {
		log.debug("Fetching Natural Gas (NG) prices");
		return fetch("NG");
	}

	public Object getHeatingOil() {
		log.debug("Fetching Heating Oil (HO) prices");
		return fetch("HO");
	}

	public Object getGasoline() {
		log.debug("Fetching Gasoline (RB) prices");
		return fetch("RB");
	}

	public Object getEthanol() {
		log.debug("Fetching Ethanol (EH) prices");
		return fetch("EH");
	}

	public Object getUranium() {
		log.debug("Fetching Uranium (UX) prices");
		return fetch("UX");
	}
}
