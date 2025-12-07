package com.treasurex.datafetch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.service.MetalCommodityApiService;

@RestController
@RequestMapping("/api/metal")
public class MetalCommodityController {

	@Autowired
	private MetalCommodityApiService metalCommodityApiService;

	// get gold price
	@GetMapping("/gold")
	public Object getGold() {
		return metalCommodityApiService.getGoldRate();
	}

	// get silver price
	@GetMapping("/silver")
	public Object getSilver() {
		return metalCommodityApiService.getSilverRate();
	}

	// get platinum price
	@GetMapping("/platinum")
	public Object getPlatinum() {
		return metalCommodityApiService.getPlatinumRate();
	}

	// get palladium price
	@GetMapping("/palladium")
	public Object getPalladium() {
		return metalCommodityApiService.getPalladiumRate();
	}
}
