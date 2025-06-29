package com.example.cryptotrading.controller;

import com.example.cryptotrading.entity.AggregatedPrice;
import com.example.cryptotrading.service.TradingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prices")
public class PriceController {
    private final TradingService tradingService;

    public PriceController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    /**
     * an api to retrieve the latest best aggregated price.
     * @param symbol the trading pair symbol: "ETHUSDT" or "BTCUSDT"
     * @return AggregatedPrice object containing price, symbol, and timestamp
     */
    @GetMapping("/latest")
    public AggregatedPrice getLatestPrice(@RequestParam String symbol) {
        return tradingService.getLatestPrice(symbol);
    }
}
