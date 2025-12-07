package com.treasurex.datafetch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.service.LivestockCommodityApiService;

@RestController
@RequestMapping("/api/livestock")
public class LivestockCommodityController {

	@Autowired
	private LivestockCommodityApiService livestockCommodityApiService;

	@GetMapping("/cheese-futures")
	public Object getCheeseFutures() {
		return livestockCommodityApiService.getCheeseFutures();
	}

	@GetMapping("/milk-futures")
	public Object getMilkFutures() {
		return livestockCommodityApiService.getMilkFutures();
	}

	@GetMapping("/live-cattle")
	public Object getLiveCattle() {
		return livestockCommodityApiService.getLiveCattle();
	}

	@GetMapping("/feeder-cattle")
	public Object getFeederCattle() {
		return livestockCommodityApiService.getFeederCattle();
	}

	@GetMapping("/lean-hogs")
	public Object getLeanHogs() {
		return livestockCommodityApiService.getLeanHogs();
	}
}
