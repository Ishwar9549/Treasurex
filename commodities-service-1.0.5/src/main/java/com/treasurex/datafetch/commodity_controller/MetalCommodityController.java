package com.treasurex.datafetch.commodity_controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.commodity_service.MetalCommodityApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Metal commodity APIs.
 */
@RestController
@RequestMapping("/api/metal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Metal Commodities", description = "APIs for fetching metal commodity data")
public class MetalCommodityController {

	private final MetalCommodityApiService metalCommodityApiService;

	@GetMapping("/gold")
	@Operation(summary = "Get gold price")
	public Object getGold() {
		log.debug("Fetching gold rate");
		return metalCommodityApiService.getGoldRate();
	}

	@GetMapping("/silver")
	@Operation(summary = "Get silver price")
	public Object getSilver() {
		log.debug("Fetching silver rate");
		return metalCommodityApiService.getSilverRate();
	}

	@GetMapping("/platinum")
	@Operation(summary = "Get platinum price")
	public Object getPlatinum() {
		log.debug("Fetching platinum rate");
		return metalCommodityApiService.getPlatinumRate();
	}

	@GetMapping("/palladium")
	@Operation(summary = "Get palladium price")
	public Object getPalladium() {
		log.debug("Fetching palladium rate");
		return metalCommodityApiService.getPalladiumRate();
	}

	@GetMapping("/rhodium")
	@Operation(summary = "Get rhodium price")
	public Object getRhodium() {
		log.debug("Fetching rhodium rate");
		return metalCommodityApiService.getRhodiumRate();
	}

	@GetMapping("/copper")
	@Operation(summary = "Get copper price")
	public Object getCopper() {
		log.debug("Fetching copper rate");
		return metalCommodityApiService.getCopperRate();
	}

	@GetMapping("/aluminum")
	@Operation(summary = "Get aluminum price")
	public Object getAluminum() {
		log.debug("Fetching aluminum rate");
		return metalCommodityApiService.getAluminumRate();
	}

	@GetMapping("/lead")
	@Operation(summary = "Get lead price")
	public Object getLead() {
		log.debug("Fetching lead rate");
		return metalCommodityApiService.getLeadRate();
	}

	@GetMapping("/zinc")
	@Operation(summary = "Get zinc price")
	public Object getZinc() {
		log.debug("Fetching zinc rate");
		return metalCommodityApiService.getZincRate();
	}

	@GetMapping("/nickel")
	@Operation(summary = "Get nickel price")
	public Object getNickel() {
		log.debug("Fetching nickel rate");
		return metalCommodityApiService.getNickelRate();
	}

	@GetMapping("/tin")
	@Operation(summary = "Get tin price")
	public Object getTin() {
		log.debug("Fetching tin rate");
		return metalCommodityApiService.getTinRate();
	}

	@GetMapping("/steel")
	@Operation(summary = "Get steel price")
	public Object getSteel() {
		log.debug("Fetching steel rate");
		return metalCommodityApiService.getSteelRate();
	}
}
