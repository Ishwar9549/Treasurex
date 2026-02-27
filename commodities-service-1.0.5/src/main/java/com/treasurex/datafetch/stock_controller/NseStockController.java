package com.treasurex.datafetch.stock_controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.stock_service.NseStockApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for NSE (National Stock Exchange) stock APIs.
 */
@RestController
@RequestMapping("/indian-stock/nse")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "NSE Stock", description = "APIs for NSE stock indices, gainers, losers, and quotes")
public class NseStockController {

	private final NseStockApiService nseStockApiService;

	@GetMapping("/index/nifty50")
	@Operation(summary = "Get Nifty 50 index value")
	public Object getNifty50() {
		log.debug("Fetching Nifty 50 index value");
		return nseStockApiService.getNifty50();
	}

	@GetMapping("/index/banknifty")
	@Operation(summary = "Get Bank Nifty index value")
	public Object getBankNifty() {
		log.debug("Fetching Bank Nifty index value");
		return nseStockApiService.getBankNifty();
	}

	@GetMapping("/index/niftyfin")
	@Operation(summary = "Get Nifty Financial Services index value")
	public Object getNiftyFin() {
		log.debug("Fetching Nifty Financial Services index value");
		return nseStockApiService.getNiftyFin();
	}

	@GetMapping("/index/niftyIt")
	@Operation(summary = "Get Nifty IT index value")
	public Object getNiftyIt() {
		log.debug("Fetching Nifty IT index value");
		return nseStockApiService.getNiftyIt();
	}

	@GetMapping("/index/nifty-midcap-50")
	@Operation(summary = "Get Nifty Midcap 50 index value")
	public Object getNiftyMidcap() {
		log.debug("Fetching Nifty Midcap 50 index value");
		return nseStockApiService.getNiftyMidCap();
	}

	@GetMapping("/index/nifty-next-50")
	@Operation(summary = "Get Nifty Next 50 index value")
	public Object getNiftyNext50() {
		log.debug("Fetching Nifty Next 50 index value");
		return nseStockApiService.getNiftyNext50();
	}

	@GetMapping("/gainers")
	@Operation(summary = "Get top gainers")
	public Object getGainers() {
		log.debug("Fetching top NSE gainers");
		return nseStockApiService.getGainers();
	}

	@GetMapping("/losers")
	@Operation(summary = "Get top losers")
	public Object getLosers() {
		log.debug("Fetching top NSE losers");
		return nseStockApiService.getLosers();
	}

	@GetMapping("/{symbol}")
	@Operation(summary = "Get stock quote by symbol")
	public Object getStockQuote(@PathVariable("symbol") String symbol) {
		log.debug("Fetching NSE stock quote for symbol: {}", symbol);
		return nseStockApiService.getStockQuote(symbol);
	}
}
