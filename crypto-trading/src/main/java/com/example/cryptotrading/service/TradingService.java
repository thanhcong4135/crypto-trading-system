package com.example.cryptotrading.service;

import com.example.cryptotrading.entity.AggregatedPrice;
import com.example.cryptotrading.entity.TradeTransaction;
import com.example.cryptotrading.entity.Wallet;
import com.example.cryptotrading.repository.AggregatedPriceRepository;
import com.example.cryptotrading.repository.TransactionRepository;
import com.example.cryptotrading.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class TradingService {
    private final AggregatedPriceRepository priceRepo;
    private final WalletRepository walletRepo;
    private final TransactionRepository txRepo;

    public TradingService(AggregatedPriceRepository p,
                          WalletRepository w,
                          TransactionRepository t) {
        this.priceRepo = p;
        this.walletRepo = w;
        this.txRepo = t;
    }

    /**
     * step 1: validate symbol
     * Only support Ethereum - ETHUSDT and Bitcoin - BTCUSDT pairs of crypto trading
     */
    private void validateSymbol(String symbol) {
        if (!"ETHUSDT".equals(symbol) && !"BTCUSDT".equals(symbol)) {
            throw new IllegalArgumentException("Unsupported pair: " + symbol);
        }
    }

    /**
     * step 2: retrieve the latest price.
     */
    public AggregatedPrice getLatestPrice(String symbol) {
        return priceRepo.findTopBySymbolOrderByTimestampDesc(symbol)
                .orElseThrow(() -> new RuntimeException("No price for " + symbol));
    }

    /**
     * step 3–>6: trade
     */
    @Transactional
    public TradeTransaction trade(Long userId, String symbol, String command, BigDecimal qty) {
        validateSymbol(symbol);
        AggregatedPrice p = getLatestPrice(symbol);

        // step 3: choose execPrice (hint: Ask for BUY, Bid fpr SELL).
        BigDecimal execPrice;
        if ("BUY".equals(command)) {
            execPrice = p.getAskPrice();
        } else if ("SELL".equals(command)) {
            execPrice = p.getBidPrice();
        } else {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        // step 4: get or create wallets
        Wallet usdt = getOrCreateWallet(userId, "USDT");
        String coinCode = symbol.substring(0, symbol.length() - 4); // "ETH"/"BTC"
        Wallet coin = getOrCreateWallet(userId, coinCode);

        // step 5: update balance
        if ("BUY".equals(command)) {
            BigDecimal cost = execPrice.multiply(qty);
            if (usdt.getBalance().compareTo(cost) < 0) {
                throw new RuntimeException("Insufficient USDT");
            }
            usdt.setBalance(usdt.getBalance().subtract(cost));
            coin.setBalance(coin.getBalance().add(qty));
        } else {
            if (coin.getBalance().compareTo(qty) < 0) {
                throw new RuntimeException("Insufficient " + coinCode);
            }
            coin.setBalance(coin.getBalance().subtract(qty));
            usdt.setBalance(usdt.getBalance().add(execPrice.multiply(qty)));
        }
        walletRepo.saveAll(List.of(usdt, coin));

        // step 6: save transaction
        TradeTransaction tx = new TradeTransaction();
        tx.setUserId(userId);
        tx.setSymbol(symbol);
        tx.setCommand(command);
        tx.setQuantity(qty);
        tx.setPrice(execPrice);
        tx.setTimestamp(Instant.now());
        return txRepo.save(tx);
    }

    /**
     * get an existing Wallet or creates a new one with zero balance if not found.
     */
    private Wallet getOrCreateWallet(Long userId, String currency) {
        return walletRepo.findByUserIdAndCurrency(userId, currency)
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUserId(userId);
                    w.setCurrency(currency);
                    w.setBalance(BigDecimal.ZERO);
                    return walletRepo.save(w);
                });
    }

    /**
     * get history transaction
     */
    public List<TradeTransaction> history(Long userId) {
        return txRepo.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * get wallet
     */
    public List<Wallet> getWallet(Long userId) {
        return walletRepo.findByUserId(userId);
    }
}
