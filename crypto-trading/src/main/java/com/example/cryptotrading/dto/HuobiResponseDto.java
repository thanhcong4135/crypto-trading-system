package com.example.cryptotrading.dto;

import lombok.Data;

import java.util.List;

@Data
public class HuobiResponseDto {
    private String status;
    private long ts;
    private List<HuobiTickerDto> data;
}
