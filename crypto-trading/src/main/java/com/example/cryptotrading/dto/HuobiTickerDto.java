package com.example.cryptotrading.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HuobiTickerDto {
    private String symbol;
    private BigDecimal bid;
    private BigDecimal bidSize;
    private BigDecimal ask;
    private BigDecimal askSize;
}
