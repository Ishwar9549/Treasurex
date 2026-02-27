package com.treasurex.datafetch.commodity_controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.commodity_service.EnergyCommodityApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Energy commodity APIs.
 */
@RestController
@RequestMapping("/api/energy")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Energy Commodities", description = "APIs for fetching energy commodity data")
public class EnergyCommodityController {

	private final EnergyCommodityApiService energyCommodityApiService;

	/**
	 * Fetch crude oil commodity data.
	 */
	@GetMapping("/crude-oil")
	@Operation(summary = "Get crude oil data")
	public Object getCrudeOil() {
		log.debug("Fetching crude oil commodity data");
		return energyCommodityApiService.getCrudeOil();
	}

	/**
	 * Fetch brent oil commodity data.
	 */
	@GetMapping("/brent-oil")
	@Operation(summary = "Get brent oil data")
	public Object getBrentOil() {
		log.debug("Fetching brent oil commodity data");
		return energyCommodityApiService.getBrentOil();
	}

	/**
	 * Fetch natural gas commodity data.
	 */
	@GetMapping("/natural-gas")
	@Operation(summary = "Get natural gas data")
	public Object getNaturalGas() {
		log.debug("Fetching natural gas commodity data");
		return energyCommodityApiService.getNaturalGas();
	}

	/**
	 * Fetch heating oil commodity data.
	 */
	@GetMapping("/heating-oil")
	@Operation(summary = "Get heating oil data")
	public Object getHeatingOil() {
		log.debug("Fetching heating oil commodity data");
		return energyCommodityApiService.getHeatingOil();
	}

	/**
	 * Fetch gasoline commodity data.
	 */
	@GetMapping("/gasoline")
	@Operation(summary = "Get gasoline data")
	public Object getGasoline() {
		log.debug("Fetching gasoline commodity data");
		return energyCommodityApiService.getGasoline();
	}

	/**
	 * Fetch ethanol commodity data.
	 */
	@GetMapping("/get-ethanol")
	@Operation(summary = "Get ethanol data")
	public Object getEthanol() {
		log.debug("Fetching ethanol commodity data");
		return energyCommodityApiService.getEthanol();
	}

	/**
	 * Fetch uranium commodity data.
	 */
	@GetMapping("/get-uranium")
	@Operation(summary = "Get uranium data")
	public Object getUranium() {
		log.debug("Fetching uranium commodity data");
		return energyCommodityApiService.getUranium();
	}
}
//END