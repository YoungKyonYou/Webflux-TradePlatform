package com.youyk.customerservice.dto;

import com.youyk.customerservice.domain.Ticker;
import lombok.Builder;

@Builder
public record Holding(
        Ticker ticker,
        Integer quantity
) {
}
