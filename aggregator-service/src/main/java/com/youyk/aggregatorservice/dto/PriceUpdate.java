package com.youyk.aggregatorservice.dto;

import com.youyk.aggregatorservice.domain.Ticker;

import java.time.LocalDateTime;

public record PriceUpdate(
        Ticker ticker,
        Integer price,
        LocalDateTime time
) {
}
