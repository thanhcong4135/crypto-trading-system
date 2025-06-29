package com.example.cryptotrading.scheduler;

import com.example.cryptotrading.service.PriceAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceScheduler {
    private final PriceAggregationService priceAggregationService;

    @Scheduled(fixedRate = 10000)
    public void fetchCryptoPricesEvery10Seconds() {
        priceAggregationService.fetchAndStore();
    }
}
