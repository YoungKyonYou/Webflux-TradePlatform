package com.youyk.aggregatorservice.dto;

import com.youyk.aggregatorservice.domain.Ticker;

import java.time.LocalDateTime;

public record StockPriceResponse(
        Ticker ticker,
        Integer price
) {
}
