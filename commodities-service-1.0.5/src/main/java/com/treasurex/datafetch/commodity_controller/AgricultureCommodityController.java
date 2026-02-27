package com.treasurex.datafetch.commodity_controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.commodity_service.AgricultureCommodityApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Agriculture commodity APIs.
 */
@RestController
@RequestMapping("/api/agriculture")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Agriculture Commodities", description = "APIs for fetching agriculture commodity data")
public class AgricultureCommodityController {

	private final AgricultureCommodityApiService agricultureCommodityApiService;

	/**
	 * Fetch wheat commodity data.
	 */
	@GetMapping("/wheat")
	@Operation(summary = "Get wheat data")
	public Object getWheat() {
		log.debug("Fetching wheat commodity data");
		return agricultureCommodityApiService.getWheat();
	}

	/**
	 * Fetch corn commodity data.
	 */
	@GetMapping("/corn")
	@Operation(summary = "Get corn data")
	public Object getCorn() {
		log.debug("Fetching corn commodity data");
		return agricultureCommodityApiService.getCorn();
	}

	/**
	 * Fetch soybean commodity data.
	 */
	@GetMapping("/soybean")
	@Operation(summary = "Get soybean data")
	public Object getSoybean() {
		log.debug("Fetching soybean commodity data");
		return agricultureCommodityApiService.getSoybean();
	}

	/**
	 * Fetch coffee commodity data.
	 */
	@GetMapping("/coffee")
	@Operation(summary = "Get coffee data")
	public Object getCoffee() {
		log.debug("Fetching coffee commodity data");
		return agricultureCommodityApiService.getCoffee();
	}

	/**
	 * Fetch sugar commodity data.
	 */
	@GetMapping("/sugar")
	@Operation(summary = "Get sugar data")
	public Object getSugar() {
		log.debug("Fetching sugar commodity data");
		return agricultureCommodityApiService.getSugar();
	}

	/**
	 * Fetch cotton commodity data.
	 */
	@GetMapping("/cotton")
	@Operation(summary = "Get cotton data")
	public Object getCotton() {
		log.debug("Fetching cotton commodity data");
		return agricultureCommodityApiService.getCotton();
	}

	/**
	 * Fetch cocoa commodity data.
	 */
	@GetMapping("/cocoa")
	@Operation(summary = "Get cocoa data")
	public Object getCocoa() {
		log.debug("Fetching cocoa commodity data");
		return agricultureCommodityApiService.getCocoa();
	}
}
//END