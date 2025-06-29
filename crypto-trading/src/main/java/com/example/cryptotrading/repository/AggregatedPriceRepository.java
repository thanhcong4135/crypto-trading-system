package com.example.cryptotrading.repository;

import com.example.cryptotrading.entity.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Long> {
    Optional<AggregatedPrice> findTopBySymbolOrderByTimestampDesc(String symbol);
}
