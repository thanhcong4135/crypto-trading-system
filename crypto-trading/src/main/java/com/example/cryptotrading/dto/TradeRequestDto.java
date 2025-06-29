package com.example.cryptotrading.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRequestDto {
    private String symbol;
    private String command;
    private BigDecimal quantity;
}
