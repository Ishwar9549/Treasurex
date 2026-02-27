package com.treasurex.datafetch.commodity_service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to fetch Metal commodity prices using GoldAPI.
 * API used: https://www.goldapi.io
 */
@Service
@Slf4j
public class MetalCommodityApiService {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String API_KEY = "goldapi-4qy1smisgl24g-io"; // GoldAPI key
	private final String BASE_URL = "https://www.goldapi.io/api/";

	/**
	 * Generic method to fetch metal price.
	 */
	private Object fetch(String symbol) {
		String url = BASE_URL + symbol;

		log.debug("Fetching metal price for symbol: {}", symbol);

		HttpHeaders headers = new HttpHeaders();
		headers.set("x-access-token", API_KEY);
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
		return response.getBody();
	}

	// --- Precious metals ---

	public Object getGoldRate() {
		log.debug("Fetching Gold (XAU/INR) price");
		return fetch("XAU/INR");
	}

	public Object getSilverRate() {
		log.debug("Fetching Silver (XAG/INR) price");
		return fetch("XAG/INR");
	}

	public Object getPlatinumRate() {
		log.debug("Fetching Platinum (XPT/INR) price");
		return fetch("XPT/INR");
	}

	public Object getPalladiumRate() {
		log.debug("Fetching Palladium (XPD/INR) price");
		return fetch("XPD/INR");
	}

	public Object getRhodiumRate() {
		log.debug("Fetching Rhodium (XRD/INR) price");
		return fetch("XRD/INR");
	}

	// --- Base/industrial metals ---

	public Object getCopperRate() {
		log.debug("Fetching Copper (CU/INR) price");
		return fetch("CU/INR");
	}

	public Object getAluminumRate() {
		log.debug("Fetching Aluminum (AL/INR) price");
		return fetch("AL/INR");
	}

	public Object getLeadRate() {
		log.debug("Fetching Lead (PB/INR) price");
		return fetch("PB/INR");
	}

	public Object getZincRate() {
		log.debug("Fetching Zinc (ZN/INR) price");
		return fetch("ZN/INR");
	}

	public Object getNickelRate() {
		log.debug("Fetching Nickel (NI/INR) price");
		return fetch("NI/INR");
	}

	public Object getTinRate() {
		log.debug("Fetching Tin (SN/INR) price");
		return fetch("SN/INR");
	}

	public Object getSteelRate() {
		log.debug("Fetching Steel (STEEL/INR) price");
		return fetch("STEEL/INR");
	}
}
