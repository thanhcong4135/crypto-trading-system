package com.example.cryptotrading.controller;

import com.example.cryptotrading.entity.Wallet;
import com.example.cryptotrading.service.TradingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final TradingService tradingService;

    public WalletController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    /**
     * An api to retrieve the user’s crypto currencies wallet balance
     * GET /api/wallet
     * @return a list of Wallet objects, each containing currency code and balance.
     */
    @GetMapping
    public List<Wallet> getWallet() {
        return tradingService.getWallet(1L);
    }
}
