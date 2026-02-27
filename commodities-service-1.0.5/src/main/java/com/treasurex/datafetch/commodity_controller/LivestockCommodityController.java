package com.treasurex.datafetch.commodity_controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.commodity_service.LivestockCommodityApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Livestock commodity APIs.
 */
@RestController
@RequestMapping("/api/livestock")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Livestock Commodities", description = "APIs for fetching livestock commodity data")
public class LivestockCommodityController {

	private final LivestockCommodityApiService livestockCommodityApiService;

	/**
	 * Fetch cheese futures commodity data.
	 */
	@GetMapping("/cheese-futures")
	@Operation(summary = "Get cheese futures data")
	public Object getCheeseFutures() {
		log.debug("Fetching cheese futures data");
		return livestockCommodityApiService.getCheeseFutures();
	}

	/**
	 * Fetch milk futures commodity data.
	 */
	@GetMapping("/milk-futures")
	@Operation(summary = "Get milk futures data")
	public Object getMilkFutures() {
		log.debug("Fetching milk futures data");
		return livestockCommodityApiService.getMilkFutures();
	}

	/**
	 * Fetch live cattle commodity data.
	 */
	@GetMapping("/live-cattle")
	@Operation(summary = "Get live cattle data")
	public Object getLiveCattle() {
		log.debug("Fetching live cattle data");
		return livestockCommodityApiService.getLiveCattle();
	}

	/**
	 * Fetch feeder cattle commodity data.
	 */
	@GetMapping("/feeder-cattle")
	@Operation(summary = "Get feeder cattle data")
	public Object getFeederCattle() {
		log.debug("Fetching feeder cattle data");
		return livestockCommodityApiService.getFeederCattle();
	}

	/**
	 * Fetch lean hogs commodity data.
	 */
	@GetMapping("/lean-hogs")
	@Operation(summary = "Get lean hogs data")
	public Object getLeanHogs() {
		log.debug("Fetching lean hogs data");
		return livestockCommodityApiService.getLeanHogs();
	}
}
