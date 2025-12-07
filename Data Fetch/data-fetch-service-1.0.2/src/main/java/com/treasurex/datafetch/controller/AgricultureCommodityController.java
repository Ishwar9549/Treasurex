package com.treasurex.datafetch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.service.AgricultureCommodityApiService;

@RestController
@RequestMapping("/api/agriculture")
public class AgricultureCommodityController {

	@Autowired
	private AgricultureCommodityApiService agricultureCommodityApiService;

	@GetMapping("/wheat")
	public Object getWheat() {
		return agricultureCommodityApiService.getWheat();
	}

	@GetMapping("/corn")
	public Object getCorn() {
		return agricultureCommodityApiService.getCorn();
	}

	@GetMapping("/soybean")
	public Object getSoybean() {
		return agricultureCommodityApiService.getSoybean();
	}

	@GetMapping("/coffee")
	public Object getCoffee() {
		return agricultureCommodityApiService.getCoffee();
	}

	@GetMapping("/sugar")
	public Object getSugar() {
		return agricultureCommodityApiService.getSugar();
	}

	@GetMapping("/cotton")
	public Object getCotton() {
		return agricultureCommodityApiService.getCotton();
	}

	@GetMapping("/cocoa")
	public Object getCocoa() {
		return agricultureCommodityApiService.getCocoa();
	}
}
