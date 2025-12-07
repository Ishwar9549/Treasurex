package com.treasurex.datafetch.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EnergyCommodityApiService {

	private final RestTemplate restTemplate = new RestTemplate();

	// Your TwelveData API key
	private final String API_KEY = "2e640904e0024e1a8928e8d0294a071e";

	// Common base URL for daily candles
	private final String BASE_URL = "https://api.twelvedata.com/time_series?symbol=";

	// Generic method to fetch daily energy commodity prices in INR.
	private Object fetch(String symbol) {

		String url = BASE_URL + symbol + "&interval=1day&apikey=" + API_KEY + "&convert_to=INR";

		HttpHeaders headers = new HttpHeaders();

		headers.set("Content-Type", "application/json");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

		return response.getBody();
	}

	// Crude Oil (CL) - Most used commodity globally
	public Object getCrudeOil() {
		return fetch("CL");
	}

	// Brent Crude Oil (BZ) - International benchmark
	public Object getBrentOil() {
		return fetch("BZ");
	}

	// Natural Gas (NG)
	public Object getNaturalGas() {
		return fetch("NG");
	}

	// Heating Oil (HO)
	public Object getHeatingOil() {
		return fetch("HO");
	}

	// Gasoline (RB)
	public Object getGasoline() {
		return fetch("RB");
	}

	// Ethanol EH
	public Object getEthanol() {
		return fetch("EH");
	}

	// Uranium UX
	public Object getUranium() {
		return fetch("UX");
	}
}
