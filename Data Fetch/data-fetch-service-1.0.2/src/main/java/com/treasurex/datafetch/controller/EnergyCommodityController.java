package com.treasurex.datafetch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.service.EnergyCommodityApiService;

@RestController
@RequestMapping("/api/energy")
public class EnergyCommodityController {

	@Autowired
	private EnergyCommodityApiService energyCommodityApiService;

	@GetMapping("/crude-oil")
	public Object getCrudeOil() {
		return energyCommodityApiService.getCrudeOil();
	}

	@GetMapping("/brent-oil")
	public Object getBrentOil() {
		return energyCommodityApiService.getBrentOil();
	}

	@GetMapping("/natural-gas")
	public Object getNaturalGas() {
		return energyCommodityApiService.getNaturalGas();
	}

	@GetMapping("/heating-oil")
	public Object getHeatingOil() {
		return energyCommodityApiService.getHeatingOil();
	}

	@GetMapping("/gasoline")
	public Object getGasoline() {
		return energyCommodityApiService.getGasoline();
	}
	
	@GetMapping("/get-ethanol")
	public Object getEthanol() {
		return energyCommodityApiService.getEthanol();
	}
	
	@GetMapping("/get-uranium")
	public Object getUranium() {
		return energyCommodityApiService.getUranium();
	}
}
