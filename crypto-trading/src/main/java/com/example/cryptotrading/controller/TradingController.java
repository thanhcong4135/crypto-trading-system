package com.example.cryptotrading.controller;

import com.example.cryptotrading.dto.TradeRequestDto;
import com.example.cryptotrading.entity.TradeTransaction;
import com.example.cryptotrading.service.TradingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradingController {
    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    /**
     * an api which allows users to trade based on the latest best aggregated
     * Create BUY/SELL orders based on latest price.
     * POST /api/trades
     * Body: { "symbol":"ETHUSDT","command":"BUY","quantity":1 }
     * Success: 201 Created with the TradeTransaction in the response body.
     * Errors:
     * - 400 Bad Request: invalid input parameters
     * - 409 Conflict: trade failed due to insufficient funds or other issues
     */
    @PostMapping
    public ResponseEntity<?> trade(@RequestBody TradeRequestDto req) {
        try {
            TradeTransaction tx = tradingService.trade(
                    1L,
                    req.getSymbol(),
                    req.getCommand(),
                    req.getQuantity()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(tx);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * An api to retrieve the user trading history.
     * GET /api/trades
     */
    @GetMapping
    public List<TradeTransaction> getHistory() {
        return tradingService.history(1L);
    }
}
