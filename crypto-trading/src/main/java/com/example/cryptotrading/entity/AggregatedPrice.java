package com.example.cryptotrading.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "aggregated_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedPrice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String symbol;      // "ETHUSDT", "BTCUSDT"

    @Column(name = "bid_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", nullable = false, precision = 19, scale = 8)
    private BigDecimal askPrice;

    @Column(nullable = false)
    private Instant timestamp;
}
