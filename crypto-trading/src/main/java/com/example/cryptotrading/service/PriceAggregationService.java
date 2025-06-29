package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.BinanceTickerDto;
import com.example.cryptotrading.dto.HuobiResponseDto;
import com.example.cryptotrading.dto.HuobiTickerDto;
import com.example.cryptotrading.entity.AggregatedPrice;
import com.example.cryptotrading.repository.AggregatedPriceRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class PriceAggregationService {
    private final AggregatedPriceRepository repo;
    private final RestTemplate rest = new RestTemplate();

    public PriceAggregationService(AggregatedPriceRepository repo) {
        this.repo = repo;
    }

    /**
     * Scheduled: every 10 seconds, fetch ask/bid prices from Binance api and Huobi api,
     * aggregate into AggregatedPrice objects, and save them.
     */
    @Scheduled(fixedRate = 10_000)
    public void fetchAndStore() {
        List<String> symbols = List.of("ETHUSDT", "BTCUSDT");
        for (String symbol : symbols) {
            try {
                BigDecimal bestAsk = getBestAsk(symbol);
                BigDecimal bestBid = getBestBid(symbol);

                AggregatedPrice p = new AggregatedPrice();
                p.setSymbol(symbol);
                p.setAskPrice(bestAsk);
                p.setBidPrice(bestBid);
                p.setTimestamp(Instant.now());
                repo.save(p);
            } catch (Exception e) {
                System.err.println("Error fetching " + symbol + ": " + e.getMessage());
            }
        }
    }

    /**
     * Returns the best ask price between Binance and Huobi for BUY
     */
    public BigDecimal getBestAsk(String symbol) {
        return fetchBinanceAsk(symbol).min(fetchHuobiAsk(symbol));
    }

    /**
     * Returns the best bid price between Binance and Huobi for SELL
     */
    public BigDecimal getBestBid(String symbol) {
        return fetchBinanceBid(symbol).max(fetchHuobiBid(symbol));
    }

    // ─── BINANCE ──────────────────────────────────────────────────────────────

    /**
     * Price aggregation from the source below:
     * Binance
     * Url : https://api.binance.com/api/v3/ticker/bookTicker
     */
    private BinanceTickerDto findBinanceTicker(String symbol) {
        String url = "https://api.binance.com/api/v3/ticker/bookTicker";
        ResponseEntity<List<BinanceTickerDto>> resp = rest.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return resp.getBody()
                .stream()
                .filter(t -> symbol.equals(t.getSymbol()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Binance: not found " + symbol));
    }

    private BigDecimal fetchBinanceAsk(String symbol) {
        return findBinanceTicker(symbol).getAskPrice();
    }

    private BigDecimal fetchBinanceBid(String symbol) {
        return findBinanceTicker(symbol).getBidPrice();
    }

    /**
     * Price aggregation from the source below:
     * Houbi
     * Url : https://api.huobi.pro/market/tickers
     */
    private HuobiTickerDto findHuobiTicker(String symbol) {
        String url = "https://api.huobi.pro/market/tickers";
        HuobiResponseDto resp = rest.getForObject(url, HuobiResponseDto.class);
        return resp.getData().stream()
                .filter(t -> symbol.toLowerCase().equals(t.getSymbol()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Huobi: not found " + symbol));
    }

    private BigDecimal fetchHuobiAsk(String symbol) {
        return findHuobiTicker(symbol).getAsk();
    }

    private BigDecimal fetchHuobiBid(String symbol) {
        return findHuobiTicker(symbol).getBid();
    }
}
