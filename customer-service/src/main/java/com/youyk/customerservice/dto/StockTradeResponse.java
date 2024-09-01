package com.youyk.customerservice.dto;

import com.youyk.customerservice.domain.Ticker;
import com.youyk.customerservice.domain.TradeAction;
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
