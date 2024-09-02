package com.youyk.aggregatorservice.dto;


import com.youyk.aggregatorservice.domain.Ticker;
import lombok.Builder;

@Builder
public record Holding(
        Ticker ticker,
        Integer quantity
) {
}
