package com.youyk.aggregatorservice.dto;


import com.youyk.aggregatorservice.domain.Ticker;
import com.youyk.aggregatorservice.domain.TradeAction;
import lombok.Builder;

@Builder
public record StockTradeResponse(
        Integer customerId,
        Ticker ticker,
        Integer price,
        Integer quantity,
        TradeAction action,
        Integer totalPrice,
        Integer balance
) {
}
