package com.example.cryptotrading.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BinanceTickerDto {
    private String symbol;

    private BigDecimal bidPrice;

    private BigDecimal bidQty;

    private BigDecimal askPrice;

    private BigDecimal askQty;
}
