package com.youyk.aggregatorservice.dto;

import com.youyk.aggregatorservice.domain.Ticker;
import com.youyk.aggregatorservice.domain.TradeAction;

public record TradeRequest(
        Ticker ticker,
        TradeAction action,
        Integer quantity
) {
}
