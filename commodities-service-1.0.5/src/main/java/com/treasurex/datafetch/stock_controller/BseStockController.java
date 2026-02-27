package com.treasurex.datafetch.stock_controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.datafetch.stock_service.BseStockApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for BSE (Bombay Stock Exchange) stock APIs.
 */
@RestController
@RequestMapping("/indian-stock/bse")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "BSE Stock", description = "APIs for BSE stock indices, gainers, losers, and quotes")
public class BseStockController {

	private final BseStockApiService bseStockApiService;

	/**
	 * Get current Sensex index value.
	 */
	@GetMapping("/index/sensex")
	@Operation(summary = "Get Sensex index value")
	public Object getSensex() {
		log.debug("Fetching Sensex index value");
		return bseStockApiService.getSensex();
	}

	/**
	 * Get top gaining stocks.
	 */
	@GetMapping("/gainers")
	@Operation(summary = "Get top gainers")
	public Object gainers() {
		log.debug("Fetching top gainers");
		return bseStockApiService.getGainers();
	}

	/**
	 * Get top losing stocks.
	 */
	@GetMapping("/losers")
	@Operation(summary = "Get top losers")
	public Object losers() {
		log.debug("Fetching top losers");
		return bseStockApiService.getLosers();
	}

	/**
	 * Get stock quote by symbol.
	 *
	 * @param symbol stock symbol
	 */
	@GetMapping("/{symbol}")
	@Operation(summary = "Get stock quote by symbol")
	public Object getStock(@PathVariable("symbol") String symbol) {
		log.debug("Fetching stock quote for symbol: {}", symbol);
		return bseStockApiService.getStockQuote(symbol);
	}
}
